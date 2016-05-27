package org.jerry.frameworks.base.plugin.web.controller;

import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.plugin.entity.Movable;
import org.jerry.frameworks.base.plugin.service.BaseMovableService;
import org.jerry.frameworks.base.utils.MessageUtils;
import org.jerry.frameworks.base.web.bind.annotation.PageableDefault;
import org.jerry.frameworks.base.web.controller.BaseCRUDController;
import org.jerry.frameworks.base.web.validate.AjaxResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

/**
 * <p>Date : 16/5/26</p>
 * <p>Time : 下午2:55</p>
 *
 * @author jerry
 */
public abstract class BaseMovableController<T extends BaseEntity & Movable, ID extends Serializable>
                    extends BaseCRUDController<T, ID> {

    protected BaseMovableService<T, ID> getBaseMovableService() {
        return (BaseMovableService<T, ID>) baseBiz;
    }

    /**
     * 基础查询(降序)
     *
     * @param searchable 条件
     * @param model      页面实体
     * @return 路由到list页的数据
     */
    @RequestMapping(method = RequestMethod.GET)
    @PageableDefault(value = 10, sort = "weight=desc")
    @Override
    public String list(Searchable searchable, Model model) {
        return super.list(searchable, model);
    }

    @RequestMapping(value = "{fromId}/{toId}/up")
    @ResponseBody
    public AjaxResponse up(@PathVariable(value = "fromId") ID fromId, @PathVariable(value = "toId") ID toId) {
        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }

        AjaxResponse ajaxResponse = new AjaxResponse("位置移动成功");
        try {
            getBaseMovableService().up(fromId, toId);
        } catch (IllegalStateException e) {
            ajaxResponse.setSuccess(Boolean.TRUE);
            ajaxResponse.setMessage(MessageUtils.message("move.not.enough"));
        }

        return ajaxResponse;
    }

    @RequestMapping(value = "{fromId}/{toId}/down")
    @ResponseBody
    public AjaxResponse down(@PathVariable(value = "fromId") ID fromId, @PathVariable(value = "toId") ID toId) {
        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }

        AjaxResponse ajaxResponse = new AjaxResponse("移动位置成功");
        try {
            getBaseMovableService().down(fromId, toId);
        } catch (IllegalStateException e) {
            ajaxResponse.setSuccess(Boolean.FALSE);
            ajaxResponse.setMessage(MessageUtils.message("move.not.enough"));
        }
        return ajaxResponse;
    }

    @RequestMapping(value = "reweight")
    @ResponseBody
    public AjaxResponse reWeight() {
        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }

        AjaxResponse ajaxResponse = new AjaxResponse("优化权重成功！");
        try {
            getBaseMovableService().reweight();
        } catch (IllegalStateException e) {
            ajaxResponse.setSuccess(Boolean.FALSE);
            ajaxResponse.setMessage("优化权重失败了！");
        }
        return ajaxResponse;
    }
}
