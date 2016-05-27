package org.jerry.frameworks.system.group.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.GroupRelationEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * 组关系仓库类
 *
 * <p>Date : 16/5/23</p>
 * <p>Time : 上午9:00</p>
 *
 * @author jerry
 */
public interface GroupRelationRepository extends BaseRepository<GroupRelationEntity, Long> {

    GroupRelationEntity findByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * 范围查 如果在指定范围内 就没必要再新增一个 如当前是[10,20] 如果数据库有[9,21] 10<=9 and 21>=20
     *
     * @param groupId           组ID
     * @param startUserId       开始用户ID
     * @param endUserId         结束用户ID
     * @return                  符合条件的组
     */
    GroupRelationEntity findByGroupIdAndStartUserIdLessThanEqualAndEndUserIdGreaterThanEqual(
            Long groupId,
            Long startUserId,
            Long endUserId
    );

    /**
     * 区间删除冗余的用户组关系,因为该用户组包含了在其它用户组中的用户.[小心使用]
     *
     * @param startUserId       开始用户ID
     * @param endUserId         结束用户ID
     */
    @Modifying
    @Query("delete from GroupRelationEntity where (startUserId >= ?1 and endUserId <= ?2) or " +
            "(userId >= ?1 and userId <= ?2)")
    void deleteInRange(Long startUserId, Long endUserId);

    GroupRelationEntity findByGroupIdAndOrganizationId(Long groupId, Long organizationId);

    @Query("select groupId from GroupRelationEntity where userId=?1 or (startUserId <= ?1 and endUserId >= ?1)")
    List<Long> findGroupIds(Long userId);

    @Query("select groupId from GroupRelationEntity where userId=?1 or (startUserId <= ?1 and endUserId >= ?1) or (organizationId in (?2))")
    List<Long> findGroupIds(Long userId, Set<Long> organizationIds);

    @Modifying
    @Query("delete from GroupRelationEntity r where " +
            "not exists (select 1 from GroupEntity g where r.groupId = g.id) or " +
            "not exists (select 1 from OrganizationEntity o where r.organizationId = o.id)")
    void clearDeletedGroupRelation();
}
