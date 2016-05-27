package org.jerry.frameworks.system.organzation.biz;

import org.jerry.frameworks.base.plugin.service.BaseTreeableService;
import org.jerry.frameworks.system.entity.jpa.JobEntity;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

/**
 * <p>Date : 16/5/23</p>
 * <p>Time : 下午3:11</p>
 *
 * @author jerry
 */
@Service
public class JobBiz extends BaseTreeableService<JobEntity, Long> {

    /**
     * 过滤仅获取可显示的数据
     *
     * @param jobIds                职务IDs
     * @param organizationJobIds    组织职务IDs
     */
    public void filterForCanShow(Set<Long> jobIds, Set<Long[]> organizationJobIds) {
        Iterator<Long> iter1 = jobIds.iterator();

        while (iter1.hasNext()) {
            Long id = iter1.next();
            JobEntity jobEntity = findOne(id);
            if (jobEntity == null || Boolean.FALSE.equals(jobEntity.getShow()))
                iter1.remove();
        }

        Iterator<Long[]> iter2 = organizationJobIds.iterator();

        while (iter2.hasNext()) {
            Long id = iter2.next()[1];
            JobEntity jobEntity = findOne(id);
            if (jobEntity == null || Boolean.FALSE.equals(jobEntity.getShow()))
                iter2.remove();
        }
    }
}
