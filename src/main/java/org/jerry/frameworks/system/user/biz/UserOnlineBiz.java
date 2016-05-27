package org.jerry.frameworks.system.user.biz;

import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.system.entity.jpa.UserOnlineEntity;
import org.jerry.frameworks.system.user.repository.UserOnlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>Date : 16/5/24</p>
 * <p>Time : 下午3:52</p>
 *
 * @author jerry
 */
@Service
public class UserOnlineBiz extends BaseBiz<UserOnlineEntity, String> {

    @Autowired
    private UserOnlineRepository userOnlineRepository;

    /**
     * 上线
     *
     * @param userOnline    系统用户上线时实体
     */
    public void online(UserOnlineEntity userOnline) {
        save(userOnline);
    }

    /**
     * 下线
     *
     * @param userOnlineId  上线时产生的Id
     */
    public void offline(String userOnlineId) {
        UserOnlineEntity entity = findOne(userOnlineId);
        if (entity != null) {
            delete(entity);
        }
    }

    /**
     * 批量下线
     * 用于session timeout或admin kick
     *
     * @param needOfflineIdList     下线的ID集合
     */
    public void batchOffline(List<String> needOfflineIdList) {
        userOnlineRepository.batchDelete(needOfflineIdList);
    }

    /**
     * 无效的UserOnline
     *
     * @return  列表
     */
    public Page<UserOnlineEntity> findExpiredUserOnlineList(Date expiredDate, Pageable pageable) {
        return userOnlineRepository.findAfterExpiredUserOnlineList(expiredDate, pageable);
    }
}
