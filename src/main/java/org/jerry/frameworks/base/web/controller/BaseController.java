package org.jerry.frameworks.base.web.controller;

import org.jerry.frameworks.base.entity.AbstractEntity;
import org.jerry.frameworks.base.utils.ReflectUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;

/**
 * 基础控制器
 *
 * <p>Date : 16/4/27</p>
 * <p>Time : 上午9:59</p>
 *
 * @author jerry
 */
public abstract class BaseController<T extends AbstractEntity, ID extends Serializable> {

    /**
     * 实体类型
     */
    protected final Class<T> entityClass;

    /**
     * 视图前缀
     */
    protected String viewPrefix;

    protected BaseController() {
        this.entityClass = ReflectUtils.findParameterizedType(getClass(), 0);
        setViewPrefix(defaultViewPrefix());
    }

    /**
     * 设置通用数据
     *
     * @param model 数据模型
     */
    protected void setCommonData(Model model) {}

    /**
     * 当前模块 视图的前缀
     * 默认
     * 1、获取当前类头上的@RequestMapping中的value作为前缀
     * 2、如果没有就使用当前模型小写的简单类名
     */
    public void setViewPrefix(String viewPrefix) {
        if (viewPrefix.startsWith("/")) {
            viewPrefix = viewPrefix.substring(1);
        }
        this.viewPrefix = viewPrefix;
    }

    public String getViewPrefix() {
        return viewPrefix;
    }

    protected T newModel() {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("can not instantiated model : " + this.entityClass, e);
        }
    }

    /**
     * 获取视图名称：即prefixViewName + "/" + suffixName
     *
     * @param suffixName 视图路由后缀
     * @return 整体视图路由径
     */
    public String viewName(String suffixName) {
        if (!suffixName.startsWith("/")) {
            suffixName = "/" + suffixName;
        }
        return getViewPrefix() + suffixName;
    }

    /**
     * 共享的验证规则
     * 验证失败返回true
     *
     * @param t
     * @param result
     * @return
     */
    protected boolean hasError(T t, BindingResult result) {
        Assert.notNull(t);
        return result.hasErrors();
    }

    /**
     * 重定向到指定URL.
     *
     * @param backUrl 为null时, 将重定向到默认getViewPrefix().
     * @return
     */
    protected String redirectToUrl(String backUrl) {
        if (StringUtils.isEmpty(backUrl)) {
            backUrl = getViewPrefix();
        }
        if (!backUrl.startsWith("/") && !backUrl.startsWith("http")) {
            backUrl = "/" + backUrl;
        }
        return "redirect:" + backUrl;
    }

    /**
     * 获取默认的视图路由前缀
     *
     * @return
     */
    protected String defaultViewPrefix() {
        String currentViewPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(getClass(), RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            currentViewPrefix = requestMapping.value()[0];
        }
        if (StringUtils.isEmpty(currentViewPrefix)) {
            currentViewPrefix = entityClass.getSimpleName();
        }
        return currentViewPrefix;
    }
}
