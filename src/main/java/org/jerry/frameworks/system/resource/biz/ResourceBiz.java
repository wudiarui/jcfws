package org.jerry.frameworks.system.resource.biz;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.jerry.frameworks.base.entity.search.SearchOperator;
import org.jerry.frameworks.base.entity.search.Searchable;
import org.jerry.frameworks.base.plugin.service.BaseTreeableService;
import org.jerry.frameworks.system.auth.biz.UserAuthBiz;
import org.jerry.frameworks.system.entity.jpa.ResourceEntity;
import org.jerry.frameworks.system.entity.jpa.UserEntity;
import org.jerry.frameworks.system.entity.vo.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>Date : 16/5/23</p>
 * <p>Time : 下午4:04</p>
 *
 * @author jerry
 */
@Service
public class ResourceBiz extends BaseTreeableService<ResourceEntity, Long> {

    @Autowired
    private UserAuthBiz userAuthBiz;

    public String findActualResourceIdentity(ResourceEntity resource) {

        StringBuilder s = new StringBuilder(resource.getIdentity());

        boolean hasResourceIdentity = !StringUtils.isEmpty(resource.getIdentity());

        ResourceEntity parent = findOne(resource.getParentId());
        while (parent != null) {            // 这里是从后向前拼接
            if(StringUtils.isNotEmpty(parent.getIdentity())) {
                s.insert(0, parent.getIdentity() + ":");
                hasResourceIdentity = true;
            }
            parent = findOne(parent.getParentId());
        }

        //如果用户没有声明 资源标识  且父也没有，那么就为空
        if(!hasResourceIdentity) {
            return "";
        }

        //如果最后一个字符是: 因为不需要，所以删除之
        int length = s.length();
        if(length > 0 && s.lastIndexOf(":") == length - 1) {
            s.deleteCharAt(length - 1);
        }

        //如果有儿子 最后拼一个*
        boolean hasChildren = false;
        for(ResourceEntity r : findAll()) {
            if(resource.getId().equals(r.getParentId())) {
                hasChildren = true;
                break;
            }
        }
        if(hasChildren) {
            s.append(":*");
        }

        return s.toString();
    }

    /**
     * 根据用户生成菜单
     *
     * @param user      系统用户
     * @return          用户菜单
     */
    public List<Menu> findMenus(UserEntity user) {
        Searchable searchable = Searchable.newSearchable().addSearchFilter("show", SearchOperator.eq, Boolean.TRUE)
                                                        .addSort(new Sort(Sort.Direction.DESC, "parentId", "weight"));

        List<ResourceEntity> resources = findAllWithSort(searchable);

        Set<String> userPermissions = userAuthBiz.findStringPermissions(user);

        Iterator<ResourceEntity> iter = resources.iterator();
        while (iter.hasNext()) {
            if (!hasPermission(iter.next(), userPermissions)) {
                iter.remove();
            }
        }

        return convertToMenus(resources);
    }

    private static List<Menu> convertToMenus(List<ResourceEntity> resources) {
        if (resources.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        // 返回删除的那个, 正是倒序的第一个root node.
        Menu root = convertToMenu(resources.remove(resources.size() - 1));

        recursiveMenu(root, resources);
        List<Menu> menus = root.getChildrens();
        removeNoLeafMenu(menus);

        return menus;
    }

    private boolean hasPermission(ResourceEntity resource, Set<String> userPermissions) {
        String actualResourceIdentity = findActualResourceIdentity(resource);
        if (StringUtils.isEmpty(actualResourceIdentity)) {
            return true;
        }

        for (String permission : userPermissions) {
            if (hasPermission(permission, actualResourceIdentity)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermission(String permission, String actualResourceIdentity) {
        //得到权限字符串中的 资源部分，如a:b:create --->资源是a:b
        String permissionResourceIdentity = permission.substring(0, permission.lastIndexOf(":"));

        //如果权限字符串中的资源 是 以资源为前缀 则有权限 如a:b 具有a:b的权限
        if(permissionResourceIdentity.startsWith(actualResourceIdentity)) {
            return true;
        }

        WildcardPermission wp1 = new WildcardPermission(permissionResourceIdentity);
        WildcardPermission wp2 = new WildcardPermission(actualResourceIdentity);

        return wp1.implies(wp2) || wp2.implies(wp1);
    }

    /**
     * 把资源转换成菜单
     *
     * @param resource  资源
     * @return          菜单
     */
    private static Menu convertToMenu(ResourceEntity resource) {
        return new Menu(resource.getId(), resource.getName(), resource.getIcon(), resource.getUrl());
    }

    /**
     * 把资源递归成菜单
     *
     * @param menu          主菜单
     * @param resources     资源集合
     */
    private static void recursiveMenu(Menu menu, List<ResourceEntity> resources) {
        for (int i = resources.size() - 1; i >= 0; i--) {
            ResourceEntity resource = resources.get(i);
            if (resource.getParentId().equals(menu.getId())) {
                menu.getChildrens().add(convertToMenu(resource));
                resources.remove(i);
            }
        }

        for (Menu subMenu : menu.getChildrens()) {
            recursiveMenu(subMenu, resources);
        }
    }

    /**
     * 递归删除菜单中no leaf node
     *
     * @param menus     菜单集合
     */
    private static void removeNoLeafMenu(List<Menu> menus) {
        if (menus.size() == 0) {
            return;
        }

        for (int i = menus.size() - 1; i >= 0; i--) {
            Menu menu = menus.get(i);
            if (!menu.isHasChildren() && StringUtils.isEmpty(menu.getUrl())) {
                menus.remove(i);
            }

            removeNoLeafMenu(menu.getChildrens());
        }
    }
}
