package org.jerry.frameworks.system.user.exception;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 上午10:12</p>
 *
 * @author jerry
 */
public class UserPasswordRetryLimitExceedException extends UserException {

    public UserPasswordRetryLimitExceedException(int maxRetryLimited) {
        super("user.password.retry.limit.exceed", new Object[] {
                maxRetryLimited
        });
    }
}
