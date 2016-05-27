package org.jerry.frameworks.system.organzation.biz;

import org.jerry.frameworks.base.plugin.service.BaseTreeableService;
import org.jerry.frameworks.system.entity.jpa.OrganizationEntity;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

/**
 * <p>Date : 16/5/23</p>
 * <p>Time : 下午3:27</p>
 *
 * @author jerry
 */
@Service
public class OrganizationBiz extends BaseTreeableService<OrganizationEntity, Long> {
    /**
     * 过滤仅获取可显示的数据
     *
     * @param organizationIds       组织机构IDs
     * @param organizationJobIds    组织职务IDs
     */
    public void filterForCanShow(Set<Long> organizationIds, Set<Long[]> organizationJobIds) {

        Iterator<Long> iter1 = organizationIds.iterator();

        while (iter1.hasNext()) {
            Long id = iter1.next();
            OrganizationEntity o = findOne(id);
            if (o == null || Boolean.FALSE.equals(o.getShow())) {
                iter1.remove();
            }
        }

        Iterator<Long[]> iter2 = organizationJobIds.iterator();

        while (iter2.hasNext()) {
            Long id = iter2.next()[0];
            OrganizationEntity o = findOne(id);
            if (o == null || Boolean.FALSE.equals(o.getShow())) {
                iter2.remove();
            }
        }

    }
}
