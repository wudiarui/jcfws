package org.jerry.frameworks.system.shiro.session.mgt.eis;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.jerry.frameworks.system.entity.jpa.UserOnlineEntity;
import org.jerry.frameworks.system.shiro.ShiroConstants;
import org.jerry.frameworks.system.shiro.session.mgt.OnlineSession;
import org.jerry.frameworks.system.shiro.session.mgt.OnlineSessionFactory;
import org.jerry.frameworks.system.user.biz.UserOnlineBiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Date : 16/5/24</p>
 * <p>Time : 下午2:32</p>
 *
 * @author jerry
 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO {

    /**
     * 上次同步数据库的时间戳
     */
    private static final String LAST_SYNC_DB_TIMESTAMP =
            OnlineSessionDAO.class.getName() + "LAST_SYNC_DB_TIMESTAMP";

    @Autowired
    private UserOnlineBiz userOnlineBiz;

    @Autowired
    private OnlineSessionFactory onlineSessionFactory;

    private long dbSyncPeriod = 5 * 60 * 1000;

    public void setDbSyncPeriod(long dbSyncPeriod) {
        this.dbSyncPeriod = dbSyncPeriod;
    }

    /**
     * 将会话同步到DB
     *
     * @param session   shiro session
     */
    public void syncToDb(OnlineSession session) {
        Date lastSyncTimestamp = (Date) session.getAttribute(LAST_SYNC_DB_TIMESTAMP);
        if (lastSyncTimestamp != null) {
            boolean needSync = true;
            long deltaTime = session.getLastAccessTime().getTime() - lastSyncTimestamp.getTime();
            if (deltaTime < dbSyncPeriod) { //无需同步
                needSync = false;
            }
            boolean isGuest = session.getUserId() == null || session.getUserId() == 0L;
            //如果不是游客 且session 数据变更了 同步
            if (!isGuest && session.isAttributeChanged()) {
                needSync = true;
            }

            if (!needSync) {
                return;
            }
        }
        // 更新同步时间
        session.setAttribute(LAST_SYNC_DB_TIMESTAMP, session.getLastAccessTime());

        if (session.isAttributeChanged()) {
            session.resetAttributeChanged();
        }

        userOnlineBiz.online(UserOnlineEntity.fromOnlineSession(session));
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        UserOnlineEntity entity = userOnlineBiz.findOne(String.valueOf(sessionId));
        if (entity == null)
            return null;
        return onlineSessionFactory.CreateSession(entity);
    }

    /**
     * 会话过期时 离线处理
     *
     * @param session   onlineSession
     */
    @Override
    protected void doDelete(Session session) {
        OnlineSession onlineSession = (OnlineSession) session;
        //定时任务删除的此时就不删除了
        if (onlineSession.getAttribute(ShiroConstants.ONLY_CLEAR_CACHE) == null) {
            try {
                userOnlineBiz.offline(String.valueOf(onlineSession.getId()));
            } catch (Exception e) {
                //即使删除失败也无所谓
            }
        }
    }
}
