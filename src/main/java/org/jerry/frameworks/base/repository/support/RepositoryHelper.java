package org.jerry.frameworks.base.repository.support;

import org.jerry.frameworks.base.entity.AbstractEntity;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.repository.callback.SearchCallback;
import org.jerry.frameworks.base.repository.support.annotation.EnableQueryCache;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * 仓库辅助类
 *
 * <p>Date : 16/4/28</p>
 * <p>Time : 下午2:11</p>
 *
 * @author jerry
 */
public class RepositoryHelper {

    private static EntityManager entityManager;
    private Class<?> entityClass;
    private boolean enableQueryCache = false;

    /**
     * @param entityClass 是否开启查询缓存
     */
    public RepositoryHelper(Class<?> entityClass) {
        this.entityClass = entityClass;

        EnableQueryCache enableQueryCacheAnnotation =
                AnnotationUtils.findAnnotation(entityClass, EnableQueryCache.class);

        boolean enableQueryCache = false;
        if (enableQueryCacheAnnotation != null) {
            enableQueryCache = enableQueryCacheAnnotation.value();
        }
        this.enableQueryCache = enableQueryCache;
    }

    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    public static EntityManager getEntityManager() {
        Assert.notNull(entityManager, "entityManager must null, please see " + entityManager.getClass().getSimpleName());

        return entityManager;
    }

    public static void flush() {
        getEntityManager().flush();
    }

    public static void clear() {
        flush();
        getEntityManager().flush();
    }

    /**
     * 根据查询条件分页查询.
     *
     * @param <T>               被查询的实体
     * @param ql                查询语句
     * @param searchable        查询条件|分页|排序
     * @param searchCallback    查询回调  自定义设置查询条件和赋值
     * @return                  返回查询结果集
     */
    public <T> List<T> findAll(final String ql, final Searchable searchable, final SearchCallback searchCallback) {
        assertConverted(searchable);
        StringBuilder sb = new StringBuilder(ql);
        searchCallback.prepareQL(sb, searchable);
        searchCallback.prepareOrder(sb, searchable);

        Query query = getEntityManager().createQuery(sb.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);

        List resultList = query.getResultList();

        return resultList;
    }

    /**
     * <p>按条件统计<br/>
     *
     * @param ql                查询语句
     * @param searchable        查询条件|分页|排序
     * @param searchCallback    查询回调  自定义设置查询条件和赋值
     * @return                  统计条目总数
     */
    public long count(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        assertConverted(searchable);

        StringBuilder sb = new StringBuilder(ql);
        searchCallback.prepareQL(sb, searchable);
        Query query = getEntityManager().createQuery(sb.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);

        return (Long)query.getSingleResult();
    }

    /**
     * 按条件查询一个实体
     *
     * @param ql                查询语句
     * @param searchable        查询条件|分页|排序
     * @param searchCallback    查询回调  自定义设置查询条件和赋值
     * @param <T>               被查询的实体
     * @return                  查询到的实体
     */
    public <T> T findOne(final String ql, final Searchable searchable, final SearchCallback searchCallback) {
        assertConverted(searchable);

        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);
        query.setMaxResults(1);
        List<T> result = query.getResultList();

        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * <p>根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页<br/>
     *
     * @param ql        查询语句
     * @param pageable  为null,表示不分页.
     * @param params    可变长参数,表示可以有多个参数
     * @param <T>       强制转型的实体
     * @return          实体结果集
     */
    public <T> List<T> findAll(final String ql, final Pageable pageable, final Object... params) {

        Query query = getEntityManager().createQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
        applyEnableQueryCache(query);
        setParameters(query, params);
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList();
    }

    /**
     * 不带分页
     *
     * @param ql        查询语句
     * @param params    可变长参数,表示可以有多个参数
     * @param <T>       强制转型的实体
     * @return          实体结果集
     */
    public <T> List<T> findAll(final String ql, final Object... params) {

        //此处必须 (Pageable) null  否则默认有调用自己了 可变参列表
        return findAll(ql, (Pageable) null, params);

    }

    /**
     * <p>根据ql和按照索引顺序的params执行ql，sort存储排序信息 null表示不排序<br/>
     *
     * @param ql        查询语句
     * @param sort      排序
     * @param params    可变长参数,表示可以有多个参数
     * @param <T>       强制转型的实体
     * @return          实体结果集
     */
    public <T> List<T> findAll(final String ql, final Sort sort, final Object... params) {
        Query query = getEntityManager().createQuery(ql + prepareOrder(sort));
        applyEnableQueryCache(query);
        setParameters(query, params);

        return query.getResultList();
    }

    /**
     * <p>根据ql和按照索引顺序的params查询一个实体<br/>
     *
     * @param ql        查询语句
     * @param params    可变长参数,表示可以有多个参数
     * @param <T>       强制转型的实体
     * @return          查询到的实体
     */
    public <T> T findOne(final String ql, final Object... params) {
        List<T> list = findAll(ql, new PageRequest(0, 1), params);

        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * <p>统计<br/>
     *
     * @param ql        查询语句
     * @param params    可变长参数,表示可以有多个参数
     * @return          统计条目总数
     */
    public long count(final String ql, final Object... params) {

        Query query = entityManager.createQuery(ql);
        applyEnableQueryCache(query);
        setParameters(query, params);

        return (Long) query.getSingleResult();
    }

    /**
     * <p>执行批处理语句.如 之间insert, update, delete 等.<br/>
     *
     * @param ql        查询语句
     * @param params    可变长参数,表示可以有多个参数
     * @return          更新的条目数
     */
    public int batchUpdate(final String ql, final Object... params) {
        Query query = getEntityManager().createQuery(ql);
        setParameters(query, params);

        return query.executeUpdate();
    }

    /**
     * 按顺序设置Query参数
     *
     * @param query     查询对象
     * @param params    条件数组
     */
    public void setParameters(Query query, Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
    }

    /**
     * 拼排序
     *
     * @param sort  排序
     * @return      排序字符串
     */
    public String prepareOrder(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return "";
        }
        StringBuilder orderBy = new StringBuilder("");
        orderBy.append(" order by ");
        orderBy.append(sort.toString().replace(":", " "));
        return orderBy.toString();
    }

    /**
     * 断言是否已经被转成实体属性
     *
     * @param searchable    查询条件
     */
    private void assertConverted(Searchable searchable) {
        if (!searchable.isConverted()) {
            searchable.convert(this.entityClass);
        }
    }

    /**
     * 追加缓存
     *
     * @param query     对查询启用
     */
    public void applyEnableQueryCache(Query query) {
        if (enableQueryCache) {
            query.setHint("org.hibernate.cacheable", true);//开启查询缓存
        }
    }

    /**
     * 获得实体信息
     *
     * @param entityClass   实体类
     * @param <T>           强制类型转换
     * @return              实体类的信息
     */
    public <T> JpaEntityInformation<T, ?> getMetadata(Class<T> entityClass) {
        return JpaEntityInformationSupport.getEntityInformation(entityClass, entityManager);
    }
}
