package org.jerry.frameworks.base.repository.support.annotation;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.*;

/**
 * 连接器
 *
 * <p>Date : 16/4/28</p>
 * <p>Time : 下午2:04</p>
 *
 * @author jerry
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryJoin {

    /**
     * 连接的名字
     *
     * @return 连接器属性
     */
    String property();

    JoinType joinType();
}
