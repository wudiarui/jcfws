package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

import java.sql.Timestamp;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午3:03</p>
 *
 * @author jerry
 */
public class UserStatusHistoryEntity extends BaseEntity<Long> {

    private Long userId;
    private String status;
    private String reason;
    private Long opUserId;
    private Timestamp opDate;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOpUserId() {
        return opUserId;
    }

    public void setOpUserId(Long opUserId) {
        this.opUserId = opUserId;
    }

    public Timestamp getOpDate() {
        return opDate;
    }

    public void setOpDate(Timestamp opDate) {
        this.opDate = opDate;
    }
}
