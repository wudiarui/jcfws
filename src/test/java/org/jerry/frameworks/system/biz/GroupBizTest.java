package org.jerry.frameworks.system.biz;

import org.jerry.frameworks.base.test.BaseTest;
import org.jerry.frameworks.system.entity.jpa.GroupEntity;
import org.jerry.frameworks.system.group.biz.GroupBiz;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Date : 16/5/6</p>
 * <p>Time : 上午9:43</p>
 *
 * @author jerry
 */
public class GroupBizTest extends BaseTest {

    @Autowired
    private GroupBiz groupBiz;

    @Test
    public void testGetGroupByName() throws Exception {
        GroupEntity group = groupBiz.getGroupByName("TestGroup");
        System.out.println(group.getName() + " : " + group.getType());
    }
}