package org.jerry.frameworks.base.plugin.service;

import com.google.common.collect.Maps;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.plugin.entity.Movable;
import org.jerry.frameworks.base.repository.RepositoryHelper;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>Date : 16/5/20</p>
 * <p>Time : 下午4:26</p>
 *
 * @author jerry
 */
public abstract class BaseMovableService<T extends BaseEntity & Movable, ID extends Serializable>
        extends BaseBiz<T, ID> {

    //权重的步长
    private final Integer stepLength;

    /**
     * 默认步长1000
     */
    protected BaseMovableService() {
        this(1000);
    }

    protected BaseMovableService(Integer stepLength) {
        this.stepLength = stepLength;
    }

    public Integer getStepLength() {
        return stepLength;
    }

    @Override
    public T save(T t) {
        if (t.getWeight() == null) {
            t.setWeight(findNextWeight());
        }
        return super.save(t);
    }

    /**
     * 按照降序进行移动
     * 把{fromId}移动到{}toId}之后
     * 如 fromWeight 2000 toWeight 1000   则新的为 500
     *
     * @param fromId
     * @param toId
     */
    public void down(ID fromId, ID toId) {
        T from = findOne(fromId);
        T to = findOne(toId);
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();


        T nextTo = findNextByWeight(toWeight);

        //如果toId的下一个是fromId 则直接交换顺序即可
        if (from.equals(nextTo)) {
            from.setWeight(toWeight);
            to.setWeight(fromWeight);
            return;
        }

        int minWeight = Math.min(fromWeight, toWeight);
        int maxWeight = Math.max(fromWeight, toWeight);
        //作为一个优化，减少不连续的weight
        int count = countByBetween(minWeight, maxWeight).intValue();
        if (count > 0 && count < 20) {
            List<T> moves = findByBetweenAndAsc(minWeight, maxWeight);
            if (fromWeight < toWeight) {
                Integer swapInteger = moves.get(count - 2).getWeight();
                for (int i = count - 2; i >= 1; i--) {
                    //最后一个的weight = toWeight;
                    moves.get(i).setWeight(moves.get(i - 1).getWeight());
                }
                moves.get(0).setWeight(swapInteger);
            } else {
                for (int i = 0; i <= count - 2; i++) {
                    moves.get(i).setWeight(moves.get(i + 1).getWeight());
                }
                moves.get(count - 1).setWeight(minWeight);
            }
            return;
        }

        T preTo = findPreByWeight(toWeight);

        //计算新的权重
        int newWeight = 0;
        if (preTo == null) {
            newWeight = toWeight / 2;
        } else {
            newWeight = toWeight - (toWeight - preTo.getWeight()) / 2;

        }

        if (Math.abs(newWeight - toWeight) <= 1) {
            throw new IllegalStateException(String.format("up error, no enough weight :fromId:%d, toId:%d", fromId, toId));
        }
        from.setWeight(newWeight);

    }

    /**
     * 按照降序进行移动
     * 把{fromId}移动到toId之下
     * 如 fromWeight 1000 toWeight 2000  3000 则新的为 2500
     *
     * @param fromId    源ID
     * @param toId      目标ID
     */
    public void up(ID fromId, ID toId) {
        T from = findOne(fromId);
        T to = findOne(toId);
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        Integer fromWeight = from.getWeight();
        Integer toWeight = to.getWeight();


        T preTo = findPreByWeight(toWeight);
        //如果toId的下一个是fromId 则直接交换顺序即可
        if (from.equals(preTo)) {
            from.setWeight(toWeight);
            to.setWeight(fromWeight);
            return;
        }

        int minWeight = Math.min(fromWeight, toWeight);
        int maxWeight = Math.max(fromWeight, toWeight);
        //作为一个优化，减少不连续的weight
        int count = countByBetween(minWeight, maxWeight).intValue();
        if (count > 0 && count < 20) {
            List<T> moves = findByBetweenAndDesc(minWeight, maxWeight);
            //123/124
            //5000 4000 3000

            if (fromWeight > toWeight) {
                Integer swapInteger = moves.get(count - 2).getWeight();
                for (int i = count - 2; i >= 1; i--) {
                    //最后一个的weight = toWeight;
                    moves.get(i).setWeight(moves.get(i - 1).getWeight());
                }
                moves.get(0).setWeight(swapInteger);
            } else {
                for (int i = 0; i <= count - 2; i++) {
                    moves.get(i).setWeight(moves.get(i + 1).getWeight());
                }
                moves.get(count - 1).setWeight(maxWeight);
            }
            return;
        }

        //如果toId的下一个是fromId 则直接交换顺序即可
        if (from.equals(preTo)) {
            from.setWeight(toWeight);
            to.setWeight(fromWeight);
            return;
        }
        T nextTo = findNextByWeight(toWeight);

        //计算新的权重
        int newWeight = 0;
        if (nextTo == null) {
            newWeight = toWeight + stepLength;
        } else {
            newWeight = toWeight + (nextTo.getWeight() - toWeight) / 2;
        }

        if (Math.abs(newWeight - toWeight) <= 1) {
            throw new IllegalStateException(String.format("down error, no enough weight :fromId:%d, toId:%d", fromId, toId));
        }
        from.setWeight(newWeight);
    }

    public void reweight() {
        int batchSize = 100;
        Sort sort = new Sort(Sort.Direction.DESC, "weight");
        Pageable pageable = new PageRequest(0, batchSize, sort);
        Page<T> page = findAll(pageable);
        do {
            //doReweight需要requiresNew事务
            ((BaseMovableService) AopContext.currentProxy()).doReweight(page);

            RepositoryHelper.clear();

            if (page.isLast()) {
                break;
            }

            pageable = new PageRequest((pageable.getPageNumber() + 1) * batchSize, batchSize, sort);

            page = findAll(pageable);

        } while (true);
    }

    public void doReweight(Page<T> page) {
        int totalElements = (int) page.getTotalElements();
        List<T> moves = page.getContent();

        int firstElement = page.getNumber() * page.getSize();

        for (int i = 0; i < moves.size(); i++) {
            T move = moves.get(i);
            move.setWeight((totalElements - firstElement - i) * stepLength);
            update(move);
        }

    }

    private Integer findNextWeight() {
        Searchable searchable = Searchable.newSearchable().setPage(0, 1).addSort(Sort.Direction.DESC, "weight");
        Page<T> page = findAll(searchable);

        if (!page.hasContent()) {
            return stepLength;
        }

        return page.getContent().get(0).getWeight() + stepLength;
    }

    public T findPreByWeight(Integer weight) {
        Pageable pageable = new PageRequest(0, 1);
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_lt", weight);
        Sort sort = new Sort(Sort.Direction.DESC, "weight");
        Page<T> page = findAll(Searchable.newSearchable(searchParams).addSort(sort).setPage(pageable));

        if (page.hasContent()) {
            return page.getContent().get(0);
        }
        return null;
    }

    public T findNextByWeight(Integer weight) {
        Pageable pageable = new PageRequest(0, 1);

        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gt", weight);
        Sort sort = new Sort(Sort.Direction.ASC, "weight");
        Page<T> page = findAll(Searchable.newSearchable(searchParams).addSort(sort).setPage(pageable));

        if (page.hasContent()) {
            return page.getContent().get(0);
        }
        return null;
    }

    private Long countByBetween(Integer minWeight, Integer maxWeight) {
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gte", minWeight);
        searchParams.put("weight_lte", maxWeight);
        return count(Searchable.newSearchable(searchParams));
    }

    //@Query(value = "from Move m where m.weight>=?1 and m.weight <= ?2 order by m.weight asc")
    List<T> findByBetweenAndAsc(Integer minWeight, Integer maxWeight) {
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gte", minWeight);
        searchParams.put("weight_lte", maxWeight);

        return findAllWithSort(Searchable.newSearchable(searchParams).addSort(Sort.Direction.ASC, "weight"));
    }


    List<T> findByBetweenAndDesc(Integer minWeight, Integer maxWeight) {
        Map<String, Object> searchParams = Maps.newHashMap();
        searchParams.put("weight_gte", minWeight);
        searchParams.put("weight_lte", maxWeight);

        Sort sort = new Sort(Sort.Direction.DESC, "weight");
        return findAllWithSort(Searchable.newSearchable(searchParams).addSort(sort));
    }

}
