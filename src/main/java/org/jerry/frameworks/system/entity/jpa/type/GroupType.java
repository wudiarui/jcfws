package org.jerry.frameworks.system.entity.jpa.type;

/**
 * 用户组分类
 *
 * <p>Date : 16/5/12</p>
 * <p>Time : 下午1:41</p>
 *
 * @author jerry
 */
public enum GroupType {
    user("用户组"), organization("组织机构组");

    private final String info;

    GroupType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
