package org.jerry.frameworks.system.user.biz;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.base.constants.ValidateConstants;
import org.jerry.frameworks.base.entity.search.SearchOperator;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.entity.jpa.UserOrganizationJobEntity;
import org.jerry.frameworks.system.entity.jpa.type.UserState;
import org.jerry.frameworks.system.user.exception.UserBlockedException;
import org.jerry.frameworks.system.user.exception.UserNotExistsException;
import org.jerry.frameworks.system.user.exception.UserPasswordNotMatchException;
import org.jerry.frameworks.system.user.repository.UserRepository;
import org.jerry.frameworks.system.user.type.UserOperationType;
import org.jerry.frameworks.system.user.utils.UserLogUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统用户模块主业务类
 *
 * <p>Date : 16/5/18</p>
 * <p>Time : 上午9:52</p>
 *
 * @author jerry
 */
@Service
public class UserBiz extends BaseBiz<UserEntity, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusHistoryBiz historyBiz;

    @Autowired
    private PasswordBiz passwordBiz;

    public void setPasswordBiz(PasswordBiz passwordBiz) {
        this.passwordBiz = passwordBiz;
    }

    @Override
    public UserEntity save(UserEntity user) {
        if (user.getCreateDate() == null) {
            user.setCreateDate(new Date());
        }
        // 加密
        user.randomSalt();
        user.setPassword(passwordBiz.encryptPassword(user.getUsername(), user.getPassword(), user.getSalt()));

        return super.save(user);
    }

    @Override
    public UserEntity update(UserEntity user) {

        List<UserOrganizationJobEntity> userOrganizationJobEntities = user.getOrganizationJobs();
        for (int i = 0, l = userOrganizationJobEntities.size(); i < l; i++) {
            UserOrganizationJobEntity userOrganizationJobEntity = userOrganizationJobEntities.get(i);
            // 强制关系设置
            userOrganizationJobEntity.setUser(user);

            UserOrganizationJobEntity dbUserOranizationJob = findUserOrganizationJob(userOrganizationJobEntity);
            if (dbUserOranizationJob != null) {
                dbUserOranizationJob.setUser(userOrganizationJobEntity.getUser());
                dbUserOranizationJob.setJobId(userOrganizationJobEntity.getJobId());
                dbUserOranizationJob.setOrganizationId(userOrganizationJobEntity.getOrganizationId());
                userOrganizationJobEntities.set(i, dbUserOranizationJob);
            }
        }
        return super.update(user);
    }

    public UserOrganizationJobEntity findUserOrganizationJob(UserOrganizationJobEntity userOrganizationJob) {
        return userRepository.findUserOrganization(
                userOrganizationJob.getUser(),
                userOrganizationJob.getOrganizationId(),
                userOrganizationJob.getJobId());
    }

    public UserEntity findByUsername(String username) {
        if (StringUtils.isEmpty(username))
            return null;
        return userRepository.findByUsername(username);
    }

    public UserEntity findByEmail(String email) {
        if (StringUtils.isEmpty(email))
            return null;
        return userRepository.findByEmail(email);
    }

    public UserEntity findByMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        return userRepository.findByMobilePhoneNumber(mobile);
    }

    public UserEntity changePassword(UserEntity user, String newPassword) {
        user.randomSalt();
        user.setPassword(passwordBiz.encryptPassword(user.getUsername(), newPassword, user.getSalt()));
        update(user);
        return user;
    }

    public UserEntity changeStatus(UserEntity opUser, UserEntity user, UserState status, String reason) {
        user.setStatus(status);
        update(user);
        historyBiz.log(opUser, user, status, reason);
        return user;
    }

    public UserEntity login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            UserLogUtils.getUserLog(
                    username,
                    UserOperationType.loginError,
                    "Username or Password, require a value of not empty."
            );
            throw new UserNotExistsException();
        }

        if (password.length() < ValidateConstants.PASSWORD_MIN_LENGTH
                || password.length() > ValidateConstants.PASSWORD_MAX_LENGTH) {
            UserLogUtils.getUserLog(
                    username,
                    UserOperationType.passwordError,
                    "password length error! password is between {} and {}",
                    ValidateConstants.PASSWORD_MIN_LENGTH, ValidateConstants.PASSWORD_MAX_LENGTH
            );
            throw new UserPasswordNotMatchException();
        }

        UserEntity user = null;

        // 代理服务层, 走缓存切面
        UserBiz proxyUserBiz = (UserBiz) AopContext.currentProxy();
        if (maybeUsername(username))
            user = proxyUserBiz.findByUsername(username);
        if (user == null && maybeEmail(username))
            user = proxyUserBiz.findByEmail(username);
        if (user == null && maybeMobile(username))
            user = proxyUserBiz.findByMobile(username);

        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            UserLogUtils.getUserLog(
                    username,
                    UserOperationType.loginError,
                    "user.not.exists"
            );
            throw new UserNotExistsException();
        }

        passwordBiz.validate(user, password);

        if (user.getStatus() == UserState.blocked) {
            UserLogUtils.getUserLog(
                    username,
                    UserOperationType.loginError,
                    "user is blocked!"
            );
            throw new UserBlockedException(historyBiz.getLastReason(user));
        }

        UserLogUtils.getUserLog(
                username,
                UserOperationType.loginSuccess,
                "login success!"
        );

        return user;
    }

    private boolean maybeUsername(String username) {
        if (!username.matches(ValidateConstants.USERNAME_PATTERN)) {
            return false;
        }

        if (username.length() < ValidateConstants.USERNAME_MIN_LENGTH || username.length() > ValidateConstants.USERNAME_MAX_LENGTH) {
            return false;
        }
        return true;
    }

    private boolean maybeEmail(String email) {
        return email.matches(ValidateConstants.EMAIL_PATTERN);
    }

    private boolean maybeMobile(String mobile) {
        return mobile.matches(ValidateConstants.MOBILE_PHONE_NUMBER_PATTERN);
    }

    public void changePassword(UserEntity opUser, Long[] ids, String newPassword) {
        UserBiz proxyUserBiz = (UserBiz) AopContext.currentProxy();

        for (Long id : ids) {
            UserEntity user = findOne(id);
            proxyUserBiz.changePassword(user, newPassword);
            UserLogUtils.getUserLog(
                    user.getUsername(),
                    UserOperationType.changePassword,
                    "admin user {} change password!",
                    opUser.getUsername()
            );
        }
    }

    public void changeStatus(UserEntity opUser, Long[] ids, UserState userStatus, String reason) {
        UserBiz proxyUserBiz = (UserBiz) AopContext.currentProxy();

        for (Long id : ids) {
            UserEntity user = findOne(id);
            proxyUserBiz.changeStatus(opUser, user, userStatus, reason);
            UserLogUtils.getUserLog(
                    user.getUsername(),
                    UserOperationType.changeStatus,
                    "admin user {} change status!",
                    opUser.getUsername()
            );
        }
    }

    public Set<Map<String, Object>> findIdAndNames(Searchable searchable, String username) {

        searchable.addSearchFilter("username", SearchOperator.like, username);
        searchable.addSearchFilter("deleted", SearchOperator.eq, false);

        return Sets.newHashSet(
                Lists.transform(
                        findAll(searchable).getContent(),
                        new Function<UserEntity, Map<String, Object>>() {
                            @Override
                            public Map<String, Object> apply(UserEntity user) {
                                Map<String, Object> data = Maps.newHashMap();
                                data.put("label", user.getUsername());
                                data.put("value", user.getId());
                                return data;
                            }
                        }
                )
        );
    }

    /**
     * 获取那些在用户-组织机构/工作职务中存在 但在组织机构/工作职务中不存在的
     *
     * @param pageable  pageable
     * @return  过滤后的结果集
     */
    public Page<UserOrganizationJobEntity> findUserOrganizationJobOnNotExistsOrganizationOrJob(Pageable pageable) {
        return userRepository.findUserOrganizationJobOnNotExistsOrganizationOrJob(pageable);
    }

    /**
     * 删除用户不存在的情况的UserOrganizationJob（比如手工从数据库物理删除）。。
     */
    public void deleteUserOrganizationJobOnNotExistsUser() {
        userRepository.deleteUserOrganizationJobOnNotExistsUser();
    }
}
