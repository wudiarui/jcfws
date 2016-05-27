package org.jerry.frameworks.system.permission.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.RoleResourcePermissionEntity;
import org.jerry.frameworks.system.entity.jpa.RoleEntity;
import org.springframework.data.jpa.repository.Query;

/**
 * <p>Date : 16/5/23</p>
 * <p>Time : 下午3:44</p>
 *
 * @author jerry
 */
public interface RoleRepository extends BaseRepository<RoleEntity, Long> {

    @Query("from RoleResourcePermissionEntity where role = ?1 and resourceId = ?2")
    RoleResourcePermissionEntity findRoleResourcePermission(RoleEntity role, Long resourceId);
}
