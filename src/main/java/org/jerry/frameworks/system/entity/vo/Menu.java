package org.jerry.frameworks.system.entity.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 显示菜单实体类
 *
 * <p>Date : 16/5/16</p>
 * <p>Time : 上午10:27</p>
 *
 * @author jerry
 */
public class Menu implements Serializable {
    private Long id;
    private String name;
    private String url;
    private String icon;

    private List<Menu> childrens;

    public Menu(Long id, String name, String url, String icon) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Menu> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<Menu> childrens) {
        this.childrens = childrens;
    }

    public boolean isHasChildren() {
        return !getChildrens().isEmpty();
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", url='" + url + '\'' +
                ", children=" + childrens +
                '}';
    }
}
