package org.jerry.frameworks.base.plugin.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.entity.search.SearchOperator;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.entity.search.filter.SearchFilter;
import org.jerry.frameworks.base.entity.search.filter.SearchFilterHelper;
import org.jerry.frameworks.base.plugin.entity.Treeable;
import org.jerry.frameworks.base.repository.RepositoryHelper;
import org.jerry.frameworks.base.utils.ReflectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午4:54</p>
 *
 * @author jerry
 */
public abstract class BaseTreeableService<T extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable>
        extends BaseBiz<T, ID> {
    private final String DELETE_CHILDREN_QL;
    private final String UPDATE_CHILDREN_PARENT_IDS_QL;
    private final String FIND_SELF_AND_NEXT_SIBLINGS_QL;
    private final String FIND_NEXT_WEIGHT_QL;

    private RepositoryHelper repositoryHelper;

    public BaseTreeableService() {
        Class<T> entityClass = ReflectUtils.findParameterizedType(getClass(), 0);
        repositoryHelper = new RepositoryHelper(entityClass);
        String entityName = repositoryHelper.getEntityName(entityClass);

        DELETE_CHILDREN_QL = String.format("delete from %s where id=?1 or parentId like concat(?2, %s)", entityName, "'%'");
        UPDATE_CHILDREN_PARENT_IDS_QL = String.format(
                "update %s set parentId=(?1 || substring(parentIds, length(?2) + 1)) where parentId like concat(?2, %s)", entityName, "'%'");
        FIND_SELF_AND_NEXT_SIBLINGS_QL = String.format(
                "from %s where parentId=?1 and weight>=?2 order by weight asc", entityName);
        FIND_NEXT_WEIGHT_QL = String.format(
                "select case when max(weight) is null then 1 else (max(weight) + 1) end from %s where parentId=?1", entityName);
    }

    /**
     * 保存
     *
     * @param t   实体
     * @return    执行存储后实体
     */
    @Override
    public T save(T t) {
        if (t.getWeight() == null) {
            t.setWeight(nextWeight(t.getParentId()));
        }
        return super.save(t);
    }

    /**
     * 获得下一级weight
     *
     * @param id    实体ID
     * @return      下一级weight
     */
    public int nextWeight(ID id) {
        return repositoryHelper.<Integer>findOne(FIND_NEXT_WEIGHT_QL, id);
    }

    /**
     * 删除自己及所有子节点
     *
     * @param t    self
     */
    @Transactional
    public void deleteSelfAndChild(T t) {
        repositoryHelper.batchUpdate(DELETE_CHILDREN_QL, t.getId(), t.makeSelfAsNewParentIds());
    }

    /**
     * batch delete node of T self and T under the node.
     *
     * @param tList     NODE实体集
     */
    public void deleteSelfAndChild(List<T> tList) {
        for (T t : tList) {
            deleteSelfAndChild(t);
        }
    }

    /**
     * 追加子节点
     *
     * @param parent    父节点
     * @param child     子节点
     */
    public void appendChild(T parent, T child) {
        child.setParentId(parent.getId());
        child.setParentIds(parent.getParentIds());
        child.setWeight(nextWeight(parent.getId()));
        save(child);
    }

    /**
     * 移动节点
     * 根节点不能移动
     *
     * @param source   源节点
     * @param target   目标节点
     * @param moveType 位置
     */
    public void move(T source, T target, String moveType) {
        if (source == null || target == null || source.isRoot()) { //根节点不能移动
            return;
        }

        // 相邻子妹节点,交替位置即可
        boolean isSibling = source.getParentId().equals(target.getParentId());
        boolean isNextOrPrevMoveType = "next".equals(moveType) || "prev".equals(moveType);
        if (isSibling && isNextOrPrevMoveType && Math.abs(source.getWeight() - target.getWeight()) == 1) {
            //无需移动
            if ("next".equals(moveType) && source.getWeight() > target.getWeight()) {
                return;
            }
            if ("prev".equals(moveType) && source.getWeight() < target.getWeight()) {
                return;
            }
            int sourceWeight = source.getWeight();
            source.setWeight(target.getWeight());
            target.setWeight(sourceWeight);
            return;
        }

        // 移动到目标节点之后
        if ("next".equals(moveType)) {
            List<T> siblings = findSelfAndNextSiblings(target.getParentIds(), target.getWeight());
            siblings.remove(0);     // 把自己删除, order by weight asc第一个就是自已

            if (siblings.size() == 0) { // 如果没用兄弟了, 直接设置源为目标
                int nextWeight = nextWeight(target.getParentId());
                updateSelfAndChild(source, target.getParentId(), target.getParentIds(), nextWeight);
                return;
            } else {
                moveType = "prev";      // 跳到下一代码块进行处理
                target = siblings.get(0);
            }
        }

        // 移动到目标节点之前
        if ("prev".equals(moveType)) {
            List<T> siblings = findSelfAndNextSiblings(target.getParentIds(), target.getWeight());
            //兄弟节点中包含源节点
            if (siblings.contains(source)) {
                // 1 2 [3 source] 4
                siblings = siblings.subList(0, siblings.indexOf(source) + 1);       // 截取源节点之前的
                int firstWeight = siblings.get(0).getWeight();
                for (int i = 0; i < siblings.size() - 1; i++) {
                    siblings.get(i).setWeight(siblings.get(i + 1).getWeight());     // 往后排
                }
                siblings.get(siblings.size() - 1).setWeight(firstWeight);
            } else {
                // 1 2 3 4  [5 new]
                int nextWeight = nextWeight(target.getParentId());
                int firstWeight = siblings.get(0).getWeight();
                for (int i = 0; i < siblings.size() - 1; i++) {
                    siblings.get(i).setWeight(siblings.get(i + 1).getWeight());
                }
                siblings.get(siblings.size() - 1).setWeight(nextWeight);
                source.setWeight(firstWeight);
                updateSelfAndChild(source, target.getParentId(), target.getParentIds(), source.getWeight());
            }
            return;
        }
        //否则作为最后孩子节点
        int nextWeight = nextWeight(target.getId());
        updateSelfAndChild(source, target.getId(), target.makeSelfAsNewParentIds(), nextWeight);
    }

    /**
     * 查找目标节点及之后的兄弟  注意：值与越小 越排在前边
     *
     * @param parentIds     节点所有的父节点串
     * @param currentWeight 当前权重
     * @return 节点及之后的兄弟节点
     */
    protected List<T> findSelfAndNextSiblings(String parentIds, int currentWeight) {
        return repositoryHelper.<T>findAll(FIND_SELF_AND_NEXT_SIBLINGS_QL, parentIds, currentWeight);
    }

    /**
     * 把源节点全部变更为目标节点
     *
     * @param source       源节点实体
     * @param newParentId  新的父节点ID
     * @param newParentIds 新的父节点ID串
     * @param newWeight    新的级别权重
     */
    private void updateSelfAndChild(T source, ID newParentId, String newParentIds, int newWeight) {
        String oldSourceChildrenParentIds = source.makeSelfAsNewParentIds();
        source.setParentId(newParentId);
        source.setParentIds(newParentIds);
        source.setWeight(newWeight);
        String newSourceChildrenParentIds = source.makeSelfAsNewParentIds();
        repositoryHelper.batchUpdate(UPDATE_CHILDREN_PARENT_IDS_QL, newSourceChildrenParentIds, oldSourceChildrenParentIds);
    }

    /**
     * 查看与name模糊匹配的名称
     *
     * @param name 查询名称
     * @return 模糊匹配的名称
     */
    public Set<String> findNames(Searchable searchable, String name, ID excludeId) {
        T t = findOne(excludeId);

        searchable.addSearchFilter("name", SearchOperator.like, name);
        addExcludeSearchFilter(searchable, t);

        return Sets.newHashSet(
                Lists.transform(
                        findAll(searchable).getContent(),
                        new Function<T, String>() {
                            @Override
                            public String apply(T t) {
                                return t.getName();
                            }
                        }
                )
        );
    }

    /**
     * 查询子子孙孙
     *
     * @return 子孙节点列表
     */
    public List<T> findChildren(List<T> parents, Searchable searchable) {
        if (parents.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        SearchFilter first = SearchFilterHelper.newCondition("parentIds", SearchOperator.prefixLike,
                    parents.get(0).makeSelfAsNewParentIds());

        SearchFilter[] others = new SearchFilter[parents.size() - 1];

        for (int i = 1; i < parents.size(); i++) {
            others[i - 1] = SearchFilterHelper.newCondition("parentIds", SearchOperator.prefixLike,
                    parents.get(i).makeSelfAsNewParentIds());
        }
        searchable.or(first, others);

        List<T> childList = findAllWithSort(searchable);
        return childList;
    }

    /**
     * 查找根和一级节点
     *
     * @param searchable    查询条件
     * @return              根和一级节点列表
     */
    public List<T> findRootAndChild(Searchable searchable) {
        searchable.addSearchParam("parentId_eq", 0);    // query the root
        List<T> models = findAllWithSort(searchable);

        if (models.size() == 0)
            return models;
        List<ID> ids = Lists.newArrayList();
        for (int i = 0; i < models.size(); i++) {
            ids.add(models.get(i).getId());
        }
        searchable.removeSearchFilter("parentId_eq");
        searchable.addSearchParam("parentId_in", ids);

        models.addAll(findAllWithSort(searchable));
        return models;
    }

    /**
     * 根据当前节点集合,查找祖先ID(多级父IDs)
     *
     * @param currentIds    当前节点ID集合
     * @return              批量查询后的多级父IDs
     */
    public Set<ID> findAncestorIds(Iterable<ID> currentIds) {
        Set<ID> parents = Sets.newHashSet();
        for (ID currentId : currentIds) {
            parents.addAll(findAncestorIds(currentId));
        }

        return parents;
    }

    /**
     * 根据当前节点ID,查找祖先ID(多级父IDs)
     *
     * @param currentId 当前节点ID
     * @return          多级父IDs
     */
    public Set<ID> findAncestorIds(ID currentId) {
        Set ids = Sets.newHashSet();
        T t = findOne(currentId);
        if (t == null) {
            return ids;
        }
        for (String idStr : StringUtils.tokenizeToStringArray(t.getParentIds(), "/")) {
            if (!StringUtils.isEmpty(idStr)) {
                ids.add(Long.valueOf(idStr));
            }
        }
        return ids;
    }

    /**
     * 根据父级节点串,递归查找祖先ID(多级父IDs)
     *
     * @param parentIds 父节点串
     * @return          祖先ID(多级父IDs)
     */
    public List<T> findAncestor(String parentIds) {
        if (StringUtils.isEmpty(parentIds)) {
            return Collections.EMPTY_LIST;
        }
        String[] ids = StringUtils.tokenizeToStringArray(parentIds, "/");

        return Lists.reverse(findAllWithNoPageNoSort(Searchable.newSearchable().addSearchFilter("id", SearchOperator.in, ids)));
    }

    public List<T> findAllByName(Searchable searchable, T t) {
        addExcludeSearchFilter(searchable, t);
        return findAllWithSort(searchable);
    }

    public void addExcludeSearchFilter(Searchable searchable, T excludeM) {
        if (excludeM == null) {
            return;
        }
        searchable.addSearchFilter("id", SearchOperator.ne, excludeM.getId());
        searchable.addSearchFilter("parentIds", SearchOperator.suffixNotLike, excludeM.makeSelfAsNewParentIds());
    }
}
