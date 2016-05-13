package org.jerry.frameworks.base.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.jerry.frameworks.base.entity.AbstractEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * <p>Date : 16/5/6</p>
 * <p>Time : 下午1:39</p>
 *
 * @author jerry
 */
@ContextConfiguration(locations = {
        "classpath:spring-config.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class BaseTest extends AbstractTransactionalJUnit4SpringContextTests {

    @PersistenceContext
    protected EntityManager entityManager;

    protected String nextRandom() {
        return System.currentTimeMillis() + RandomStringUtils.randomNumeric(5);
    }

    protected void flush() {
        entityManager.flush();
    }

    protected void clear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected void deleteAll(List<? extends AbstractEntity> entityList) {
        for (AbstractEntity m : entityList) {
            delete(m);
        }
    }

    protected void delete(AbstractEntity m) {
        m = entityManager.find(m.getClass(), m.getId());
        if (m != null) {
            entityManager.remove(m);
        }
    }
}
