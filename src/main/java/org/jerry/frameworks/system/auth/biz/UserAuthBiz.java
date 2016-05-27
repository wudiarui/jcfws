package org.jerry.frameworks.system.auth.biz;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.commons.lang3.StringUtils;
import org.jerry.frameworks.system.entity.jpa.*;
import org.jerry.frameworks.system.group.biz.GroupBiz;
import org.jerry.frameworks.system.organzation.biz.JobBiz;
import org.jerry.frameworks.system.organzation.biz.OrganizationBiz;
import org.jerry.frameworks.system.permission.biz.PermissionBiz;
import org.jerry.frameworks.system.permission.biz.RoleBiz;
import org.jerry.frameworks.system.resource.biz.ResourceBiz;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 *
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午4:35</p>
 *
 * @author jerry
 */
@Service
public class UserAuthBiz {

    @Autowired
    private GroupBiz groupBiz;

    @Autowired
    private AuthBiz authBiz;

    @Autowired
    private JobBiz jobBiz;

    @Autowired
    private OrganizationBiz organizationBiz;

    @Autowired
    private PermissionBiz permissionBiz;

    @Autowired
    private RoleBiz roleBiz;

    @Autowired
    private ResourceBiz resourceBiz;

    /**
     * 获取用户角色
     *
     * @param user  系统用户
     * @return      用户所有的角色
     */
    public Set<RoleEntity> findRoles(UserEntity user) {
        if (user == null) {
            return Sets.newHashSet();
        }
        // 获取用户ID
        Long userId = user.getId();
//        组织机构职务IDs
        Set<Long[]> organizationJobIds = Sets.newHashSet();
//        组织机构IDs
        Set<Long> organizationIds = Sets.newHashSet();
//        职务IDs
        Set<Long> jobIds = Sets.newHashSet();

//        获得用户的职务、组织机构职务和组织机构
        for (UserOrganizationJobEntity uoj : user.getOrganizationJobs()) {
            Long oId = uoj.getOrganizationId();
            Long jobId = uoj.getJobId();

            if (oId != null && jobId != null && oId != 0L && jobId != 0L) {
                organizationJobIds.add(new Long[]{oId, jobId});
            }
            organizationIds.add(oId);
            jobIds.add(jobId);
        }
//        获取组织机构的祖先IDs
        organizationIds.addAll(organizationBiz.findAncestorIds(organizationIds));
//        获取职务的祖先IDs
        jobIds.addAll(jobBiz.findAncestorIds(jobIds));

        //过滤组织机构 仅获取目前可用的组织机构数据
        organizationBiz.filterForCanShow(organizationIds, organizationJobIds);
        //过滤工作职务 仅获取目前可用的工作职务数据
        jobBiz.filterForCanShow(jobIds, organizationJobIds);
        //默认分组 + 根据用户编号 和 组织编号 找 分组
        Set<Long> groupIds = groupBiz.findShowGroupIds(userId, organizationIds);

        Set<Long> roleIds = authBiz.findRoleIds(userId, groupIds, organizationIds, jobIds, organizationJobIds);

        Set<RoleEntity> roleEntities = roleBiz.findShowRoles(roleIds);

        return roleEntities;
    }

    public Set<String> findStringRoles(UserEntity user) {
        Set<RoleEntity> roles = ((UserAuthBiz) AopContext.currentProxy()).findRoles(user);
        return Sets.newHashSet(
                Collections2.transform(roles, new Function<RoleEntity, String>() {
                    @Override
                    public String apply(RoleEntity roleEntity) {
                        return roleEntity.getRole();
                    }
                })
        );
    }


    /**
     * 根据系统用户角色,获取权限字符串
     *
     * @param user      系统用户
     * @return          该用户的权限字符串
     */
    public Set<String> findStringPermissions(UserEntity user) {
        Set<String> permissions = Sets.newHashSet();

        Set<RoleEntity> roles = ((UserAuthBiz) AopContext.currentProxy()).findRoles(user);
        for (RoleEntity role : roles) {
            for (RoleResourcePermissionEntity rrp : role.getResourcePermissions()) {
                ResourceEntity resource = resourceBiz.findOne(rrp.getResourceId());

                String actualResourceIdentity = resourceBiz.findActualResourceIdentity(resource);
                //不可用 即没查到 或者标识字符串不存在
                if (resource == null || StringUtils.isEmpty(actualResourceIdentity) || Boolean.FALSE.equals(resource.getShow())) {
                    continue;
                }

                for (Long permissionId : rrp.getPermissionIds()) {
                    PermissionEntity permission = permissionBiz.findOne(permissionId);
                    //不可用
                    if (permission == null || Boolean.FALSE.equals(permission.getShow())) {
                        continue;
                    }

                    permissions.add(actualResourceIdentity + ":" + permission.getPermission());
                }
            }
        }
        return permissions;
    }
}
