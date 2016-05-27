package org.jerry.frameworks.system.user.biz;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.jerry.frameworks.base.utils.security.Md5Utils;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.user.exception.UserPasswordNotMatchException;
import org.jerry.frameworks.system.user.exception.UserPasswordRetryLimitExceedException;
import org.jerry.frameworks.system.user.type.UserOperationType;
import org.jerry.frameworks.system.user.utils.UserLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * <p>Date : 16/5/18</p>
 * <p>Time : 上午11:26</p>
 *
 * @author jerry
 */
@Service
public class PasswordBiz {

    @Autowired
    private CacheManager cacheManager;

    private Cache loginRecordCache;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount = 10;

    public void setUserRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @PostConstruct
    public void init() {
        loginRecordCache = cacheManager.getCache(loginRecordCache.getName());
    }

    public void validate(UserEntity user, String password) {
        String username = user.getUsername();

        int retry = 0;

        Element cacheElement = loginRecordCache.get(username);
        if (cacheElement != null) {
            retry = (Integer)cacheElement.getObjectValue();
            if (retry >= maxRetryCount) {
                UserLogUtils.getUserLog(username,
                        UserOperationType.passwordError,
                        "password error, retry limit exceed! password: {},max retry count {}",
                        password, maxRetryCount);
                throw new UserPasswordRetryLimitExceedException(maxRetryCount);
            }
        }

        if (!matches(user, password)) {
            loginRecordCache.put(new Element(username, ++retry));
            UserLogUtils.getUserLog(
                    username,
                    UserOperationType.passwordError,
                    "password error! password: {} retry count: {}",
                    password, retry
            );
            throw new UserPasswordNotMatchException();
        } else {
            clearLoginRecordCache(username);
        }
    }

    public void clearLoginRecordCache(String username) {
        loginRecordCache.remove(username);
    }

    public boolean matches(UserEntity user, String newPassword) {
        return user.getPassword().equals(encryptPassword(user.getUsername(), newPassword, user.getSalt()));
    }

    public String encryptPassword(String username, String password, String salt) {
        return Md5Utils.hash(username + password + salt);
    }
}
