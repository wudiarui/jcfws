package org.jerry.frameworks.base.web.controller;

import org.jerry.frameworks.base.Constants;
import org.jerry.frameworks.base.biz.BaseBiz;
import org.jerry.frameworks.base.entity.AbstractEntity;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.web.bind.annotation.PageableDefault;
import org.jerry.frameworks.base.web.controller.permission.PermissionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * 基础的CRUD控制器
 *
 * <p>Date : 16/4/27</p>
 * <p>Time : 上午11:44</p>
 *
 * @author jerry
 */
public abstract class BaseCRUDController<T extends AbstractEntity, ID extends Serializable>
            extends BaseController<T, ID> {

    protected BaseBiz<T, ID> baseBiz;

    private boolean listAlsoSetCommonData = Boolean.FALSE;

    protected PermissionList permissionList = null;

    @Autowired
    public void setBaseBiz(BaseBiz<T, ID> baseBiz) {
        this.baseBiz = baseBiz;
    }

    /**
     * 列表也设置common data
     */
    public void setListAlsoSetCommonData(boolean listAlsoSetCommonData) {
        this.listAlsoSetCommonData = listAlsoSetCommonData;
    }

    /**
     * 权限前缀：如sys:user
     * 则生成的新增权限为 sys:user:create
     */
    public void setResourceIdentity(String resourceIdentity) {
        if (!StringUtils.isEmpty(resourceIdentity)) {
            permissionList = PermissionList.newPermissionList(resourceIdentity);
        }
    }

    /**
     * 基础查询(降序)
     *
     * @param searchable    条件
     * @param model         页面实体
     * @return              路由到list页的数据
     */
    @RequestMapping(method = RequestMethod.GET)
    @PageableDefault(sort = "id=desc")
    public String list(Searchable searchable, Model model) {
        if (permissionList != null) {
            this.permissionList.assertHasViewPermission();
        }

        model.addAttribute("page", baseBiz.findAll(searchable));

        if (listAlsoSetCommonData) {
            setCommonData(model);
        }

        return viewName("list");
    }

    /**
     * 仅返回表格数据
     *
     * @param searchable    条件
     * @param model         页面实体
     * @return              表格数据
     */
    @RequestMapping(method = RequestMethod.GET, headers = "table=true")
    @PageableDefault(sort = "id=desc")
    public String listTable(Searchable searchable, Model model) {
        list(searchable, model);
        return viewName("listTable");
    }

    /**
     * 实体编辑页
     *
     * @param model         页面实体
     * @param t             实体路由
     * @return              路由到实体页的数据
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable("id") T t) {
        if (permissionList != null) {
            this.permissionList.assertHasViewPermission();
        }

        setCommonData(model);
        model.addAttribute("t", t);
        model.addAttribute(Constants.OP_NAME, "查看");
        return viewName("editForm");
    }

    /**
     * 实体新增页
     *
     * @param model         页面实体
     * @return              路由到新增实体页的空数据
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        if (permissionList != null) {
            this.permissionList.assertHasCreatePermission();
        }

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "新增");
        if (!model.containsAttribute("t")) {
            model.addAttribute("t", newModel());
        }
        return viewName("editForm");
    }

    /**
     * 新增实体
     *
     * @param model                 页面实体
     * @param t                     类型
     * @param result                页面消息验证结果
     * @param redirectAttributes    带参数据的重定向
     * @return                      重定向到当前页
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @Valid @ModelAttribute("t") T t, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            this.permissionList.assertHasCreatePermission();
        }

        if (hasError(t, result)) {
            return showCreateForm(model);
        }
        baseBiz.save(t);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "新增成功");
        return redirectToUrl(null);
    }

    /**
     * 实体修改页
     *
     * @param t                     类型
     * @param model                 页面实体
     * @return                      路由到修改实体页的空数据
     */
    @RequestMapping(value = "{id}/update", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") T t, Model model) {
        if (permissionList != null) {
            this.permissionList.assertHasUpdatePermission();
        }

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "修改");
        model.addAttribute("t", t);
        return viewName("editForm");
    }

    /**
     * 更新实体
     *
     * @param model                 页面实体
     * @param t                     实体类型
     * @param result                页面消息验证结果
     * @param backURL               重定向返回路由
     * @param redirectAttributes    带参数据的重定向
     * @return                      更新实体后,重定向路由及带参数据
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(Model model, @Valid @ModelAttribute("t") T t, BindingResult result,
                         @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
                         RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            this.permissionList.assertHasUpdatePermission();
        }

        if (hasError(t, result)) {
            return showUpdateForm(t, model);
        }
        baseBiz.update(t);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "修改成功");
        return redirectToUrl(backURL);
    }

    /**
     * 删除实体页
     *
     * @param t                     实体类型
     * @param model                 页面实体
     * @return                      路由到删除实体页的数据
     */
    @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
    public String showDeleteForm(@PathVariable("id") T t, Model model) {
        if (permissionList != null) {
            this.permissionList.assertHasDeletePermission();
        }

        setCommonData(model);
        model.addAttribute(Constants.OP_NAME, "删除");
        model.addAttribute("t", t);
        return viewName("editForm");
    }

    /**
     * 删除实体
     *
     * @param t                     实体类型
     * @param backURL               重定向返回路由
     * @param redirectAttributes    带参数据的重定向
     * @return                      删除实体后,重定向路由及带参数据
     */
    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public String delete(
            @PathVariable("id") T t,
            @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
            RedirectAttributes redirectAttributes) {

        if ((permissionList != null))
            this.permissionList.assertHasDeletePermission();

        baseBiz.delete(t);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "删除成功");

        return redirectToUrl(backURL);
    }

    /**
     * 根据ID集合,批量删除实体
     *
     * @param ids                   实体的id集合
     * @param backURL               重定向返回路由
     * @param redirectAttributes    带参数据的重定向
     * @return                      批量删除实体后,重定向路由及带参数据
     */
    @RequestMapping(value = "batch/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteInBatch(
            @RequestParam(value = "ids", required = false) ID[] ids,
            @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
            RedirectAttributes redirectAttributes) {
        if (permissionList != null)
            this.permissionList.assertHasDeletePermission();

        baseBiz.delete(ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
        return redirectToUrl(backURL);
    }
}
