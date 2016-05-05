package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;
import org.omg.CORBA.BooleanHolder;

/**
 *
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午2:47</p>
 * @author jerry
 */
public class GroupEntity extends BaseEntity<Long> {
    
    private String name;
    private String type;
    private Boolean isShow;
    private Boolean defaultGroup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }

    public Boolean getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(Boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
