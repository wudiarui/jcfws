package org.jerry.frameworks.system.user.exception;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午1:28</p>
 *
 * @author jerry
 */
public class UserNotExistsException extends UserException {

    public UserNotExistsException() {
        super("user.not.exists", null);
    }
}
