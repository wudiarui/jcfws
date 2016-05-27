package org.jerry.frameworks.system.user.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.UserOnlineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * <p>Date : 16/5/24</p>
 * <p>Time : 下午2:36</p>
 *
 * @author jerry
 */
public interface UserOnlineRepository extends BaseRepository<UserOnlineEntity, String> {

    @Query("from UserOnlineEntity o where o.lastAccessTime < ?1 order by o.lastAccessTime asc")
    Page<UserOnlineEntity> findAfterExpiredUserOnlineList(Date expiredDate, Pageable pageable);

    @Modifying
    @Query("delete from UserOnlineEntity o where o.id in (?1)")
    void batchDelete(List<String> needExpiredIdList);
}
