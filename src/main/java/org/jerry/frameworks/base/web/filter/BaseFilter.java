package org.jerry.frameworks.base.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基础过滤器<br/>
 * 用于过滤URL中的空格、逗号、分号和换行。<br/>
 * <p>又用于黑、白名单过滤</p>
 *
 * <p>Date : 16/5/30</p>
 * <p>Time : 下午3:51</p>
 *
 * @author jerry
 */
public class BaseFilter implements Filter {
    private FilterConfig config = null;

    private final String[] NULL_STRING_ARRAY = new String[0];
    private final String   URL_SPLIT_PATTERN = "[, ;\r\n]";

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private Logger logger = LoggerFactory.getLogger("url.filter");

    /**
     * 白名单
     */
    private String[] whiteListURLs = null;

    /**
     * 黑名单
     */
    private String[] blackListURLs = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
        this.initConfig();
        this.init();
    }

    public void init() throws ServletException {
    }

    private void initConfig() {
        String whiteListURLStr = this.config.getInitParameter("whiteListURL");
        whiteListURLs = strToArray(whiteListURLStr);


        String blackListURLStr = this.config.getInitParameter("blackListURL");
        blackListURLs = strToArray(blackListURLStr);

    }

    private String[] strToArray(String urlStr) {
        if (urlStr == null) {
            return NULL_STRING_ARRAY;
        }
        String[] urlArray = urlStr.split(URL_SPLIT_PATTERN);

        List<String> urlList = new ArrayList<String>();

        for (String url : urlArray) {
            url = url.trim();
            if (url.length() == 0) {
                continue;
            }

            urlList.add(url);
        }

        return urlList.toArray(NULL_STRING_ARRAY);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String currentURL = httpRequest.getServletPath();

        logger.debug("url filter : current url : [{}]", currentURL);

        if (isBlackURL(currentURL)) {
            chain.doFilter(request, response);
            return;
        }
        if (isWhiteURL(currentURL)) {
            chain.doFilter(request, response);
            return;
        }
        doFilter(httpRequest, httpResponse, chain);
        return;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private boolean isWhiteURL(String currentURL) {
        for (String whiteURL : whiteListURLs) {
            if (pathMatcher.match(whiteURL, currentURL)) {
                logger.debug("url filter : white url list matches : [{}] match [{}] continue", currentURL, whiteURL);
                return true;
            }
        }
        logger.debug("url filter : white url list not matches : [{}] not match [{}]",
                currentURL, Arrays.toString(whiteListURLs));
        return false;
    }

    private boolean isBlackURL(String currentURL) {
        for (String blackURL : blackListURLs) {
            if (pathMatcher.match(blackURL, currentURL)) {
                logger.debug("url filter : black url list matches : [{}] match [{}] break", currentURL, blackURL);
                return true;
            }
        }
        logger.debug("url filter : black url list not matches : [{}] not match [{}]",
                currentURL, Arrays.toString(blackListURLs));
        return false;
    }

    public FilterConfig getConfig() {
        return config;
    }
}
