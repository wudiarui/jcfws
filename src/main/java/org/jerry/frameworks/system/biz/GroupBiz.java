package org.jerry.frameworks.system.biz;

import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.system.entity.jpa.GroupEntity;
import org.jerry.frameworks.system.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * <p>Date : 16/5/6</p>
 * <p>Time : 上午9:26</p>
 *
 * @author jerry
 */
@Service
public class GroupBiz extends BaseBiz<GroupEntity, Long> {

    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository){
        this.groupRepository = groupRepository;
    }

    public GroupEntity getGroupByName(String groupName) {
        return this.groupRepository.findByName(groupName);
    }
}
