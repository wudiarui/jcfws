package org.jerry.frameworks.system.entity.jpa;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.jerry.frameworks.base.constants.IconConstants;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.plugin.entity.Treeable;
import org.jerry.frameworks.system.entity.jpa.emun.OrganizationType;

import javax.persistence.*;

/**
 * The class is table <b>{@code "sys_organization"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_organization", schema = "eam")
public class OrganizationEntity extends BaseEntity<Long> implements Treeable<Long> {

    private String name;

    @Enumerated(EnumType.STRING)
    private OrganizationType type;
    @Column(name = "parent_id")
    private Long parentId;
    @Column(name = "parent_ids")
    private String parentIds;

    private String icon;
    private Integer weight;

    /**
     * 是否有叶子节点
     */
    @Formula(value = "(select count(*) from sys_organization f_t where f_t.parent_id = id)")
    private boolean hasChildren;

    @Column(name = "is_show")
    private Boolean isShow = Boolean.FALSE;

    public OrganizationEntity() {
    }

    public OrganizationEntity(Long id) {
        setId(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public String getParentIds() {
        return parentIds;
    }

    @Override
    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public String makeSelfAsNewParentIds() {
        return getParentIds() + getId() + getSeparator();
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getIcon() {
        if (!StringUtils.isEmpty(icon)) {
            return icon;
        }
        if (isRoot()) {
            return getRootDefaultIcon();
        }
        if (isLeaf()) {
            return getLeafDefaultIcon();
        }
        return getBranchDefaultIcon();
    }

    @Override
    public boolean isRoot() {
        return getParentId() != null && getParentId() == 0;
    }

    @Override
    public boolean isLeaf() {
        return !isRoot() && !isHasChildren();

    }

    @Override
    public boolean isHasChildren() {
        return hasChildren;
    }

    /**
     * 根节点默认图标 如果没有默认 空即可
     *
     * @return  根节点默认图标
     */
    @Override
    public String getRootDefaultIcon() {
        return IconConstants.DEFAULT_TREE_ROOT_ICON;
    }

    /**
     * 树枝节点默认图标 如果没有默认 空即可
     *
     * @return  树枝节点默认图标
     */
    @Override
    public String getBranchDefaultIcon() {
        return IconConstants.DEFAULT_TREE_BRANCH_ICON;
    }

    /**
     * 树叶节点默认图标 如果没有默认 空即可
     *
     * @return  树叶节点默认图标
     */
    @Override
    public String getLeafDefaultIcon() {
        return IconConstants.DEFAULT_TREE_LEAF_ICON;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }
}
