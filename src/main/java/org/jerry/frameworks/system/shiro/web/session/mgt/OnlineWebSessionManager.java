package org.jerry.frameworks.system.shiro.web.session.mgt;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.system.entity.jpa.UserOnlineEntity;
import org.jerry.frameworks.system.shiro.ShiroConstants;
import org.jerry.frameworks.system.shiro.session.mgt.OnlineSession;
import org.jerry.frameworks.system.user.biz.UserOnlineBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * <p>Date : 16/5/25</p>
 * <p>Time : 上午11:17</p>
 *
 * @author jerry
 */
public class OnlineWebSessionManager extends DefaultWebSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(OnlineWebSessionManager.class);

    @Autowired
    private UserOnlineBiz userOnlineBiz;

    public void setUserOnlineBiz(UserOnlineBiz userOnlineBiz) {
        this.userOnlineBiz = userOnlineBiz;
    }

    @Override
    public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) throws InvalidSessionException {
        super.setAttribute(sessionKey, attributeKey, value);
        if (value != null && needMarkAttributeChanged(attributeKey)) {
            OnlineSession session = (OnlineSession) doGetSession(sessionKey);
            session.markAttributeChanged();
        }
    }

    @Override
    public Object removeAttribute(SessionKey sessionKey, Object attributeKey) throws InvalidSessionException {
        Object removed = super.removeAttribute(sessionKey, attributeKey);
        if (removed != null) {
            OnlineSession session = (OnlineSession) doGetSession(sessionKey);
            session.markAttributeChanged();
        }
        return removed;
    }

    @Override
    protected Collection<Session> getActiveSessions() {
        throw new UnsupportedOperationException("getActiveSessions method not supported");
    }

    /**
     * 验证session是否有效 用于删除过期session
     */
    @Override
    public void validateSessions() {
        if (logger.isInfoEnabled()) {
            logger.info("invalidation sessions...");
        }

        int invalidCount = 0;
        int timeout = (int) getGlobalSessionTimeout();

        Date expiredDate = DateUtils.addMilliseconds(new Date(), 0 - timeout);
        PageRequest pageRequest = new PageRequest(0, 100);
        Page<UserOnlineEntity> page = userOnlineBiz.findExpiredUserOnlineList(expiredDate, pageRequest);

        while (page.hasContent()) {
            List<String> needOfflineIdList = Lists.newArrayList();
            for (UserOnlineEntity userOnline : page.getContent()) {
                try {
                    SessionKey key = new DefaultSessionKey(userOnline.getId());
                    Session session = retrieveSession(key);
                    if (session != null) {
                        session.setAttribute(ShiroConstants.ONLY_CLEAR_CACHE, true);
                    }
                    validate(session, key);
                } catch (InvalidSessionException e) {
                    if (logger.isDebugEnabled()) {
                        boolean expired = (e instanceof ExpiredSessionException);
                        String msg = "Invalidated session with id [" + userOnline.getId() + "]" +
                                (expired ? " (expired)" : " (stopped)");
                        logger.debug(msg);
                    }
                    invalidCount++;
                    needOfflineIdList.add(userOnline.getId());
                }
            }

            if (needOfflineIdList.size() > 0) {
                try {
                    userOnlineBiz.batchOffline(needOfflineIdList);
                } catch (Exception e) {
                    logger.error("batch delete db session error.", e);
                }
            }
            pageRequest = new PageRequest(0, pageRequest.getPageSize());
            page = userOnlineBiz.findExpiredUserOnlineList(expiredDate, pageRequest);
        }

        if (logger.isInfoEnabled()) {
            String msg = "Finished invalidation session.";
            if (invalidCount > 0) {
                msg += "  [" + invalidCount + "] sessions were stopped.";
            } else {
                msg += "  No sessions were stopped.";
            }
            logger.info(msg);
        }
    }

    private boolean needMarkAttributeChanged(Object attributeKey) {
        if (attributeKey == null) {
            return false;
        }

        String attributeKeyStr = attributeKey.toString();
        //优化 flash属性没必要持久化
        return !attributeKeyStr.startsWith("org.springframework")
                && !attributeKeyStr.startsWith("javax.servlet")
                && !attributeKeyStr.equals(Constants.CURRENT_USERNAME);
    }
}
