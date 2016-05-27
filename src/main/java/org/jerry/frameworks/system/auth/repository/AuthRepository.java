package org.jerry.frameworks.system.auth.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.AuthEntity;

import java.util.Set;

/**
 * 管理授权的仓库接口
 *
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午2:38</p>
 *
 * @author jerry
 */
public interface AuthRepository extends BaseRepository<AuthEntity, Long> {

    AuthEntity findByUserId(Long userId);

    AuthEntity findByGroupId(Long groupId);

    AuthEntity findByOrganizationIdAndJobId(Long organizationId, Long jobId);

    /**
     * 查找并获得角色列表
     *
     * @param userId                用户ID
     * @param groupIds              用户组ID列表
     * @param organzationIds        组织机构ID列表
     * @param jobIds                职务ID列表
     * @param organzationJobIds     组织机构中职务ID列表
     * @return                      权限列表
     */
    Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organzationIds, Set<Long> jobIds, Set<Long[]> organzationJobIds);
}
