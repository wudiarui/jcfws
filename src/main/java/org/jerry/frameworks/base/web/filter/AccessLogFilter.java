package org.jerry.frameworks.base.web.filter;

import org.jerry.frameworks.base.utils.LogUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 记录访问日志
 *
 * <p>Date : 16/5/30</p>
 * <p>Time : 下午4:16</p>
 *
 * @author jerry
 */
public final class AccessLogFilter extends BaseFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        LogUtils.logAccess(request);
        chain.doFilter(request, response);
    }
}
