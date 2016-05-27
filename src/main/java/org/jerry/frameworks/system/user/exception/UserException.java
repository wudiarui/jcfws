package org.jerry.frameworks.system.user.exception;

import org.jerry.frameworks.base.exception.BaseException;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 上午10:40</p>
 *
 * @author jerry
 */
public class UserException extends BaseException {

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
