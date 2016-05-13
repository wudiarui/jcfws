package org.jerry.frameworks.base.repository;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.plugin.entity.LogicDeleteable;
import org.jerry.frameworks.base.repository.BaseRepository;
import org.jerry.frameworks.base.repository.callback.SearchCallback;
import org.jerry.frameworks.base.repository.RepositoryHelper;
import org.jerry.frameworks.base.repository.support.annotation.QueryJoin;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;

/**
 * 扩展{@link SimpleJpaRepository}, 作用于补充Spring Data处理不了的特殊需求.
 * <p>Date : 16/4/21</p>
 * <p>Time : 下午1:33</p>
 *
 * @author jerry
 */
public class SimpleBaseRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {
    public static final String LOGIC_DELETE_ALL_QUERY_STRING = "update %s x set x.deleted=true where x in (?1)";
    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x where x in (?1)";
    public static final String FIND_QUERY_STRING = "from %s x where 1=1 ";
    public static final String FIND_QUERY_STRING_AS_LOGIC_DELETE = "from %s x where x.deleted=false ";
    public static final String COUNT_QUERY_STRING = "select count(x) from %s x where 1=1 ";
    public static final String COUNT_QUERY_STRING_AS_LOGIC_DELETE = "select count(x) from %s x where x.deleted=false ";

    private final EntityManager entityManager;

    private Class<T> entityClass;

    private final JpaEntityInformation<T, ID> entityInformation;

    private final RepositoryHelper repositoryHelper;

    private String entityName;
    private String idName;


    /**
     * 查询所有的QL
     */
    private String findAllQL;
    /**
     * 统计QL
     */
    private String countAllQL;

    private QueryJoin[] joins;

    private SearchCallback searchCallback = SearchCallback.DEFAULT;

    public SimpleBaseRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        this.entityClass = this.entityInformation.getJavaType();
        this.entityName = this.entityInformation.getEntityName();
        this.idName = this.entityInformation.getIdAttributeNames().iterator().next();

        repositoryHelper = new RepositoryHelper(entityClass);
        T t = null;
        try {
            t = entityClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (t instanceof LogicDeleteable) {
            findAllQL = String.format(FIND_QUERY_STRING_AS_LOGIC_DELETE, entityName);
            countAllQL = String.format(COUNT_QUERY_STRING_AS_LOGIC_DELETE, entityName);
        } else {
            findAllQL = String.format(FIND_QUERY_STRING, entityName);
            countAllQL = String.format(COUNT_QUERY_STRING, entityName);
        }
    }

    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    public void setFindAllQL(String findAllQL) {
        this.findAllQL = findAllQL;
    }

    public void setCountAllQL(String countAllQL) {
        this.countAllQL = countAllQL;
    }

    public void setJoins(QueryJoin[] joins) {
        this.joins = joins;
    }

    /////////////////////////////////////////////////
    ////////覆盖默认spring data jpa的实现////////////
    /////////////////////////////////////////////////

    /**
     * 根据实体id, 删除实体
     *
     * @param id    实体ID
     */
    @Transient
    @Override
    public void delete(final ID id) {
        T t = findOne(id);
        delete(t);
    }

    /**
     * 删除实体.如果实现过{@link LogicDeleteable}, 那么执行logic Delete, 否则执行删除.
     *
     * @param t     实体对象
     */
    @Transient
    @Override
    public void delete(T t) {
        if (t == null) return;
        if(t instanceof LogicDeleteable) {
            ((LogicDeleteable) t).markDeleted();
            save(t);
        } else {
            delete(t);
        }
    }

    /**
     * 根据实体ID集合删除相应实体
     *
     * @param ids   实体ID集合
     */
    @Transient
    @Override
    public void delete(ID[] ids) {
        if (ArrayUtils.isEmpty(ids)) return;
        List<T> models = new ArrayList<>();

        for (ID id : ids) {
            T model;
            try {
                model = entityClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("batch delete operate " + entityClass + "error.", e);
            }
            try {
                BeanUtils.setProperty(model, idName, id);
            } catch (Exception e) {
                throw new RuntimeException("batch delete operate " + entityClass + "error, can not set id.", e);
            }
            models.add(model);
        }
        deleteInBatch(models);
    }

    @Transient
    @Override
    public void deleteInBatch(final Iterable<T> entities) {
        Iterator<T> iterator = entities.iterator();
        if (!iterator.hasNext()) return;

        Set models = Sets.newHashSet(iterator);

        boolean logicDeleteableEntity = LogicDeleteable.class.isAssignableFrom(this.entityClass);

        if (logicDeleteableEntity) {
            String ql = String.format(LOGIC_DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        } else {
            String ql = String.format(DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        }
    }

    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    @Transient
    @Override
    public T findOne(ID id) {
        if (id == null) return null;
        if (id instanceof Integer && (Integer) id == 0) {
            return null;
        }
        if (id instanceof Long && (Long) id == 0) {
            return null;
        }
        return super.findOne(id);
    }

    ////////根据Specification查询 直接从SimpleJpaRepository复制过来的///////////////////////////////////
    @Override
    public T findOne(Specification<T> spec) {
        try {
            return getQuery(spec, (Sort) null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(ID[])
     */
    public List<T> findAll(Iterable<ID> ids) {

        return getQuery(new Specification<T>() {
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<?> path = root.get(entityInformation.getIdAttribute());
                return path.in(cb.parameter(Iterable.class, "ids"));
            }
        }, (Sort) null).setParameter("ids", ids).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification)
     */
    public List<T> findAll(Specification<T> spec) {
        return getQuery(spec, (Sort) null).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
     */
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {

        TypedQuery<T> query = getQuery(spec, pageable);
        return pageable == null ? new PageImpl<>(query.getResultList()) : readPage(query, pageable, spec);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Sort)
     */
    public List<T> findAll(Specification<T> spec, Sort sort) {

        return getQuery(spec, sort).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#count(org.springframework.data.jpa.domain.Specification)
     */
    public long count(Specification<T> spec) {

        return getCountQuery(spec).getSingleResult();
    }

    ///////直接从SimpleJpaRepository复制过来的///////////////////////////////

    /**
     * Reads the given {@link javax.persistence.TypedQuery} into a {@link org.springframework.data.domain.Page} applying the given {@link org.springframework.data.domain.Pageable} and
     * {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param query    must not be {@literal null}.
     * @param spec     can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery(getCountQuery(spec));
        List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T>emptyList();

        return new PageImpl<T>(content, pageable, total);
    }

    /**
     * Executes a count query and transparently sums up all values returned.
     *
     * @param query must not be {@literal null}.
     * @return
     */
    private static Long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query);

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    /**
     * Creates a new count query for the given {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param spec can be {@literal null}.
     * @return
     */
    protected TypedQuery<Long> getCountQuery(Specification<T> spec) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);


        Root<T> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        TypedQuery<Long> q = entityManager.createQuery(query);
        repositoryHelper.applyEnableQueryCache(q);
        return q;
    }

    /**
     * Creates a new {@link javax.persistence.TypedQuery} from the given {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param spec     can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {

        Sort sort = pageable == null ? null : pageable.getSort();
        return getQuery(spec, sort);
    }


    private void applyJoins(Root<T> root) {
        if(joins == null) {
            return;
        }

        for(QueryJoin join : joins) {
            root.join(join.property(), join.joinType());
        }
    }


    /**
     * Applies the given {@link org.springframework.data.jpa.domain.Specification} to the given {@link javax.persistence.criteria.CriteriaQuery}.
     *
     * @param spec  can be {@literal null}.
     * @param query must not be {@literal null}.
     * @return
     */
    private <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<T> root = query.from(entityClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

//    private TypedQuery<M> applyLockMode(TypedQuery<M> query) {
//        LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider.getLockModeType();
//        return type == null ? query : query.setLockMode(type);
//    }
    ///////直接从SimpleJpaRepository复制过来的///////////////////////////////

    @Override
    public List<T> findAll() {
        return repositoryHelper.findAll(findAllQL);
    }

    @Override
    public List<T> findAll(final Sort sort) {
        return repositoryHelper.findAll(findAllQL, sort);
    }

    @Override
    public Page<T> findAll(final Pageable pageable) {
        return new PageImpl<>(
                repositoryHelper.<T>findAll(findAllQL, pageable),
                pageable,
                repositoryHelper.count(countAllQL)
        );
    }

    @Override
    public long count() {
        return repositoryHelper.count(countAllQL);
    }

    /////////////////////////////////////////////////
    ///////////////////自定义实现////////////////////
    /////////////////////////////////////////////////

    @Override
    public long count(Searchable searchable) {
        return repositoryHelper.count(countAllQL, searchable, searchCallback);
    }

    @Override
    public Page<T> findAll(final Searchable searchable) {
        List<T> list = repositoryHelper.findAll(findAllQL, searchable, searchCallback);
        long total = searchable.hasPageable() ? count(searchable) : list.size();
        return new PageImpl<>(
                list,
                searchable.getPage(),
                total
        );
    }

    /**
     * 重写默认的 这样可以走一级/二级缓存
     *
     * @param id    实体ID
     * @return      是否存在缓存
     */
    @Override
    public boolean exists(ID id) {
        return findOne(id) != null;
    }
}
