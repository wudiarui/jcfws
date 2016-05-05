package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午2:58</p>
 *
 * @author jerry
 */
public class GroupRelationEntity extends BaseEntity<Long> {

    private Long groupId;
    private Long organizationId;
    private Long userId;
    private Long startUserId;
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
