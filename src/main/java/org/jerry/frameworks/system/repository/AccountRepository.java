package org.jerry.frameworks.system.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.UserEntity;

/**
 * <p>Date : 16/4/22</p>
 * <p>Time : 上午10:07</p>
 *
 * @author jerry
 */
public interface AccountRepository extends BaseRepository<UserEntity, Long> {
    /**
     * 通过名称查看单个实体
     * @param username
     *          账号名
     * @return
     *          账号实体
     */
    UserEntity findByUsername(String username);
}
