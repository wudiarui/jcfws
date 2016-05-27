package org.jerry.frameworks.system.permission.biz;

import com.google.common.collect.Sets;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.system.entity.jpa.RoleEntity;
import org.jerry.frameworks.system.entity.jpa.RoleResourcePermissionEntity;
import org.jerry.frameworks.system.permission.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>Date : 16/5/23</p>
 * <p>Time : 下午3:50</p>
 *
 * @author jerry
 */
@Service
public class RoleBiz extends BaseBiz<RoleEntity, Long> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleEntity update(RoleEntity role) {
        List<RoleResourcePermissionEntity> roleResourcePermissionEntities = role.getResourcePermissions();
        for (int i = 0; i < roleResourcePermissionEntities.size(); i++) {
            RoleResourcePermissionEntity localRoleResourcePermission = roleResourcePermissionEntities.get(i);
            localRoleResourcePermission.setRole(role);
            RoleResourcePermissionEntity dbRoleResourcePermission = findRoleResourcePermission(localRoleResourcePermission);
            if (dbRoleResourcePermission != null) {
                dbRoleResourcePermission.setRole(localRoleResourcePermission.getRole());
                dbRoleResourcePermission.setResourceId(localRoleResourcePermission.getResourceId());
                dbRoleResourcePermission.setPermissionIds(localRoleResourcePermission.getPermissionIds());
                roleResourcePermissionEntities.set(i, dbRoleResourcePermission);
            }
        }
        return super.update(role);
    }

    private RoleResourcePermissionEntity findRoleResourcePermission(RoleResourcePermissionEntity roleResourcePermission) {
        return roleRepository.findRoleResourcePermission(
                roleResourcePermission.getRole(), roleResourcePermission.getResourceId());
    }

    /**
     * 获取可用的角色列表
     *
     */
    public Set<RoleEntity> findShowRoles(Set<Long> roleIds) {

        Set<RoleEntity> roles = Sets.newHashSet();

        //TODO 如果角色很多 此处应该写查询
        for (RoleEntity role : findAll()) {
            if (Boolean.TRUE.equals(role.getShow()) && roleIds.contains(role.getId())) {
                roles.add(role);
            }
        }
        return roles;
    }
}
