package org.jerry.frameworks.system.group.biz;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.system.entity.jpa.GroupRelationEntity;
import org.jerry.frameworks.system.group.repository.GroupRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>Date : 16/5/23</p>
 * <p>Time : 上午10:13</p>
 *
 * @author jerry
 */
@Service
public class GroupRelationBiz extends BaseBiz<GroupRelationEntity, Long> {
    @Autowired
    private GroupRelationRepository groupRelationRepository;

    public void appendRelation(Long groupId, Long[] organizationIds) {
        if (groupId == null || ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (Long organizationId : organizationIds) {
            if (organizationId == null) {
                continue;
            }
            GroupRelationEntity r = groupRelationRepository.findByGroupIdAndOrganizationId(groupId, organizationId);
            if (r == null) {
                r = new GroupRelationEntity();
                r.setGroupId(groupId);
                r.setOrganizationId(organizationId);
                save(r);
            }
        }
    }

    public void appendRelation(Long groupId, Long[] userIds, Long[] startUserIds, Long[] endUserIds) {
//        if (ArrayUtils.isEmpty(startUserIds) || ArrayUtils.isEmpty(endUserIds)) {
//            return;
//        }
        if (groupId == null) return;

        if(ArrayUtils.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                if (userId == null) {
                    continue;
                }
                GroupRelationEntity r = groupRelationRepository.findByGroupIdAndUserId(groupId, userId);
                if (r == null) {
                    r = new GroupRelationEntity();
                    r.setGroupId(groupId);
                    r.setUserId(userId);
                    save(r);
                }
            }
        }

        if (ArrayUtils.isNotEmpty(startUserIds) && startUserIds.length <= endUserIds.length) {
            long startUserId = 0;
            long endUserId = 0;
            for (int i = 0, l = startUserIds.length; i < l; i++) {
                startUserId = startUserIds[i];
                endUserId = endUserIds[i];

                GroupRelationEntity r = groupRelationRepository
                        .findByGroupIdAndStartUserIdLessThanEqualAndEndUserIdGreaterThanEqual(groupId, startUserId, endUserId);

                if (r == null) {
                    groupRelationRepository.deleteInRange(startUserId, endUserId);
                    r = new GroupRelationEntity();
                    r.setGroupId(groupId);
                    r.setStartUserId(startUserId);
                    r.setEndUserId(endUserId);
                    save(r);
                }
            }
        }
    }

    public Set<Long> findGroupIds(Long userId, Set<Long> organizationIds) {
        if (organizationIds.isEmpty()) {
            return Sets.newHashSet(groupRelationRepository.findGroupIds(userId));
        } else {
            return Sets.newHashSet(groupRelationRepository.findGroupIds(userId, organizationIds));
        }
    }
}
