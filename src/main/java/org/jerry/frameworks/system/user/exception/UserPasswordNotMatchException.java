package org.jerry.frameworks.system.user.exception;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 上午11:08</p>
 *
 * @author jerry
 */
public class UserPasswordNotMatchException extends UserException {

    public UserPasswordNotMatchException() {
        super("user.password.not.match", null);
    }
}
