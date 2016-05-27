package org.jerry.frameworks.system.shiro.web.filter.authc;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.user.biz.UserBiz;
import org.jerry.frameworks.system.user.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;

import javax.servlet.ServletRequest;

/**
 * 基于几点修改：
 * 1、onLoginFailure 时 把异常添加到request attribute中 而不是异常类名
 * 2、登录成功时：成功页面重定向：
 * 2.1、如果前一个页面是登录页面，-->2.3
 * 2.2、如果有SavedRequest 则返回到SavedRequest
 * 2.3、否则根据当前登录的用户决定返回到管理员首页/前台首页
 * <p/>
 *
 * <p>Date : 16/5/25</p>
 * <p>Time : 下午1:17</p>
 *
 * @author jerry
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    @Autowired
    private UserBiz userBiz;

    @Override
    protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
        request.setAttribute(getFailureKeyAttribute(), ae);
    }

    /**
     * 默认的成功地址
     */
    private String defaultSuccessUrl;
    /**
     * 管理员默认的成功地址
     */
    private String adminDefaultSuccessUrl;

    public void setUserBiz(UserBiz userBiz) {
        this.userBiz = userBiz;
    }

    public String getDefaultSuccessUrl() {
        return defaultSuccessUrl;
    }

    public void setDefaultSuccessUrl(String defaultSuccessUrl) {
        this.defaultSuccessUrl = defaultSuccessUrl;
    }

    public String getAdminDefaultSuccessUrl() {
        return adminDefaultSuccessUrl;
    }

    public void setAdminDefaultSuccessUrl(String adminDefaultSuccessUrl) {
        this.adminDefaultSuccessUrl = adminDefaultSuccessUrl;
    }

    /**
     * 根据用户选择成功地址
     *
     * @return          跳转页面的地址
     */
    @Override
    public String getSuccessUrl() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        UserEntity user = userBiz.findByUsername(username);
        if (user != null && Boolean.FALSE.equals(user.getAdmin())) {
            return getAdminDefaultSuccessUrl();
        }
        return getDefaultSuccessUrl();
    }
}
