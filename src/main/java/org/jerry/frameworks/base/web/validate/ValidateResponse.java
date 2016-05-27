package org.jerry.frameworks.base.web.validate;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 验证消息
 *
 * <p>Date : 16/5/26</p>
 * <p>Time : 上午10:02</p>
 *
 * @author jerry
 */
public class ValidateResponse {
    private final static Integer SUCCESS = 1;
    private final static Integer FAIL    = 0;

    private List<Object> results = Lists.newArrayList();

    public ValidateResponse() {
    }

    public static ValidateResponse newInstance() {
        return new ValidateResponse();
    }

    /**
     * 验证失败（使用前台alertTextOk定义的消息）
     *
     * @param fieldId 验证失败的字段名
     */
    public void validateFail(String fieldId) {
        validateFail(fieldId, "");
    }

    /**
     * 验证失败
     *
     * @param fieldId 验证失败的字段名
     * @param message 验证失败时显示的消息
     */
    public void validateFail(String fieldId, String message) {
        results.add(new Object[]{fieldId, FAIL, message});
    }

    /**
     * 验证成功（使用前台alertTextOk定义的消息）
     *
     * @param fieldId 验证成功的字段名
     */
    public void validateSuccess(String fieldId) {
        validateSuccess(fieldId, "");
    }

    /**
     * 验证成功
     *
     * @param fieldId 验证成功的字段名
     * @param message 验证成功时显示的消息
     */
    public void validateSuccess(String fieldId, String message) {
        results.add(new Object[]{fieldId, SUCCESS, message});
    }

    /**
     * 返回验证结果
     *
     * @return  验证结果
     */
    public Object result() {
        if (results.size() == 1) {
            return results.get(0);
        }
        return results;
    }
}
