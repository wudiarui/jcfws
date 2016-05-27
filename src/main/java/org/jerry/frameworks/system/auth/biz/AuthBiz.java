package org.jerry.frameworks.system.auth.biz;

import org.apache.commons.lang3.ArrayUtils;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.system.auth.repository.AuthRepository;
import org.jerry.frameworks.system.entity.jpa.AuthEntity;
import org.jerry.frameworks.system.entity.jpa.GroupEntity;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.group.biz.GroupBiz;
import org.jerry.frameworks.system.user.biz.UserBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午4:02</p>
 *
 * @author jerry
 */
@Service
public class AuthBiz extends BaseBiz<AuthEntity, Long> {
    @Autowired
    private UserBiz userBiz;

    @Autowired
    private GroupBiz groupBiz;

    @Autowired
    private AuthRepository authRepository;

    public void addUserAuth(Long[] userIds, AuthEntity m) {

        if (ArrayUtils.isEmpty(userIds)) {
            return;
        }

        for (Long userId : userIds) {
            UserEntity user = userBiz.findOne(userId);
            if (user == null) {
                continue;
            }

            AuthEntity auth = authRepository.findByUserId(userId);
            if (auth != null) {
                auth.addRoleIds(m.getRoleIds());
                continue;
            }
            auth = new AuthEntity();
            auth.setUserId(userId);
            auth.setType(m.getType());
            auth.setRoleIds(m.getRoleIds());
            save(auth);
        }
    }

    public void addGroupAuth(Long[] groupIds, AuthEntity m) {
        if (ArrayUtils.isEmpty(groupIds)) {
            return;
        }

        for (Long groupId : groupIds) {
            GroupEntity group = groupBiz.findOne(groupId);
            if (group == null) {
                continue;
            }

            AuthEntity auth = authRepository.findByGroupId(groupId);
            if (auth != null) {
                auth.addRoleIds(m.getRoleIds());
                continue;
            }
            auth = new AuthEntity();
            auth.setGroupId(groupId);
            auth.setType(m.getType());
            auth.setRoleIds(m.getRoleIds());
            save(auth);
        }
    }

    public void addOrganizationJobAuth(Long[] organizationIds, Long[][] jobIds, AuthEntity m) {

        if (ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (int i = 0, l = organizationIds.length; i < l; i++) {
            Long organizationId = organizationIds[i];
            if (jobIds[i].length == 0) {
                addOrganizationJobAuth(organizationId, null, m);
                continue;
            }

            //仅新增/修改一个 spring会自动split（“，”）--->给数组
            if (l == 1) {
                for (int j = 0, l2 = jobIds.length; j < l2; j++) {
                    Long jobId = jobIds[i][0];
                    addOrganizationJobAuth(organizationId, jobId, m);
                }
            } else {
                for (int j = 0, l2 = jobIds[i].length; j < l2; j++) {
                    Long jobId = jobIds[i][0];
                    addOrganizationJobAuth(organizationId, jobId, m);
                }
            }

        }
    }

    private void addOrganizationJobAuth(Long organizationId, Long jobId, AuthEntity m) {
        if (organizationId == null) {
            organizationId = 0L;
        }
        if (jobId == null) {
            jobId = 0L;
        }


        AuthEntity auth = authRepository.findByOrganizationIdAndJobId(organizationId, jobId);
        if (auth != null) {
            auth.addRoleIds(m.getRoleIds());
            return;
        }

        auth = new AuthEntity();
        auth.setOrganizationId(organizationId);
        auth.setJobId(jobId);
        auth.setType(m.getType());
        auth.setRoleIds(m.getRoleIds());
        save(auth);
    }

    /**
     * <ul>根据用户信息获取 角色
     * <li>用户  根据用户绝对匹配</li>
     * <li>组织机构 根据组织机构绝对匹配 此处需要注意 祖先需要自己获取</li>
     * <li>工作职务 根据工作职务绝对匹配 此处需要注意 祖先需要自己获取</li>
     * <li>组织机构和工作职务  根据组织机构和工作职务绝对匹配 此处不匹配祖先</li>
     * <li>组  根据组绝对匹配</li>
     * </ul>
     *
     * @param userId             必须有
     * @param groupIds           可选
     * @param organizationIds    可选
     * @param jobIds             可选
     * @param organizationJobIds 可选
     * @return  用户角色列表
     */
    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organizationIds, Set<Long> jobIds, Set<Long[]> organizationJobIds) {
        return authRepository.findRoleIds(userId, groupIds, organizationIds, jobIds, organizationJobIds);
    }
}
