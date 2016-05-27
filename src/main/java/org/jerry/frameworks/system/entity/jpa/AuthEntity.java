package org.jerry.frameworks.system.entity.jpa;


import com.google.common.collect.Sets;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.repository.hibernate.type.CollectionToStringUserType;
import org.jerry.frameworks.base.repository.support.annotation.EnableQueryCache;
import org.jerry.frameworks.system.entity.jpa.type.AuthType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

/**
 * The class is table <b>{@code "sys_auth"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@TypeDef(
        name = "SetToStringUserType",
        typeClass = CollectionToStringUserType.class,
        parameters = {
                @Parameter(name = "separator", value = ","),
                @Parameter(name = "collectionType", value = "java.util.hashSet"),
                @Parameter(name = "elementType", value = "java.lang.Long")
        }
)
@Entity
@Table(name = "sys_auth", schema = "eam")
@EnableQueryCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthEntity extends BaseEntity<Long> {

    @Column(name = "organization_id")
    private Long organizationId = 0L;

    @Column(name = "job_id")
    private Long jobId = 0L;

    @Column(name = "user_id")
    private Long userId = 0L;

    @Column(name = "group_id")
    private Long groupId = 0L;

    @Type(type = "SetToStringUserType")
    @Column(name = "role_ids")
    private Set<Long> roleIds;

    @Enumerated(value = EnumType.STRING)
    private AuthType type;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Set<Long> getRoleIds() {
        if (roleIds == null) {
            roleIds = Sets.newHashSet();
        }
        return roleIds;
    }

    public void addRoleId(Long roleId) {
        getRoleIds().add(roleId);
    }


    public void addRoleIds(Set<Long> roleIds) {
        getRoleIds().addAll(roleIds);
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public AuthType getType() {
        return type;
    }

    public void setType(AuthType type) {
        this.type = type;
    }
}