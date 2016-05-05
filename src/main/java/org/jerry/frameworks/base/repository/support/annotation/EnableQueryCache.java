package org.jerry.frameworks.base.repository.support.annotation;

import java.lang.annotation.*;

/**
 * 开启查询缓存
 *
 * <p>Date : 16/4/28</p>
 * <p>Time : 下午2:01</p>
 *
 * @author jerry
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableQueryCache {

    boolean value() default true;
}
