package org.jerry.frameworks.base.repository.callback;

import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.repository.callback.impl.DefaultSearchCallback;
import org.jerry.frameworks.base.repository.callback.impl.NoneSearchCallback;

import javax.persistence.Query;

/**
 * 条件检索的回调接口
 *
 * <p>Date : 16/4/27</p>
 * <p>Time : 下午4:29</p>
 *
 * @author jerry
 */
public interface SearchCallback {
    public static final SearchCallback NONE = new NoneSearchCallback();
    public static final SearchCallback DEFAULT = new DefaultSearchCallback();

    /**
     * 动态拼HQL where、group by having
     *
     * @param ql     jpql
     * @param search 条件
     */
    void prepareQL(StringBuilder ql, Searchable search);
    void prepareOrder(StringBuilder ql, Searchable search);

    /**
     * 根据search给query赋值及设置分页信息
     *
     * @param query
     * @param search
     */
    void setValues(Query query, Searchable search);

    void setPageable(Query query, Searchable search);
}
