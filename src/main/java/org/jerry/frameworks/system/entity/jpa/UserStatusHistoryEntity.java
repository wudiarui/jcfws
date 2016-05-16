package org.jerry.frameworks.system.entity.jpa;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.system.entity.jpa.emun.UserState;
import org.springframework.format.annotation.DateTimeFormat;


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

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private UserEntity user;

    /**
     * 锁定的用户
     */
    @Enumerated(value = EnumType.STRING)
    private UserState status;

    /**
     * 备注信息
     */
    private String reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "op_user_id")
    private UserEntity opUser;


    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = Constants.DEFAULT_DATE_TIME_PATTERN)
    @Column(name = "op_date")
    private Timestamp opDate;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserState getStatus() {
        return status;
    }

    public void setStatus(UserState status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UserEntity getOpUser() {
        return opUser;
    }

    public void setOpUser(UserEntity opUser) {
        this.opUser = opUser;
    }

    public Timestamp getOpDate() {
        return opDate;
    }

    public void setOpDate(Timestamp opDate) {
        this.opDate = opDate;
    }
}
