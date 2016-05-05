package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_user_organization_job"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_user_organization_job", schema = "eam")
public class UserOrganizationJobEntity extends BaseEntity<Long> {

    private Long userId;
    private Long organizationId;
    private Long jobId;

    @Basic
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
}
