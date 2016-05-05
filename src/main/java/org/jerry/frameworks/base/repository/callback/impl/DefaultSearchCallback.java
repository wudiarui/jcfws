package org.jerry.frameworks.base.repository.callback.impl;

import org.jerry.frameworks.base.entity.search.SearchOperator;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.entity.search.filter.AndCondition;
import org.jerry.frameworks.base.entity.search.filter.Condition;
import org.jerry.frameworks.base.entity.search.filter.OrCondition;
import org.jerry.frameworks.base.entity.search.filter.SearchFilter;
import org.jerry.frameworks.base.repository.callback.SearchCallback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import javax.persistence.Query;

/**
 * 默认的检索回调类
 *
 * <p>Date : 16/4/27</p>
 * <p>Time : 下午4:39</p>
 *
 * @author jerry
 */
public class DefaultSearchCallback implements SearchCallback {

    /**
     * 回调参数前缀
     */
    private static final String paramPrefix = "param_";
    /**
     * 别名
     */
    private String alias;
    /**
     * 带点别名
     */
    private String aliasWithDot;

    public DefaultSearchCallback() {
        this("");
    }

    public DefaultSearchCallback(String alias) {
        this.alias = alias;
        if(!StringUtils.isEmpty(this.alias)) {
            this.aliasWithDot = this.alias + ".";
        } else {
            this.aliasWithDot = "";
        }
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAliasWithDot() {
        return aliasWithDot;
    }

    public void setAliasWithDot(String aliasWithDot) {
        this.aliasWithDot = aliasWithDot;
    }

    private int getCondition(StringBuilder ql, int paramIndex, SearchFilter searchFilter) {
        boolean needAppendBracket = searchFilter instanceof OrCondition || searchFilter instanceof AndCondition;

        if (needAppendBracket) {
            ql.append("(");
        }

        if(searchFilter instanceof Condition) {
            Condition condition = (Condition) searchFilter;
            // 自定义条件
            String entityProperty = condition.getEntityProperty();
            String operatorStr = condition.getOperatorStr();

            // 加入实体别名带点加属性
            ql.append(getAliasWithDot());
            ql.append(entityProperty);

            //操作符
            //1、如果是自定义查询符号，则使用SearchPropertyMappings中定义的默认的操作符
            ql.append(" ");
            ql.append(operatorStr);

            if (!condition.isUnaryFilter()) {
                ql.append(" :");
                ql.append(paramPrefix);
                ql.append(paramIndex++);
                return paramIndex;
            }
        } else if (searchFilter instanceof OrCondition) {
            boolean isFirst = true;
            for (SearchFilter orSearchFilter : ((OrCondition) searchFilter).getOrFilters()) {
                if(!isFirst)
                    ql.append(" or ");
                paramIndex = getCondition(ql, paramIndex, orSearchFilter);
                isFirst = false;
            }
        } else if (searchFilter instanceof AndCondition) {
            boolean isFirst = true;
            for (SearchFilter andSearchFilter : ((AndCondition) searchFilter).getAndFilters()) {
                if (!isFirst)
                    ql.append(" and ");
                paramIndex = getCondition(ql, paramIndex, andSearchFilter);
                isFirst = false;
            }
        }

        if (needAppendBracket)
            ql.append(")");
        return paramIndex;
    }

    @Override
    public void prepareQL(StringBuilder ql, Searchable search) {
        if (!search.hasSearchFilter())
            return;

        int paramIndex = 1;
        for (SearchFilter searchFilter : search.getSearchFilters()) {
            if (searchFilter instanceof Condition) {
                Condition condition = (Condition)searchFilter;
                if (condition.getOperator() == SearchOperator.custom) {
                    continue;
                }

            }
            ql.append(" and ");
            paramIndex = getCondition(ql, paramIndex, searchFilter);
        }
    }

    @Override
    public void prepareOrder(StringBuilder ql, Searchable search) {
        if (search.hashSort()) {
            ql.append(" order by ");
            for (Sort.Order order : search.getSort()) {
                ql.append(String.format("%s%s %s, ", getAliasWithDot(), order.getProperty(), order.getDirection().name().toLowerCase()));
            }
            ql.delete(ql.length() - 2, ql.length());
        }
    }

    private int setValues(Query query, SearchFilter searchFilter, int paramIndex) {
        if (searchFilter instanceof Condition) {
            Condition condition = (Condition) searchFilter;
            if (condition.getOperator() == SearchOperator.custom) {
                return paramIndex;
            }
            if (condition.isUnaryFilter()) {
                return paramIndex;
            }
            query.setParameter(paramPrefix + paramIndex++, formtValue(condition, condition.getValue()));
        } else if (searchFilter instanceof OrCondition) {
            for (SearchFilter orSearchFilter : ((OrCondition) searchFilter).getOrFilters()) {
                paramIndex = setValues(query, orSearchFilter, paramIndex);
            }
        } else if (searchFilter instanceof AndCondition) {
            for (SearchFilter andSearchFilter: ((AndCondition) searchFilter).getAndFilters()) {
                paramIndex = setValues(query, andSearchFilter, paramIndex);
            }
        }
        return paramIndex;
    }

    private Object formtValue(Condition condition, Object value) {
        SearchOperator operator = condition.getOperator();
        if (operator == SearchOperator.like || operator == SearchOperator.notLike) {
            return "%" + value + "%";
        }

        if (operator == SearchOperator.prefixLike || operator == SearchOperator.prefixNotLike) {
            return value + "%";
        }

        if (operator == SearchOperator.suffixLike || operator == SearchOperator.suffixNotLike) {
            return "%" + value;
        }
        return value;
    }

    @Override
    public void setValues(Query query, Searchable search) {

        int paramIndex = 1;

        for (SearchFilter searchFilter : search.getSearchFilters()) {
            paramIndex = setValues(query, searchFilter, paramIndex);

        }
    }

    @Override
    public void setPageable(Query query, Searchable search) {
        if (search.hasPageable()) {
            Pageable pageable = search.getPage();
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
    }
}
