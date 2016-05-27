package org.jerry.frameworks.system.entity.jpa.type;

/**
 * 授权类型
 *
 * <p>Date : 16/5/12</p>
 * <p>Time : 下午3:30</p>
 *
 * @author jerry
 */
public enum AuthType {
    user("用户"), user_group("用户组"), organization_job("组织机构和工作职务"), organization_group("组织机构组");

    private final String info;

    AuthType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
