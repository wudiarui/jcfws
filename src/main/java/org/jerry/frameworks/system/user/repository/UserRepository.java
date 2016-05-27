package org.jerry.frameworks.system.user.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.entity.jpa.UserOrganizationJobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * <p>Date : 16/5/18</p>
 * <p>Time : 上午9:56</p>
 *
 * @author jerry
 */
public interface UserRepository extends BaseRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByMobilePhoneNumber(String mobilePhoneNumber);

    UserEntity findByEmail(String email);

    @Query("from UserOrganizationJobEntity where user=?1 and organizationId=?2 and jobId=?3")
    UserOrganizationJobEntity findUserOrganization(UserEntity user, Long organizationId, Long jobId);

    /**
     * 查找不存在在职务或组织机构表中的关联实体集
     *
     * @param pageable  分页信息
     * @return  职务或组织机构表中没有或不存在的关联实体集
     */
    @Query("select uoj from UserOrganizationJobEntity uoj where" +
            " not exists (select 1 from JobEntity j where uoj.jobId = j.id) or" +
            " not exists (select 1 from OrganizationEntity o where uoj.organizationId = o.id)")
    Page<UserOrganizationJobEntity> findUserOrganizationJobOnNotExistsOrganizationOrJob(Pageable pageable);

    /**
     * 级联删除不存在{@link UserEntity}中的{@link UserOrganizationJobEntity}
     */
    @Modifying
    @Query("delete from UserOrganizationJobEntity uoj where" +
            " not exists (select 1 from UserEntity u where uoj.user = u.id)")
    void deleteUserOrganizationJobOnNotExistsUser();
}
