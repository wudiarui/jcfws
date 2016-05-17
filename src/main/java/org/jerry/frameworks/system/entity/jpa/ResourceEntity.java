package org.jerry.frameworks.system.entity.jpa;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.jerry.frameworks.base.constants.IconConstants;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.plugin.entity.Treeable;
import org.jerry.frameworks.base.repository.support.annotation.EnableQueryCache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The class is table <b>{@code "sys_resource"}</b> mapping Entity by JPA generate.
 * <p>Date : 16/4/14</p>
 * <p>Time : 下午4:24</p>
 * @author jerry
 */
@Entity
@Table(name = "sys_resource", schema = "eam")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ResourceEntity extends BaseEntity<Long> implements Treeable<Long> {

    /**
     * 标题
     */
    private String name;

    /**
     * 资源标识符 用于权限匹配的 如sys:resource
     */
    private String identity;

    /**
     * 点击后前往的地址
     * 菜单才有
     */
    private String url;

    /**
     * 父路径
     */
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "parent_ids")
    private String parentIds;

    /**
     * 图标
     */
    private String icon;

    /**
     * 权重
     */
    private Integer weight;

    @Formula(value = "(select count(*) from sys_resource f_t where f_t.parent_id = id)")
    private boolean hasChildren;

    /**
     * 是否显示
     */
    @Column(name = "is_show")
    private Boolean isShow = Boolean.FALSE;

    public ResourceEntity() {}

    public ResourceEntity(Long id) {
        setId(id);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public String makeSelfAsNewParentIds() {
        return getParentIds() + getId() + getSeparator();
    }

    public String getTreetableIds() {
        String selfId = makeSelfAsNewParentIds().replace("/", "-");
        return selfId.substring(0, selfId.length() - 1);
    }

    public String getTreetableParentIds() {
        String parentIds = getParentIds().replace("/", "-");
        return parentIds.substring(0, parentIds.length() - 1);
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

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

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public boolean isRoot() {
        return getParentId() != null && getParentId() == 0;
    }


    @Override
    public boolean isLeaf() {
        return !isRoot() && !isHasChildren();

    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
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
}
