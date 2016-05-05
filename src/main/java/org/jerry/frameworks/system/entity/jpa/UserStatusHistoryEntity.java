package org.jerry.frameworks.system.entity.jpa;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The class is table <b>{@code "sys_user_status_history"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_user_status_history", schema = "eam")
public class UserStatusHistoryEntity extends BaseEntity<Long> {

    private Long userId;
    private String status;
    private String reason;
    private Long opUserId;
    private Timestamp opDate;

    @Basic
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Basic
    @Column(name = "op_user_id")
    public Long getOpUserId() {
        return opUserId;
    }

    public void setOpUserId(Long opUserId) {
        this.opUserId = opUserId;
    }

    @Basic
    @Column(name = "op_date")
    public Timestamp getOpDate() {
        return opDate;
    }

    public void setOpDate(Timestamp opDate) {
        this.opDate = opDate;
    }
}
