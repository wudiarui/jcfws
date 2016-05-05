package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_role"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_role", schema = "eam")
public class RoleEntity extends BaseEntity<Long> {

    private String name;
    private String role;
    private String description;
    private Byte isShow;

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "is_show")
    public Byte getIsShow() {
        return isShow;
    }

    public void setIsShow(Byte isShow) {
        this.isShow = isShow;
    }
}
