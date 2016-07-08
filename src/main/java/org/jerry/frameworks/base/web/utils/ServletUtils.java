package org.jerry.frameworks.base.web.utils;

import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Date : 16/5/31</p>
 * <p>Time : 下午3:24</p>
 *
 * @author jerry
 */
public class ServletUtils {
    /**
     * 判断指定请求url是否以method请求的 firstPrefix+lastPrefixes开头
     * 如当前请求url是/sample/create 则匹配firstPrefix:/sample lastPrefixed /create
     *
     * @param request       请求
     * @param method        请求的方法
     * @param firstPrefix   首
     * @param lastPrefixes  尾s
     * @return              是否符合自定义表达式
     */
    public static boolean startWith(HttpServletRequest request, RequestMethod method, String firstPrefix, String... lastPrefixes) {
        String requestMethod = request.getMethod();
        if (!requestMethod.equalsIgnoreCase(method.name())) {
            return false;
        }
        String url = request.getServletPath();
        if (!url.startsWith(firstPrefix)) {
            return false;
        }

        if (lastPrefixes.length == 0) {
            return true;
        }

        url = url.substring(firstPrefix.length());
        for (String lastPrefix : lastPrefixes) {
            if (url.startsWith(lastPrefix)) {
                return true;
            }
        }

        return false;
    }
}
