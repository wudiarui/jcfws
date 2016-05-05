package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午3:00</p>
 *
 * @author jerry
 */
public class RoleResourcePermissionEntity extends BaseEntity<Long> {

    private Long roleId;
    private Long resourceId;
    private String PermissionIds;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getPermissionIds() {
        return PermissionIds;
    }

    public void setPermissionIds(String permissionIds) {
        PermissionIds = permissionIds;
    }
}
