package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.system.entity.jpa.emun.GroupType;

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

    @Enumerated(EnumType.STRING)
    private GroupType type;

    @Column(name = "is_show")
    private Boolean isShow = Boolean.FALSE;

    @Column(name = "default_group")
    private Boolean defaultGroup = Boolean.FALSE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }

    public Boolean getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(Boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
