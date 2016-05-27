package org.jerry.frameworks.system.user.exception;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午2:05</p>
 *
 * @author jerry
 */
public class UserBlockedException extends UserException {

    public UserBlockedException(String reason) {
        super("user.blocked", new Object[] {
                reason
        });
    }
}
