package org.jerry.frameworks.system.shiro;

/**
 * <p>Date : 16/5/18</p>
 * <p>Time : 上午9:47</p>
 *
 * @author jerry
 */
public interface ShiroConstants {
    /**
     * 当前在线会话
     */
    String ONLINE_SESSION = "online_session";

    /**
     * 仅清空本地缓存 不情况数据库的
     */
    String ONLY_CLEAR_CACHE = "online_session_only_clear_cache";
}
