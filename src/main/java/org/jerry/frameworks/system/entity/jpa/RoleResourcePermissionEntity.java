package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jpa.BaseEntity;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_role_resource_permission"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_role_resource_permission", schema = "eam")
public class RoleResourcePermissionEntity extends BaseEntity<Long> {

    private Long roleId;
    private Long resourceId;
    private String permissionIds;

    @Basic
    @Column(name = "role_id")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "resource_id")
    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    @Basic
    @Column(name = "permission_ids")
    public String getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(String permissionIds) {
        this.permissionIds = permissionIds;
    }
}
