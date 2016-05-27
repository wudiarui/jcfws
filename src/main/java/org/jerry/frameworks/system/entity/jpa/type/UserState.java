package org.jerry.frameworks.system.entity.jpa.type;

/**
 * <p>Date : 16/5/16</p>
 * <p>Time : 上午11:03</p>
 *
 * @author jerry
 */
public enum UserState {
    normal("正常"), blocked("封禁");

    private final String info;

    UserState(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
