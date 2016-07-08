package org.jerry.frameworks.base.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.jerry.frameworks.base.constants.Constants;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Date : 16/5/30</p>
 * <p>Time : 下午4:34</p>
 *
 * @author jerry
 */
public class SetCommonDataInterceptor extends HandlerInterceptorAdapter {
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final static String[] DEFAULT_EXCLUDE_PARAMETER_PATTERN = new String[] {
            "\\&\\w*page.pn=\\d+",
            "\\?\\w*page.pn=\\d+",
            "\\&\\w*page.size=\\d+"
    };

    private String[] excludeParameterPatterns = DEFAULT_EXCLUDE_PARAMETER_PATTERN;
    private String[] excludeUrlPatterns = null;

    public void setExcludeParameterPatterns(String[] excludeParameterPatterns) {
        this.excludeParameterPatterns = excludeParameterPatterns;
    }

    public void setExcludeUrlPatterns(final String[] excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isExclude(request)) {
            return true;
        }
        if (request.getAttribute(Constants.CONTEXT_PATH) == null) {
            request.setAttribute(Constants.CONTEXT_PATH, request.getContextPath());
        }
        if (request.getAttribute(Constants.CURRENT_URL) == null) {
            request.setAttribute(Constants.CURRENT_URL, extractCurrentURL(request, true));
        }
        if (request.getAttribute(Constants.NO_QUERYSTRING_CURRENT_URL) == null) {
            request.setAttribute(Constants.NO_QUERYSTRING_CURRENT_URL, extractCurrentURL(request, false));
        }
        if (request.getAttribute(Constants.BACK_URL) == null) {
            request.setAttribute(Constants.BACK_URL, extractBackURL(request));
        }

        return true;
    }

    private boolean isExclude(final HttpServletRequest request) {
        if (excludeUrlPatterns == null) {
            return false;
        }
        for (String pattern : excludeUrlPatterns) {
            if (pathMatcher.match(pattern, request.getServletPath())) {
                return true;
            }
        }
        return false;
    }

    private String extractCurrentURL(HttpServletRequest request, boolean needQuerySyx) {
        String url = request.getRequestURI();
        String queryString = request.getQueryString();

        if (StringUtils.isNotEmpty(queryString)) {
            queryString = "?" + queryString;
            for (String pattern : excludeParameterPatterns) {
                queryString = queryString.replace(pattern, "");
            }
            if (queryString.startsWith("&")) {
                queryString = "?" + queryString.substring(1);
            }
        }
        if (StringUtils.isNotEmpty(queryString) && needQuerySyx) {
            url = url + queryString;
        }
        return url;
    }

    /**
     * 上一次请求的地址
     * 1、先从request.parameter中查找BackURL
     * 2、获取header中的 referer
     *
     * @param request       请求
     * @return              返回的地址
     */
    private String extractBackURL(HttpServletRequest request) {
        String url = request.getParameter(Constants.BACK_URL);

        //使用Filter时 文件上传时 getParameter时为null 所以改成Interceptor

        if (StringUtils.isEmpty(url)) {
            url = request.getHeader("Referer");
        }

        if(!StringUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
            return url;
        }

        if (!StringUtils.isEmpty(url) && url.startsWith(request.getContextPath())) {
            url = getBasePath(request) + url;
        }
        return url;
    }

    private String getBasePath(HttpServletRequest req) {
        StringBuffer baseUrl = new StringBuffer();
        String scheme = req.getScheme();
        int port = req.getServerPort();

        baseUrl.append(scheme);        // http, https
        baseUrl.append("://");
        baseUrl.append(req.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            baseUrl.append(':');
            baseUrl.append(req.getServerPort());
        }
        return baseUrl.toString();
    }
}
