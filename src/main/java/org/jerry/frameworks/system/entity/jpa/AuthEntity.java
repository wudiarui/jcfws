package org.jerry.frameworks.system.entity.jpa;


import org.jerry.frameworks.base.entity.jpa.BaseEntity;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_auth"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_auth", schema = "eam")
public class AuthEntity extends BaseEntity<Long> {
    private Long organizationId;
    private Long jobId;
    private Long userId;
    private Long groupId;
    private String roleIds;
    private String type;

    @Basic
    @Column(name = "organization_id")
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Basic
    @Column(name = "job_id")
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @Basic
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "group_id")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "role_ids")
    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
