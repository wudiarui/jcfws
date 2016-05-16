package org.jerry.frameworks.system.entity.jpa.emun;

/**
 * <p>Date : 16/5/13</p>
 * <p>Time : 下午3:12</p>
 *
 * @author jerry
 */
public enum OrganizationType {
    bloc("集团"), branch_office("分公司"), department("部门"), group("部门小组");

    private final String info;

    OrganizationType(String info) {
        this.info = info;
    }

    String getInfo() {
        return info;
    }
}
