package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jpa.BaseEntity;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_group_relation"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_group_relation", schema = "eam")
public class GroupRelationEntity extends BaseEntity<Long> {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "start_user_id")
    private Long startUserId;

    @Column(name = "end_user_id")
    private Long endUserId;



    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }


    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(Long startUserId) {
        this.startUserId = startUserId;
    }


    public Long getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(Long endUserId) {
        this.endUserId = endUserId;
    }

}
