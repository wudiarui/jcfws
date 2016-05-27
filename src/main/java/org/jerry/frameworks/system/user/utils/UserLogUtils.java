package org.jerry.frameworks.system.user.utils;

import org.jerry.frameworks.base.utils.IpUtils;
import org.jerry.frameworks.base.utils.LogUtils;
import org.jerry.frameworks.system.user.type.UserOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 日志记录用户操作工具类
 *
 * <p>Date : 16/5/19</p>
 * <p>Time : 上午9:46</p>
 *
 * @author jerry
 */
public class UserLogUtils {

    private final static Logger SYS_USER_LOGGER = LoggerFactory.getLogger("rashomon-sys-user");

    public static Logger getSysUserLogger() {
        return SYS_USER_LOGGER;
    }

    public static void getUserLog(String username, UserOperationType op, String msg, Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(LogUtils.getBlock(getIp()));
        sb.append(LogUtils.getBlock(username));
        sb.append(LogUtils.getBlock(msg));
        sb.append(LogUtils.getBlock(op.getInfo()));

        SYS_USER_LOGGER.info(sb.toString(), args);
    }

    public static Object getIp() {
        RequestAttributes requestAttributes = null;

        requestAttributes = RequestContextHolder.currentRequestAttributes();

        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            return IpUtils.getIpAddr(((ServletRequestAttributes) requestAttributes).getRequest());
        }

        return "unknown";
    }
}
