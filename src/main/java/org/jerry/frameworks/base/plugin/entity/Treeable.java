package org.jerry.frameworks.base.plugin.entity;

import java.io.Serializable;

/**
 * 想要实现树功能的实体,请实现此接口.
 *
 * <p>Date : 16/5/13</p>
 * <p>Time : 上午11:35</p>
 *
 * @author jerry
 */
public interface Treeable<ID extends Serializable> {

    /**
     * 显示各级叶名称
     *
     * @return  标题
     */
    String getName();

    void setName(String name);

    /**
     * 获得显示图标 16 × 16
     *
     * @return  图标
     */
    String getIcon();

    void setIcon(String icon);

    /**
     * 父路径
     *
     * @return  父路径
     */
    ID getParentId();

    void setParentId(ID parentId);

    /**
     * 所有父路径 如1,2,3,
     *
     * @return  多级父路径
     */
    String getParentIds();

    void setParentIds(String parentIds);

    /**
     * 获取 parentIds 之间的分隔符
     *
     * @return  分隔符
     */
    String getSeparator();

    /**
     * 根据自身,构造出新的父节点路径
     *
     * @return  父节点路径
     */
    String makeSelfAsNewParentIds();

    /**
     * 权重 用于排序 越小越排在前边
     *
     * @return  权重
     */
    Integer getWeight();

    void setWeight(Integer weight);

    /**
     * 是否是根节点
     *
     * @return  true|false
     */
    boolean isRoot();

    /**
     * 是否是叶子节点
     *
     * @return  true|false
     */
    boolean isLeaf();

    /**
     * 是否存在子节点
     *
     * @return  true|false
     */
    boolean isHasChildren();

    /**
     * 根节点默认图标 如果没有默认 空即可  大小为16×16
     */
    String getRootDefaultIcon();

    /**
     * 树枝节点默认图标 如果没有默认 空即可  大小为16×16
     */
    String getBranchDefaultIcon();

    /**
     * 树叶节点默认图标 如果没有默认 空即可  大小为16×16
     */
    String getLeafDefaultIcon();
}
