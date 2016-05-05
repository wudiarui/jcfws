package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午2:56</p>
 *
 * @author jerry
 */
public class AuthEntity extends BaseEntity<Long> {

    private Long organizationId;
    private Long jobId;
    private Long userId;
    private Long groupId;
    private String roleIds;
    private String type;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
