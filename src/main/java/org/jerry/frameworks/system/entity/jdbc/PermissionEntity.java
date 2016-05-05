package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午2:50</p>
 *
 * @author jerry
 */
public class PermissionEntity extends BaseEntity<Long> {

    private String name;
    private String permission;
    private String description;
    private Boolean isShow;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }
}
