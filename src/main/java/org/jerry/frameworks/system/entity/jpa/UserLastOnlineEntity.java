package org.jerry.frameworks.system.entity.jpa;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>Date : 16/5/16</p>
 * <p>Time : 下午2:56</p>
 *
 * @author jerry
 */
@Entity
@Table(name = "sys_user_last_online", schema = "eam")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserLastOnlineEntity extends BaseEntity<Long> {

    /**
     * 在线的用户
     */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    /**
     * 最后退出时的uid
     */
    private String uid;

    /**
     * 用户主机地址
     */
    @Column(name = "host")
    private String host;


    /**
     * 用户浏览器类型
     */
    @Column(name = "user_agent")
    private String userAgent;

    /**
     * 用户登录时系统IP
     */
    @Column(name = "system_host")
    private String systemHost;

    /**
     * 最后登录时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_login_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTimestamp;

    /**
     * 最后退出时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_stop_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastStopTimestamp;

    /**
     * 登录次数
     */
    @Column(name = "login_count")
    private Integer loginCount = 0;

    /**
     * 总的在线时长（秒为单位）
     */
    @Column(name = "total_online_time")
    private Long totalOnlineTime = 0L;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSystemHost() {
        return systemHost;
    }

    public void setSystemHost(String systemHost) {
        this.systemHost = systemHost;
    }

    public Date getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public void setLastLoginTimestamp(Date lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public Date getLastStopTimestamp() {
        return lastStopTimestamp;
    }

    public void setLastStopTimestamp(Date lastStopTimestamp) {
        this.lastStopTimestamp = lastStopTimestamp;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Long getTotalOnlineTime() {
        return totalOnlineTime;
    }

    public void setTotalOnlineTime(Long totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void incLoginCount() {
        setLoginCount(getLoginCount() + 1);
    }

    public void incTotalOnlineTime() {
        long onlineTime = getLastStopTimestamp().getTime() - getLastLoginTimestamp().getTime();
        setTotalOnlineTime(getTotalOnlineTime() + onlineTime / 1000);
    }

    public final static UserLastOnlineEntity fromUserOnline(UserOnlineEntity online) {
        UserLastOnlineEntity lastOnline = new UserLastOnlineEntity();
        lastOnline.setHost(online.getHost());
        lastOnline.setUserId(online.getUserId());
        lastOnline.setUsername(online.getUsername());
        lastOnline.setUserAgent(online.getUserAgent());
        lastOnline.setSystemHost(online.getSystemHost());
        lastOnline.setUid(String.valueOf(online.getId()));
        lastOnline.setLastLoginTimestamp(online.getStartTimestsamp());
        lastOnline.setLastStopTimestamp(online.getLastAccessTime());
        return lastOnline;
    }

    public static final void merge(UserLastOnlineEntity from, UserLastOnlineEntity to) {
        to.setHost(from.getHost());
        to.setUserId(from.getUserId());
        to.setUsername(from.getUsername());
        to.setUserAgent(from.getUserAgent());
        to.setSystemHost(from.getSystemHost());
        to.setUid(String.valueOf(from.getUid()));
        to.setLastLoginTimestamp(from.getLastLoginTimestamp());
        to.setLastStopTimestamp(from.getLastStopTimestamp());
    }

}
