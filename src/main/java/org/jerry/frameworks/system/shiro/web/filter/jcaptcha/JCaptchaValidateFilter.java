package org.jerry.frameworks.system.shiro.web.filter.jcaptcha;

import com.sun.tools.javac.tree.JCTree;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.jerry.frameworks.base.web.jcaptcha.JCaptcha;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>Date : 16/5/25</p>
 * <p>Time : 下午3:43</p>
 *
 * @author jerry
 */
public class JCaptchaValidateFilter extends AccessControlFilter {

    private boolean jcaptchaEbabled = false;

    private String jcaptchaParam = "jcaptchaCode";

    private String jcapatchaErrorUrl;

    /**
     * 是否开启jcaptcha
     */
    public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
        this.jcaptchaEbabled = jcaptchaEbabled;
    }

    /**
     * 前台传入的验证码
     */
    public void setJcaptchaParam(String jcaptchaParam) {
        this.jcaptchaParam = jcaptchaParam;
    }

    public void setJcapatchaErrorUrl(String jcapatchaErrorUrl) {
        this.jcapatchaErrorUrl = jcapatchaErrorUrl;
    }

    public String getJcapatchaErrorUrl() {
        return jcapatchaErrorUrl;
    }

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);
        return super.onPreHandle(request, response, mappedValue);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object o) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //验证码禁用 或不是表单提交 允许访问
        if (jcaptchaEbabled == false || !"post".equals(httpServletRequest.getMethod().toLowerCase())) {
            return true;
        }
        return JCaptcha.validateResponse(httpServletRequest, httpServletRequest.getParameter(jcaptchaParam));
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        redirectToLogin(request, response);
        return true;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        WebUtils.issueRedirect(request, response, getJcapatchaErrorUrl());
    }
}
