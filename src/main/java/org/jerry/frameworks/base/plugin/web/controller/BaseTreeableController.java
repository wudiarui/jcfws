package org.jerry.frameworks.base.plugin.web.controller;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jerry.frameworks.base.constants.Constants;
import org.jerry.frameworks.base.entity.enums.BooleanEnum;
import org.jerry.frameworks.base.entity.jpa.BaseEntity;
import org.jerry.frameworks.base.entity.search.SearchOperator;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.plugin.entity.Treeable;
import org.jerry.frameworks.base.plugin.entity.ZTree;
import org.jerry.frameworks.base.plugin.service.BaseTreeableService;
import org.jerry.frameworks.base.web.bind.annotation.PageableDefault;
import org.jerry.frameworks.base.web.controller.BaseCRUDController;
import org.jerry.frameworks.base.web.controller.permission.PermissionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

/**
 * <p>Date : 16/5/26</p>
 * <p>Time : 下午3:39</p>
 *
 * @author jerry
 */
public abstract class BaseTreeableController<T extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable>
                extends BaseCRUDController<T, ID> {
    protected BaseTreeableService<T, ID> baseTreeableService;

    @Autowired
    public void setBaseTreeableService(BaseTreeableService<T, ID> baseTreeableService) {
        this.baseTreeableService = baseTreeableService;
    }

    protected PermissionList permissionList = null;

    /**
     * 权限前缀：如sys:user
     * 则生成的新增权限为 sys:user:create
     */
    public void setResourceIdentity(String resourceIdentity) {
        if (!StringUtils.isEmpty(resourceIdentity)) {
            permissionList = PermissionList.newPermissionList(resourceIdentity);
        }
    }

    protected void setCommonData(Model model) {
        model.addAttribute("booleanList", BooleanEnum.values());
    }

    @RequestMapping(value = {"", "main"}, method = RequestMethod.GET)
    public String main() {

        if (permissionList != null) {
            permissionList.assertHasViewPermission();
        }

        return viewName("main");
    }

    @RequestMapping(value = "tree", method = RequestMethod.GET)
    @PageableDefault(sort = {"parentIds=asc", "weight=asc"})
    public String tree(HttpServletRequest request,
                       @RequestParam(value = "searchName", required = false) String searchName,
                       @RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
                       Searchable searchable, Model model) {
        if (permissionList != null) {
            permissionList.assertHasViewPermission();
        }

        List<T> entities = null;

        if (StringUtils.isNotEmpty(searchName)) {
            searchable.addSearchParam("name_like", searchName);
            entities = baseTreeableService.findAllByName(searchable, null);
            if (!async) {
                searchable.removeSearchFilter("name_like");
                List<T> childrens = baseTreeableService.findChildren(entities, searchable);
                entities.removeAll(childrens);
                entities.addAll(childrens);
            } else {
                // 异步时,不查子孙,只查自己,所以这里没代码
            }
        } else { // 初始化或重置树
            if(!async) {
                entities = baseTreeableService.findAllWithSort(searchable);
            } else {
                entities = baseTreeableService.findRootAndChild(searchable);
            }
        }

        model.addAttribute("trees", convertToZtreeList(request.getContextPath(), entities, async, true));

        return viewName("tree");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") T t, Model model) {
        if (permissionList != null) {
            permissionList.assertHasViewPermission();
        }

        setCommonData(model);
        model.addAttribute("t", t);
        model.addAttribute(Constants.OP_NAME, "查看");

        return viewName("editForm");
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") T t, Model model, RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            permissionList.assertHasUpdatePermission();
        }

        if (t == null) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, "访问的数据不存在.");
            return redirectToUrl(viewName("success"));
        }

        setCommonData(model);
        model.addAttribute("t", t);
        model.addAttribute(Constants.OP_NAME, "修改");

        return viewName("editForm");
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    public String update(Model model, @ModelAttribute("t") T t,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            permissionList.assertHasUpdatePermission();
        }

        if (result.hasErrors()) {
            return updateForm(t, model, redirectAttributes);
        }

        baseTreeableService.update(t);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "修改成功");
        return redirectToUrl(viewName("success"));
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
    public String deleteForm(@PathVariable("id") T t, Model model) {
        if (permissionList != null) {
            permissionList.assertHasDeletePermission();
        }

        setCommonData(model);
        model.addAttribute("t", t);
        model.addAttribute(Constants.OP_NAME, "删除");

        return viewName("editForm");
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public String deleteSelfAndChildren(Model model, @ModelAttribute("t") T t,
                                        BindingResult result, RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            permissionList.assertHasDeletePermission();
        }

        if (t.isRoot()) {
            result.reject("您删除的数据中包含最高级节点, 而最高级节点无法被删除!");
            return deleteForm(t, model);
        }

        baseTreeableService.deleteSelfAndChild(t);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
        return redirectToUrl(viewName("success"));
    }

    @RequestMapping(value = "batch/delete")
    public String deleteInBatch(@RequestParam(value = "ids", required = false) ID[] ids,
                                @RequestParam(value = Constants.BACK_URL, required = false) String backUrl,
                                RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            permissionList.assertHasDeletePermission();
        }

        Searchable searchable = Searchable.newSearchable().addSearchFilter("id", SearchOperator.in, ids);
        List<T> tList = baseTreeableService.findAllWithNoPageNoSort(searchable);
        for (T entity : tList) {
            if (entity.isRoot()) {
                redirectAttributes.addFlashAttribute(Constants.ERROR, "您删除的数据中包含根节点，根节点不能删除");
                return redirectToUrl(backUrl);
            }
        }

        baseTreeableService.deleteSelfAndChild(tList);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
        return redirectToUrl(viewName("success"));
    }

    @RequestMapping(value = "{parent}/appendChild", method = RequestMethod.GET)
    public String appendChildForm(@PathVariable("parent") T parent, Model model) {
        if (permissionList != null) {
            permissionList.assertHasCreatePermission();
        }

        setCommonData(model);
        if (!model.containsAttribute("child")) {
            model.addAttribute("child", newModel());
        }

        model.addAttribute(Constants.OP_NAME, "添加子节点");

        return viewName("appendChildForm");
    }

    @RequestMapping(value = "{parent}/appendChild", method = RequestMethod.POST)
    public String appendChild(Model model, @PathVariable("parent") T parent,
                              @ModelAttribute("child") T child, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (permissionList != null) {
            permissionList.assertHasCreatePermission();
        }

        setCommonData(model);

        if (result.hasErrors()) {
            appendChildForm(parent, model);
        }

        baseTreeableService.appendChild(parent, child);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "添加子节点成功");
        return redirectToUrl(viewName("success"));
    }

    @RequestMapping(value = "{source}/move", method = RequestMethod.GET)
    @PageableDefault(sort = {"parentIds=asc", "weight=asc"})
    public String showMoveForm(
            HttpServletRequest request,
            @RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
            @PathVariable("source") T source,
            Searchable searchable,
            Model model) {
        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }

        List<T> entities = null;

        searchable.addSearchFilter("id", SearchOperator.ne, source.getId());
        searchable.addSearchFilter("parentIds", SearchOperator.notLike, source.makeSelfAsNewParentIds());

        if (!async) {
            entities = baseTreeableService.findAllWithSort(searchable);
        } else {
            entities = baseTreeableService.findRootAndChild(searchable);
        }

        model.addAttribute("tree", convertToZtreeList(request.getContextPath(), entities, async, true));
        model.addAttribute(Constants.OP_NAME, "移动节点位置");

        return viewName("moveForm");
    }

    @RequestMapping(value = "{source}/move", method = RequestMethod.POST)
    @PageableDefault(sort = {"parentIds=asc", "weight=asc"})
    public String move(HttpServletRequest request,
                       @RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
                       @PathVariable("source") T source,
                       @RequestParam("target") T target,
                       @RequestParam("moveType") String moveType,
                       Searchable searchable,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }

        if (target.isRoot() && !moveType.equals("inner")) {
            model.addAttribute(Constants.ERROR, "不能移动到根节点之前或之后");
            return showMoveForm(request, async, source, searchable, model);
        }

        baseTreeableService.move(source, target, moveType);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "移动节点成功");
        return redirectToUrl(viewName("success"));
    }

    @RequestMapping(value = "{parent}/children", method = RequestMethod.GET)
    @PageableDefault(sort = {"parentIds=asc", "weight=asc"})
    public String list(
            HttpServletRequest request,
            @PathVariable("parent") T parent,
            Searchable searchable, Model model) throws UnsupportedEncodingException {

        if (permissionList != null) {
            permissionList.assertHasViewPermission();
        }

        if (parent != null) {
            searchable.addSearchFilter("parentId", SearchOperator.eq, parent.getId());
        }

        model.addAttribute("page", baseTreeableService.findAll(searchable));

        return viewName("listChildren");
    }

    /**
     * 仅返回表格数据
     */
    @RequestMapping(value = "{parent}/children", headers = "table=true", method = RequestMethod.GET)
    @PageableDefault(sort = {"parentIds=asc", "weight=asc"})
    public String listTable(
            HttpServletRequest request,
            @PathVariable("parent") T parent,
            Searchable searchable, Model model) throws UnsupportedEncodingException {

        list(request, parent, searchable, model);
        return viewName("listChildrenTable");
    }

    /////////////////////////////////////ajax///////////////////////////////////////////////
    @RequestMapping(value = "ajax/load")
    @PageableDefault(sort = {"parentIds=asc", "weight=asc"})
    @ResponseBody
    public Object load(HttpServletRequest request,
                       @RequestParam(value = "async", defaultValue = "true") boolean async,
                       @RequestParam(value = "asyncLoadAll", defaultValue = "false") boolean asyncLoadAll,
                       @RequestParam(value = "searchName", required = false) String searchName,
                       @RequestParam(value = "id", required = false) ID parentId,
                       @RequestParam(value = "excludeId", required = false) ID excludeId,
                       @RequestParam(value = "onlyCheckLeaf", required = false, defaultValue = "false") boolean onlyCheckLeaf,
                       Searchable searchable) {
        T excludeEntity = baseTreeableService.findOne(excludeId);

        List<T> entities = null;

        if (!StringUtils.isEmpty(searchName)) {
            searchable.addSearchParam("name_like", searchName);
            entities = baseTreeableService.findAllByName(searchable, excludeEntity);
            if (!async || asyncLoadAll) {//非异步模式 查自己及子子孙孙 但排除
                searchable.removeSearchFilter("name_like");
                List<T> children = baseTreeableService.findChildren(entities, searchable);
                entities.removeAll(children);
                entities.addAll(children);
            } else {}
        } else {
            if (parentId != null) { //只查某个节点下的 异步
                searchable.addSearchFilter("parentId", SearchOperator.eq, parentId);
            }

            if (async && !asyncLoadAll) { //异步模式下 且非异步加载所有
                //排除自己 及 子子孙孙
                baseTreeableService.addExcludeSearchFilter(searchable, excludeEntity);

            }

            if (parentId == null && !asyncLoadAll) {
                entities = baseTreeableService.findRootAndChild(searchable);
            } else {
                entities = baseTreeableService.findAllWithSort(searchable);
            }
        }
        return convertToZtreeList(
                request.getContextPath(),
                entities,
                async && !asyncLoadAll && parentId != null,
                onlyCheckLeaf);
    }

    @RequestMapping(value = "ajax/{parent}/appendChild", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object ajaxAppendChild(HttpServletRequest request, @PathVariable("parent") T parent) {


        if (permissionList != null) {
            permissionList.assertHasCreatePermission();
        }


        T child = newModel();
        child.setName("新节点");
        baseTreeableService.appendChild(parent, child);
        return convertToZtree(child, true, true);
    }

    @RequestMapping(value = "ajax/{id}/delete", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object ajaxDeleteSelfAndChildren(@PathVariable("id") ID id) {


        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }

        T tree = baseTreeableService.findOne(id);
        baseTreeableService.deleteSelfAndChild(tree);
        return tree;
    }

    @RequestMapping(value = "ajax/{id}/rename", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object ajaxRename(HttpServletRequest request, @PathVariable("id") T tree, @RequestParam("newName") String newName) {


        if (permissionList != null) {
            permissionList.assertHasUpdatePermission();
        }

        tree.setName(newName);
        baseTreeableService.update(tree);
        return convertToZtree(tree, true, true);
    }

    @RequestMapping(value = "ajax/{sourceId}/{targetId}/{moveType}/move", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object ajaxMove(
            @PathVariable("sourceId") T source, @PathVariable("targetId") T target,
            @PathVariable("moveType") String moveType) {


        if (this.permissionList != null) {
            this.permissionList.assertHasEditPermission();
        }


        baseTreeableService.move(source, target, moveType);

        return source;
    }

    @RequestMapping("ajax/autocomplete")
    @PageableDefault(value = 30)
    @ResponseBody
    public Set<String> autocomplete(
            Searchable searchable,
            @RequestParam("term") String term,
            @RequestParam(value = "excludeId", required = false) ID excludeId) {

        return baseTreeableService.findNames(searchable, term, excludeId);
    }

    @RequestMapping(value = "success")
    public String success() {
        return viewName("success");
    }

    @Override
    protected String redirectToUrl(String backURL) {
        if (!StringUtils.isEmpty(backURL)) {
            return super.redirectToUrl(backURL);
        }
        return super.redirectToUrl(viewName("success"));
    }

    private List<ZTree<ID>> convertToZtreeList(String contextPath, List<T> entities, boolean async, boolean onlySelectLeaf) {
        List<ZTree<ID>> zTrees = Lists.newArrayList();

        if (entities == null || entities.isEmpty()) {
            return zTrees;
        }

        for (T t : entities) {
            ZTree zTree = convertToZtree(t, !async, onlySelectLeaf);
            zTrees.add(zTree);
        }
        return zTrees;
    }

    private ZTree convertToZtree(T t, boolean open, boolean onlyCheckLeaf) {
        ZTree<ID> zTree = new ZTree<>();
        zTree.setId(t.getId());
        zTree.setName(t.getName());
        zTree.setPid(t.getParentId());
        zTree.setIconSkin(t.getIcon());
        zTree.setOpen(open);
        zTree.setRoot(t.isRoot());
        zTree.setParent(t.isHasChildren());

        if (onlyCheckLeaf && zTree.isParent()) {
            zTree.setNocheck(true);
        } else {
            zTree.setNocheck(false);
        }

        return zTree;
    }
}
