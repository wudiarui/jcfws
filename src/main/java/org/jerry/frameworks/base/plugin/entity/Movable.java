package org.jerry.frameworks.base.plugin.entity;

/**
 * <p>实体实现该接口表示想要调整数据的顺序
 * <p>优先级值越大则展示时顺序越靠前 比如 2 排在 1 前边
 * <p/>
 * <p>Date : 16/5/20</p>
 * <p>Time : 下午4:34</p>
 *
 * @author jerry
 */
public interface Movable {

    Integer getWeight();

    void setWeight(Integer weight);
}
