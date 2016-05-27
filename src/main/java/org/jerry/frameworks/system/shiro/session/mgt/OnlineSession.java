package org.jerry.frameworks.system.shiro.session.mgt;

import org.apache.shiro.session.mgt.SimpleSession;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 在线用户会话类,用于共享shiro的在线会话状态
 *
 * <p>Date : 16/5/24</p>
 * <p>Time : 上午11:48</p>
 *
 * @author jerry
 */
public class OnlineSession extends SimpleSession {
    // Serialization reminder:
    // You _MUST_ change this number if you introduce a change to this class
    // that is NOT serialization backwards compatible.  Serialization-compatible
    // changes do not require a change to this number.  If you need to generate
    // a new number in this case, use the JDK's 'serialver' program to generate it.
    private static final long serialVersionUID = -7125642695178165650L;

    static int bitIndexCounter = 0;

    private transient String host;

    private static final int USER_ID_BIT_MASK = 1 << bitIndexCounter++;
    private static final int USER_AGENT_BIT_MASK = 1 << bitIndexCounter++;
    private static final int STATUS_BIT_MASK = 1 << bitIndexCounter++;
    private static final int USERNAME_BIT_MASK = 1 << bitIndexCounter++;
    private static final int REMEMBER_ME_BIT_MASK = 1 << bitIndexCounter++;

    public static enum OnlineStatus {
        on_line("在线"), hidden("隐身"), force_logout("强制退出");
        private final String info;

        OnlineStatus(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }

    // 当前登录的用户ID
    private Long userId = 0L;

    private String username;

    // 用户浏览器类型
    private String userAgent;

    // 在线状态
    private OnlineStatus status = OnlineStatus.on_line;

    // 用户登录时的IP
    private String systemHost;

    public OnlineSession() {
        super();
    }

    public OnlineSession(String host) {
        super(host);
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineStatus status) {
        this.status = status;
    }

    public String getSystemHost() {
        return systemHost;
    }

    public void setSystemHost(String systemHost) {
        this.systemHost = systemHost;
    }

    /*
     * 属性是否改变 优化session数据同步
     */
    private transient boolean attributeChanged = false;

    public void markAttributeChanged() {
        this.attributeChanged = true;
    }

    public void resetAttributeChanged() {
        this.attributeChanged = false;
    }

    public boolean isAttributeChanged() {
        return attributeChanged;
    }

    @Override
    public Object getAttribute(Object key) {
        return super.getAttribute(key);
    }

    @Override
    public void setAttribute(Object key, Object value) {
        super.setAttribute(key, value);
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        output.defaultWriteObject();
        short alteredFieldsBitMask = this.getAlteredFieldsBitMask();
        output.writeShort(alteredFieldsBitMask);

        if (userId != null) {
            output.writeObject(userId);
        }
        if (userAgent != null) {
            output.writeObject(userAgent);
        }
        if (status != null) {
            output.writeObject(status);
        }
        if (username != null) {
            output.writeObject(username);
        }
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        short bitMask = input.readShort();

        if (isFieldPresent(bitMask, USER_ID_BIT_MASK)) {
            this.userId = (Long) input.readObject();
        }
        if (isFieldPresent(bitMask, USER_AGENT_BIT_MASK)) {
            this.userAgent = (String) input.readObject();
        }
        if (isFieldPresent(bitMask, STATUS_BIT_MASK)) {
            this.status = (OnlineStatus) input.readObject();
        }
        if (isFieldPresent(bitMask, USERNAME_BIT_MASK)) {
            this.username = (String) input.readObject();
        }
    }

    /**
     * Returns a bit mask used during serialization indicating which fields have been serialized. Fields that have been
     * altered (not null and/or not retaining the class defaults) will be serialized and have 1 in their respective
     * index, fields that are null and/or retain class default values have 0.
     *
     * @return a bit mask used during serialization indicating which fields have been serialized.
     * @since 1.0
     */
    private short getAlteredFieldsBitMask() {
        int bitMask = 0b0;
        bitMask = userId != null ? bitMask | USER_ID_BIT_MASK : bitMask;
        bitMask = userAgent != null ? bitMask | USER_AGENT_BIT_MASK : bitMask;
        bitMask = status != null ? bitMask | STATUS_BIT_MASK : bitMask;
        bitMask = username != null ? bitMask | USERNAME_BIT_MASK : bitMask;
        return (short) bitMask;
    }

    /**
     * Returns {@code true} if the given {@code bitMask} argument indicates that the specified field has been
     * serialized and therefore should be read during deserialization, {@code false} otherwise.
     *
     * @param bitMask      the aggregate bitmask for all fields that have been serialized.  Individual bits represent
     *                     the fields that have been serialized.  A bit set to 1 means that corresponding field has
     *                     been serialized, 0 means it hasn't been serialized.
     * @param fieldBitMask the field bit mask constant identifying which bit to inspect (corresponds to a class attribute).
     * @return {@code true} if the given {@code bitMask} argument indicates that the specified field has been
     *         serialized and therefore should be read during deserialization, {@code false} otherwise.
     * @since 1.0
     */
    private static boolean isFieldPresent(short bitMask, int fieldBitMask) {
        return (bitMask & fieldBitMask) != 0;
    }
}
