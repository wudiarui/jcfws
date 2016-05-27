package org.jerry.frameworks.system.group.biz;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.base.entity.search.SearchOperator;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.system.entity.jpa.GroupEntity;
import org.jerry.frameworks.system.group.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

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

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupRelationBiz groupRelationBiz;

    public GroupEntity getGroupByName(String groupName) {
        return this.groupRepository.findByName(groupName);
    }

    /**
     * 列表显示或选择器用
     *
     * @param searchable    查询条件
     * @param groupName     用户组名
     * @return              组名和ID的散列表
     */
    public Set<Map<String, Object>> findIdAndNames(Searchable searchable, String groupName) {
        searchable.addSearchFilter("name", SearchOperator.like, groupName);

        return Sets.newHashSet(
                Lists.transform(
                        findAll(searchable).getContent(),
                        new Function<GroupEntity, Map<String, Object>>() {
                            @Override
                            public Map<String, Object> apply(GroupEntity groupEntity) {
                                Map<String, Object> data = Maps.newHashMap();
                                data.put("label", groupEntity.getName());
                                data.put("value", groupEntity.getId());
                                return data;
                            }
                        }
                )
        );
    }

    /**
     * 查询出所有可显的组ID
     *
     * @param userId                用户ID
     * @param organizationIds       组织机构IDs
     * @return                      用户组IDs
     */
    public Set<Long> findShowGroupIds(Long userId, Set<Long> organizationIds) {
        Set<Long> groupIds = Sets.newHashSet();
        groupIds.addAll(groupRepository.findShowGroupIds());

        return groupIds;
    }
}
