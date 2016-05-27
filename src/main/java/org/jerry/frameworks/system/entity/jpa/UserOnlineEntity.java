package org.jerry.frameworks.system.entity.jpa;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.base.entity.AbstractEntity;
import org.jerry.frameworks.base.repository.hibernate.type.ObjectSerializeUserType;
import org.jerry.frameworks.system.shiro.session.mgt.OnlineSession;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>Date : 16/5/16</p>
 * <p>Time : 下午2:56</p>
 *
 * @author jerry
 */
/*@TypeDef(name = "SetToObjectUserType",
    typeClass = ObjectSerializeUserType.class,
    parameters = {}
)*/
@Entity
@Table(name = "sys_user_online", schema = "eam")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserOnlineEntity extends AbstractEntity<String> {

    @Id
    @GeneratedValue(generator = "assigned")
    @GenericGenerator(name = "assigned", strategy = "assigned")
    private String id;

    /**
     * 在线用户的ID
     */
    @Column(name = "user_id")
    private Long userId = 0L;

    @Column(name = "username")
    private String username;

    /**
     * 用户主机地址
     */
    @Column(name = "host")
    private String host;

    /**
     * 用户登录系统时IP
     */
    @Column(name = "system_host")
    private String systemHost;

    /**
     * 用户浏览器类型
     */
    @Column(name = "user_agent")
    private String userAgent;

    /**
     * 在线状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OnlineSession.OnlineStatus status = OnlineSession.OnlineStatus.on_line;

    @DateTimeFormat(pattern = Constants.DEFAULT_DATE_TIME_PATTERN)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_timestsamp")
    private Date startTimestsamp;

    /**
     * 超时时间
     */
    @Column(name = "timeout")
    private Long timeout;

    /**
     * 备份的当前用户会话
     */
    @Type(type = "org.jerry.frameworks.base.repository.hibernate.type.ObjectSerializeUserType")
    @Column(name = "session")
    private OnlineSession session;

    /**
     * session最后访问时间
     */
    @DateTimeFormat(pattern = Constants.DEFAULT_DATE_TIME_PATTERN)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_access_time")
    private Date lastAccessTime;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSystemHost() {
        return systemHost;
    }

    public void setSystemHost(String systemHost) {
        this.systemHost = systemHost;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public OnlineSession.OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineSession.OnlineStatus status) {
        this.status = status;
    }

    public Date getStartTimestsamp() {
        return startTimestsamp;
    }

    public void setStartTimestsamp(Date startTimestsamp) {
        this.startTimestsamp = startTimestsamp;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public OnlineSession getSession() {
        return session;
    }

    public void setSession(OnlineSession session) {
        this.session = session;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public static final UserOnlineEntity fromOnlineSession(OnlineSession session) {
        UserOnlineEntity online = new UserOnlineEntity();
        online.setId(String.valueOf(session.getId()));
        online.setUserId(session.getUserId());
        online.setUsername(session.getUsername());
        online.setStartTimestsamp(session.getStartTimestamp());
        online.setLastAccessTime(session.getLastAccessTime());
        online.setTimeout(session.getTimeout());
        online.setHost(session.getHost());
        online.setUserAgent(session.getUserAgent());
        online.setSystemHost(session.getSystemHost());
        online.setSession(session);

        return online;
    }
}
