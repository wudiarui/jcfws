package org.jerry.frameworks.base.entity.jdbc;

import org.jerry.frameworks.base.entity.AbstractEntity;

import java.io.Serializable;

/**
 * Spring JDBC Common Entity
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午1:56</p>
 * @author jerry
 */
public abstract class BaseEntity<ID extends Serializable> extends AbstractEntity<ID> {

    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }
}
