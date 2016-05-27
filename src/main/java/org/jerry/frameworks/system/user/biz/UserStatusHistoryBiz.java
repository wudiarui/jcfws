package org.jerry.frameworks.system.user.biz;

import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.entity.jpa.UserStatusHistoryEntity;
import org.jerry.frameworks.system.entity.jpa.type.UserState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>Date : 16/5/18</p>
 * <p>Time : 上午11:00</p>
 *
 * @author jerry
 */
@Service
public class UserStatusHistoryBiz extends BaseBiz<UserStatusHistoryEntity, Long> {
    public void log(UserEntity opUser, UserEntity user, UserState newStatus, String reason) {
        UserStatusHistoryEntity history = new UserStatusHistoryEntity();
        history.setUser(user);
        history.setOpUser(opUser);
        history.setOpDate(new Date());
        history.setStatus(newStatus);
        history.setReason(reason);
        save(history);
    }

    /**
     * 查找该{@link UserEntity}的最近一条历史记录
     *
     * @param user  被查找的用户
     * @return      该用户的最近一条历史记录
     */
    public UserStatusHistoryEntity findLastHistory(final UserEntity user) {
        Searchable searchable = Searchable.newSearchable()
                .addSearchParam("user_eq", user)
                .addSort(Sort.Direction.DESC, "opDate")
                .setPage(0, 1);
        Page<UserStatusHistoryEntity> page = baseRepository.findAll(searchable);

        if (page.hasContent()) {
            return page.getContent().get(0);
        }
        return null;
    }

    /**
     * 查找该{@link UserEntity}的最近一条历史记录的备注
     *
     * @param user  被查找的用户
     * @return      备注
     */
    public String getLastReason(UserEntity user) {
        UserStatusHistoryEntity history = findLastHistory(user);
        if (history == null) {
            return "";
        }
        return history.getReason();
    }
}
