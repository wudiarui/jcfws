package org.jerry.frameworks.system.repository;

import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.system.entity.jpa.GroupEntity;

/**
 * <p>Date : 16/5/6</p>
 * <p>Time : 上午9:22</p>
 *
 * @author jerry
 */
public interface GroupRepository extends BaseRepository<GroupEntity, Long> {

    /**
     * 通过组名查找单个组实体
     *
     * @param groupName     组名
     * @return              被查询的组实体
     */
    GroupEntity findByName(String groupName);
}
