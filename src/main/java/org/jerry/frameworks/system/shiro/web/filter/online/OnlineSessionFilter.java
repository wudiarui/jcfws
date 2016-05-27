package org.jerry.frameworks.system.shiro.web.filter.online;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.shiro.ShiroConstants;
import org.jerry.frameworks.system.shiro.session.mgt.OnlineSession;
import org.jerry.frameworks.system.shiro.session.mgt.eis.OnlineSessionDAO;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * <p>Date : 16/5/25</p>
 * <p>Time : 下午3:01</p>
 *
 * @author jerry
 */
public class OnlineSessionFilter extends AccessControlFilter {

    /**
     * 强制退出后重定向的地址
     */
    private String forceLogoutUrl;

    private OnlineSessionDAO onlineSessionDAO;

    public void setOnlineSessionDAO(OnlineSessionDAO onlineSessionDAO) {
        this.onlineSessionDAO = onlineSessionDAO;
    }

    public String getForceLogoutUrl() {

        return forceLogoutUrl;
    }

    public void setForceLogoutUrl(String forceLogoutUrl) {
        this.forceLogoutUrl = forceLogoutUrl;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object o) throws Exception {
        Subject subject = getSubject(request, response);
        if (subject == null) {
            return true;
        }

        Session session = onlineSessionDAO.readSession(subject.getSession().getId());
        if (session != null && session instanceof OnlineSession) {
            OnlineSession onlineSession = (OnlineSession) session;
            request.setAttribute(ShiroConstants.ONLINE_SESSION, onlineSession);

            boolean isGuest = onlineSession.getUserId() == null || onlineSession.getUserId() == 0L;
            if (isGuest) {
                UserEntity user = (UserEntity)request.getAttribute(Constants.CURRENT_USER);
                if (user != null) {
                    onlineSession.setUserId(user.getId());
                    onlineSession.setUsername(user.getUsername());
                    onlineSession.markAttributeChanged();
                }
            }

            if (onlineSession.getStatus() == OnlineSession.OnlineStatus.force_logout) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (subject != null) {
            subject.logout();
        }
        saveRequestAndRedirectToLogin(request, response);
        return true;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        WebUtils.issueRedirect(request, response, getForceLogoutUrl());
    }
}
