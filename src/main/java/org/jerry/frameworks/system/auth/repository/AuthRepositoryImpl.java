package org.jerry.frameworks.system.auth.repository;
import com.google.common.collect.Sets;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Set;

/**
 * <p>Date : 16/5/19</p>
 * <p>Time : 下午3:17</p>
 *
 * @author jerry
 */
public class AuthRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organzationIds, Set<Long> jobIds, Set<Long[]> organzationJobIds) {

        boolean hasGroupIds             = groupIds.size() > 0;
        boolean hasOrganzationIds       = organzationIds.size() > 0;
        boolean hasJobIds               = jobIds.size() > 0;
        boolean hasOrganzationJobIds    = organzationJobIds.size() > 0;

        StringBuilder hql = new StringBuilder("select roleIds from Auth where ");
        hql.append("(userId = :userId)");

        if (hasGroupIds) {
            hql.append(" or ");
            hql.append(" (groupId in (:groupIds)) ");
        }

        if (hasOrganzationIds) {
            hql.append(" or ");
            hql.append(" ( organzationId in (:organzationIds) and jobId = 0 )");
        }

        if (hasJobIds) {
            hql.append(" or ");
            hql.append(" ( organzationId = 0 and jobId in (:jobIds) )");
        }

        if (hasOrganzationJobIds) {
            int i = 0, l = organzationIds.size();
            while (i < l) {
                hql.append(" or ");
                hql.append(" ( organizationId=:organizationId_" + i + " and jobId=:jobId_" + i + " ) ");
                i++;
            }
        }

        Query query = entityManager.createQuery(hql.toString());
        query.setParameter("userId", userId);

        if (hasGroupIds)
            query.setParameter("groupIds", groupIds);

        if (hasOrganzationIds)
            query.setParameter("organzationIds", organzationIds);

        if (hasJobIds)
            query.setParameter("jobIds", jobIds);

        if (hasOrganzationJobIds) {
            int i = 0;
            for (Long[] organzationJobId : organzationJobIds) {
                query.setParameter("organzationId_" + i, organzationJobId[0]);
                query.setParameter("jobId_" + i, organzationJobId[1]);
                i++;
            }
        }

        List<Set<Long>> roleIdSets = (List<Set<Long>>) query.getResultList();

        Set<Long> roleIds = Sets.newHashSet();
        for (Set<Long> setter : roleIdSets) {
            roleIds.addAll(setter);
        }

        return roleIds;
    }
}
