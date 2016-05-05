package org.jerry.frameworks.system.biz;

import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 提供账户级服务.如登录,注销,注册,找回密码等
 *
 * <p>Date : 16/4/22</p>
 * <p>Time : 下午2:02</p>
 *
 * @author jerry
 */
@Service
public class AccountBiz extends BaseBiz<UserEntity, Long> {

    private AccountRepository accountRepository;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 检查系统用户是否存在.
     *
     * @param username  用户名称
     * @return <li>True => 存在</li>
     *         <li>False => 不存在</li>
     */
    public Boolean checkAccountExist(String username) {
        if (!StringUtils.hasLength(username))
            return Boolean.FALSE;
        UserEntity account = accountRepository.findByUsername(username);

        if (null != account && account.getDeleted() != Boolean.TRUE)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    /**
     * 根据用户名获得实体
     *
     * @param username
     * @return
     */
    public UserEntity getAccountByName(String username) {
        return accountRepository.findByUsername(username);
    }
}
