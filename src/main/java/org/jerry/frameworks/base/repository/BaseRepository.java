package org.jerry.frameworks.base.repository;

import org.jerry.frameworks.base.entity.search.Searchable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * 扩展 JPA 的 {@link PagingAndSortingRepository} 接口, 实现逻辑删除功能.
 * <p>Date : 16/4/21</p>
 * <p>Time : 下午1:25</p>
 *
 * @author jerry
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * 根据IDs批量物理删除多个实体
     *
     * @param ids   实体ID集合
     */
    void delete(ID[] ids);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    List<T> findAll();

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    List<T> findAll(Sort sort);


    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable  分页
     * @return a page of entities
     */
    Page<T> findAll(Pageable pageable);

    /**
     * 根据条件查询所有
     * 条件 + 分页 + 排序
     *
     * @param searchable    条件
     * @return  全部
     */
    Page<T> findAll(Searchable searchable);


    /**
     * 根据条件统计所有记录数
     *
     * @param searchable    条件
     * @return  全部
     */
    long count(Searchable searchable);
}
