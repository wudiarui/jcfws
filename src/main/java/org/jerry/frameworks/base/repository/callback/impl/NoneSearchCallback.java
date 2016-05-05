package org.jerry.frameworks.base.repository.callback.impl;

import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.repository.callback.SearchCallback;

import javax.persistence.Query;

/**
 * <p>Date : 16/4/27</p>
 * <p>Time : 下午4:34</p>
 *
 * @author jerry
 */
public class NoneSearchCallback implements SearchCallback {
    @Override
    public void prepareQL(StringBuilder ql, Searchable search) {

    }

    @Override
    public void prepareOrder(StringBuilder ql, Searchable search) {

    }

    @Override
    public void setValues(Query query, Searchable search) {

    }

    @Override
    public void setPageable(Query query, Searchable search) {

    }
}
