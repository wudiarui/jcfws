package org.jerry.frameworks.system.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.jerry.frameworks.base.repository.support.SimpleBaseRepositoryFactoryBean;
import org.jerry.frameworks.system.auth.biz.UserAuthBiz;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.user.biz.UserBiz;
import org.jerry.frameworks.system.user.exception.UserBlockedException;
import org.jerry.frameworks.system.user.exception.UserNotExistsException;
import org.jerry.frameworks.system.user.exception.UserPasswordNotMatchException;
import org.jerry.frameworks.system.user.exception.UserPasswordRetryLimitExceedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * 系统用户授权类
 *
 * <p>Date : 16/5/18</p>
 * <p>Time : 上午9:48</p>
 *
 * @author jerry
 */
public class UserRealm extends AuthorizingRealm {
    private final static Logger logger = LoggerFactory.getLogger("Auth.error");

    private static final String OR_OPERATOR = " or ";
    private static final String AND_OPERATOR = " and ";
    private static final String NOT_OPERATOR = "not ";

    @Autowired
    private UserBiz userBiz;

    @Autowired
    private UserAuthBiz userAuthBiz;

    @Autowired
    public UserRealm(ApplicationContext ctx) {
        super();
        ctx.getBeansOfType(SimpleBaseRepositoryFactoryBean.class);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        UserEntity user = userBiz.findByUsername(username);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(userAuthBiz.findStringRoles(user));
        authorizationInfo.setStringPermissions(userAuthBiz.findStringPermissions(user));

        return authorizationInfo;
    }

    public boolean isPermitted(PrincipalCollection principals, String permission) {
        if (permission.contains(OR_OPERATOR)) {
            String[] permissions = permission.split(OR_OPERATOR);
            for (String orPermission : permissions) {
                if (isPermittedNotWithOperator(principals, orPermission)) {
                    return true;
                }
            }
            return false;
        } else if (permission.contains(AND_OPERATOR)) {
            String[] permissions = permission.split(AND_OPERATOR);
            for (String orPermission : permissions) {
                if (!isPermittedNotWithOperator(principals, orPermission)) {
                    return false;
                }
            }
            return true;
        } else {
            return isPermittedNotWithOperator(principals, permission);
        }
    }

    private boolean isPermittedNotWithOperator(PrincipalCollection principals, String permission) {
        if (permission.startsWith(NOT_OPERATOR)) {
            return !super.isPermitted(principals, permission.substring(NOT_OPERATOR.length()));
        } else {
            return super.isPermitted(principals, permission);
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String username = upToken.getUsername().trim();
        String password = null;
        if (upToken.getPassword() != null) {
            password = String.valueOf(upToken.getPassword());
        }

        UserEntity user;
        try {
            user = userBiz.login(username, password);
        } catch (UserNotExistsException e) {
            throw new UnknownAccountException(e.getMessage(), e);
        } catch (UserPasswordNotMatchException e) {
            throw new AuthenticationException(e.getMessage(), e);
        } catch (UserPasswordRetryLimitExceedException e) {
            throw new ExcessiveAttemptsException(e.getMessage(), e);
        } catch (UserBlockedException e) {
            throw new LockedAccountException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("login error", e);
            throw new AuthenticationException("user.unknown.error", null);
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUsername(), password.toCharArray(), getName());
        return authenticationInfo;
    }
}
