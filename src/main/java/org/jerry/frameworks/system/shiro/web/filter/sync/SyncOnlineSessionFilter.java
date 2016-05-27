package org.jerry.frameworks.system.shiro.web.filter.sync;

import org.apache.shiro.web.filter.PathMatchingFilter;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.system.shiro.ShiroConstants;
import org.jerry.frameworks.system.shiro.session.mgt.OnlineSession;
import org.jerry.frameworks.system.shiro.session.mgt.eis.OnlineSessionDAO;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 权限匹配,存入数据库
 *
 * <p>Date : 16/5/26</p>
 * <p>Time : 上午10:39</p>
 *
 * @author jerry
 */
public class SyncOnlineSessionFilter extends PathMatchingFilter {

    private OnlineSessionDAO onlineSessionDAO;

    public void setOnlineSessionDAO(OnlineSessionDAO onlineSessionDAO) {
        this.onlineSessionDAO = onlineSessionDAO;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        OnlineSession session = (OnlineSession) request.getAttribute(ShiroConstants.ONLINE_SESSION);

        if (session != null && session.getStopTimestamp() == null) {
            onlineSessionDAO.syncToDb(session);
        }

        return true;
    }
}
