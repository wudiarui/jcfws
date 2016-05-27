package org.jerry.frameworks.system.user.type;

/**
 * 用户操作的类型
 *
 * <p>Date : 16/5/19</p>
 * <p>Time : 上午9:59</p>
 *
 * @author jerry
 */
public enum UserOperationType {
    loginError("登录失败"),loginSuccess("登录成功"),passwordError("密码错误"),changePassword("修改密码"),changeStatus("修改状态");

    private String info;

    UserOperationType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
