package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;
import org.omg.CORBA.LocalObject;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午3:02</p>
 *
 * @author jerry
 */
public class UserOrganizationJobEntity extends BaseEntity<Long> {

    private Long userId;
    private Long organizationId;
    private Long jobId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
