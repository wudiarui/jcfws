package org.jerry.frameworks.base.plugin.entity;

/**
 * <p>需要进行逻辑删除的实体,请实现该接口.</p>
 * <ul>假定约束列名为{@code 'deleted'},如想自定义列名,可以:
 * <li>1. Use annotations</li>
 * <li>2. Edit code {@code 'getColumn()'} </li> </ul>
 * <p>Date : 16/4/21</p>
 * <p>Time : 下午4:09</p>
 *
 * @author jerry
 */
public interface LogicDeleteable {
    /**
     * 抽象获取字段.
     * @return TRUE or FALSE
     */
    Boolean getDeleted();

    void setDeleted(Boolean deleted);

    /**
     * 标识为已删除.
     */
    void markDeleted();
}
