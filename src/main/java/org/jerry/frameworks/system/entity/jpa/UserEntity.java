package org.jerry.frameworks.system.entity.jpa;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.base.constants.ValidateConstants;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.plugin.entity.LogicDeleteable;
import org.jerry.frameworks.base.repository.support.annotation.EnableQueryCache;
import org.jerry.frameworks.system.entity.jpa.type.UserState;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The Class is table <code>'sys_user'</code> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:18</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_user", schema = "eam")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserEntity extends BaseEntity<Long> implements LogicDeleteable {

    @NotNull(message = "{not.null}")
    @Pattern(regexp = ValidateConstants.USERNAME_PATTERN, message = "{user.username.not.valid}")
    private String username;

    @NotNull(message = "{not.null}")
    @Pattern(regexp = ValidateConstants.EMAIL_PATTERN, message = "{user.email.not.valid}")
    private String email;

    @NotNull(message = "{not.null}")
    @Pattern(regexp = ValidateConstants.MOBILE_PHONE_NUMBER_PATTERN, message = "{user.mobile.phone.number.not.valid}")
    @Column(name = "mobile_phone_number")
    private String mobilePhoneNumber;

    /**
     * 用户密码(Key-method:MD5:username + private key + salt)
     */
    @Length(min = ValidateConstants.PASSWORD_MIN_LENGTH,
        max = ValidateConstants.PASSWORD_MAX_LENGTH,
        message = "{user.password.not.valid")
    private String password;

    /**
     * 密盐
     */
    private String salt;

    @DateTimeFormat(pattern = Constants.DEFAULT_DATE_TIME_PATTERN)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @Enumerated(EnumType.STRING)
    private UserState status = UserState.normal;

    private Boolean deleted = Boolean.FALSE;
    private Boolean admin = Boolean.FALSE;

    /**
     * 用户 组织机构 工作职务关联表
     */
    @OneToMany(cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        targetEntity = UserOrganizationJobEntity.class,
        mappedBy = "user",
        orphanRemoval = true)
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    //集合缓存引起的
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OrderBy()
    private List<UserOrganizationJobEntity> organizationJobs;

    public UserEntity() {
    }

    public UserEntity(Long id) {
        setId(id);
    }

    public List<UserOrganizationJobEntity> getOrganizationJobs() {
        if (organizationJobs == null) {
            organizationJobs = Lists.newArrayList();
        }
        return organizationJobs;
    }

    public void addOrganizationJob(UserOrganizationJobEntity userOrganizationJob) {
        userOrganizationJob.setUser(this);
        getOrganizationJobs().add(userOrganizationJob);
    }

    public void setOrganizationJobs(List<UserOrganizationJobEntity> organizationJobs) {
        this.organizationJobs = organizationJobs;
    }

    /**
     * 非序列化可增变量,用于缓存机构职务。
     */
    private transient Map<Long, List<UserOrganizationJobEntity>> organizationJobsMap;

    @Transient
    public Map<Long, List<UserOrganizationJobEntity>> getDisplayOrganizationJobs() {
        if (organizationJobsMap != null)
            return organizationJobsMap;

        organizationJobsMap = Maps.newHashMap();

        for (UserOrganizationJobEntity userOrganizationJob : organizationJobs) {
            Long organizationId = userOrganizationJob.getOrganizationId();
            List<UserOrganizationJobEntity> userOrganizationJobList = organizationJobsMap.get(organizationId);
            if (userOrganizationJobList == null) {
                userOrganizationJobList = Lists.newArrayList();
                organizationJobsMap.put(organizationId, userOrganizationJobList);
            }
            userOrganizationJobList.add(userOrganizationJob);
        }
        return organizationJobsMap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void randomSalt() {
        setSalt(RandomStringUtils.randomAlphanumeric(10));
    }

    public UserState getStatus() {
        return status;
    }

    public void setStatus(UserState status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void markDeleted() {
        this.deleted = Boolean.TRUE;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
