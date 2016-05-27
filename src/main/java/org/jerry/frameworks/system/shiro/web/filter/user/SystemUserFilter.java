package org.jerry.frameworks.system.shiro.web.filter.user;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.entity.jpa.type.UserState;
import org.jerry.frameworks.system.user.biz.UserBiz;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 验证用户过滤器
 * 1、用户是否删除
 * 2、用户是否锁定
 *
 * <p>Date : 16/5/25</p>
 * <p>Time : 下午2:29</p>
 *
 * @author jerry
 */
public class SystemUserFilter extends AccessControlFilter {

    @Autowired
    private UserBiz userBiz;

    private String userNotFoundUrl;

    private String userBlockedUrl;

    private String userUnknownErrorUrl;

    public String getUserNotFoundUrl() {
        return userNotFoundUrl;
    }

    public void setUserNotFoundUrl(String userNotFoundUrl) {
        this.userNotFoundUrl = userNotFoundUrl;
    }

    public String getUserBlockedUrl() {
        return userBlockedUrl;
    }

    public void setUserBlockedUrl(String userBlockedUrl) {
        this.userBlockedUrl = userBlockedUrl;
    }

    public String getUserUnknownErrorUrl() {
        return userUnknownErrorUrl;
    }

    public void setUserUnknownErrorUrl(String userUnknownErrorUrl) {
        this.userUnknownErrorUrl = userUnknownErrorUrl;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (subject == null) {
            return true;
        }

        String username = (String) subject.getPrincipal();
        UserEntity user = userBiz.findByUsername(username);

        request.setAttribute(Constants.CURRENT_USER, user);
        ((HttpServletRequest)request).getSession().setAttribute(Constants.CURRENT_USERNAME, username);

        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object o) throws Exception {
        UserEntity user = (UserEntity) request.getAttribute(Constants.CURRENT_USER);
        // 这里返回True, 是因为非鉴权的URL, 如login页、演示页
        if (user == null) {
            return true;
        }

        if (Boolean.TRUE.equals(user.getDeleted()) || user.getStatus() == UserState.blocked) {
            getSubject(request, response).logout();
            saveRequestAndRedirectToLogin(request, response);
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        getSubject(request, response).logout();
        saveRequestAndRedirectToLogin(request, response);
        return true;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        UserEntity user = (UserEntity) request.getAttribute(Constants.CURRENT_USER);
        String url = null;
        if (Boolean.TRUE.equals(user.getDeleted())) {
            url = getUserNotFoundUrl();
        } else if (user.getStatus() == UserState.blocked) {
            url = getUserBlockedUrl();
        } else {
            url = getUserUnknownErrorUrl();
        }

        WebUtils.issueRedirect(request, response, url);
    }
}
