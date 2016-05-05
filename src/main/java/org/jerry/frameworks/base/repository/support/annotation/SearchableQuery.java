package org.jerry.frameworks.base.repository.support.annotation;

import org.jerry.frameworks.base.repository.callback.SearchCallback;

import java.lang.annotation.*;

/**
 * 覆盖默认的根据条件查询数据
 *
 * <p>Date : 16/4/28</p>
 * <p>Time : 下午2:07</p>
 *
 * @author jerry
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchableQuery {

    /**
     * 覆盖默认的查询所有ql
     *
     * @return
     */
    String findAllQuery() default "";

    /**
     * 覆盖默认的统计所有ql
     *
     * @return
     */
    String countAllQuery() default "";

    /**
     * 给ql拼条件及赋值的回调类型
     *
     * @return org.jerry.framework.base.repository.callback.SearchCallback子类
     */
    Class<? extends SearchCallback> callbackClass() default SearchCallback.class;

    QueryJoin[] joins() default {};
}
