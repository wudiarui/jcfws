package org.jerry.frameworks.system.entity.jpa;

import org.hibernate.annotations.*;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.repository.support.annotation.EnableQueryCache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The class is table <b>{@code "sys_permission"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_permission", schema = "eam")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PermissionEntity extends BaseEntity<Long> {
    /**
     * 前端显示名称
     */
    private String name;
    /**
     * 系统中验证时使用的权限标识
     */
    private String permission;
    /**
     * 详细描述
     */
    private String description;
    /**
     * 是否显示 也表示是否可用 为了统一 都使用这个
     */
    @Column(name = "is_show")
    private Boolean isShow = Boolean.FALSE;

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
