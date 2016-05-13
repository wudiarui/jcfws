package org.jerry.frameworks.base.repository.hibernate;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 根据 jpa api 获取hibernate相关api
 *
 * <p>Date : 16/5/12</p>
 * <p>Time : 下午3:54</p>
 *
 * @author jerry
 */
public class HibernateUtils {

    /**
     * 根据jpa EntityManager 获取 hibernate Session API
     *
     * @param entityManager jpa实体管理器
     * @return  hibernate session
     */
    public static Session getSession(EntityManager entityManager) {
        return (Session) entityManager.getDelegate();
    }

    /**
     * 根据jpa EntityManagerFactory 获取 hibernate SessionFactory API
     *
     * @param entityManagerFactory  jpa实体管理器工厂
     * @return  hibernate session factory
     */
    public static SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
        return ((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
    }

    /**
     * 根据jpa EntityManager 获取 hibernate SessionFactory API
     *
     * @param entityManager jpa实体管理者
     * @return  hibernate session factory
     */
    public static SessionFactory getSessionFactory(EntityManager entityManager) {
        return getSessionFactory(entityManager.getEntityManagerFactory());
    }

    /**
     * 根据jpa EntityManagerFactory 获取 hibernate Cache API
     *
     * @param entityManagerFactory  jpa实体管理者工厂
     * @return  hibernate cache
     */
    public static Cache getCache(EntityManagerFactory entityManagerFactory) {
        return getSessionFactory(entityManagerFactory).getCache();
    }

    /**
     * 根据jpa EntityManager 获取 hibernate Cache API
     *
     * @param entityManager jpa实体管理者
     * @return  hibernate cache
     */
    public static Cache getCache(EntityManager entityManager) {
        return getCache(entityManager.getEntityManagerFactory());
    }

    /**
     * 清空一级缓存
     *
     * @param entityManager jpa实体管理者
     */
    public static void evictLevel1Cache(EntityManager entityManager) {
        entityManager.clear();
    }

    /**
     * 根据jpa EntityManager 清空二级缓存
     *
     * @param entityManager jpa实体管理者
     */
    public static void evictLevel2Cache(EntityManager entityManager) {
        evictLevel2Cache(entityManager.getEntityManagerFactory());
    }

    /**
     * 根据jpa EntityManagerFactory 清空二级缓存 包括：
     * 1、实体缓存
     * 2、集合缓存
     * 3、查询缓存
     * 注意：
     * jpa Cache api 只能evict 实体缓存，其他缓存是删不掉的。。。
     *
     * @param entityManagerFactory  jpa实体管理者工厂
     */
    public static void evictLevel2Cache(EntityManagerFactory entityManagerFactory) {
        Cache cache = getCache(entityManagerFactory);
        cache.evictEntityRegions();
        cache.evictCollectionRegions();
        cache.evictDefaultQueryRegion();
        cache.evictQueryRegions();
        cache.evictNaturalIdRegions();
    }
}
