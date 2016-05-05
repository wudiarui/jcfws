package org.jerry.frameworks.system.entity.jdbc;

import org.jerry.frameworks.base.entity.jdbc.BaseEntity;

/**
 * <p>Date : 16/4/19</p>
 * <p>Time : 下午2:48</p>
 *
 * @author jerry
 */
public class OrganizationEntity extends BaseEntity<Long> {

    private String name;
    private String type;
    private Long parentId;
    private String parentIds;
    private String icon;
    private Integer weight;
    private Boolean isShow;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }
}
