package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jpa.BaseEntity;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_group"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_group", schema = "eam")
public class GroupEntity extends BaseEntity<Long> {

    private String name;
    private String type;
    private Byte isShow;
    private Byte defaultGroup;

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "is_show")
    public Byte getIsShow() {
        return isShow;
    }

    public void setIsShow(Byte isShow) {
        this.isShow = isShow;
    }

    @Basic
    @Column(name = "default_group")
    public Byte getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(Byte defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

}
