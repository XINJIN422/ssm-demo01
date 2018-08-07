package com.yangkang.ssmdemo01.mvc.dao;

import com.yangkang.ssmdemo01.mvc.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})    配置了web容器启动监听器并指定位置后就不需要了
public class IUserDaoTest {

    @Autowired
    private IUserDao dao;

    @Test
    public void testSelectUser() throws Exception {
        long id = 1;
        User user = dao.selectUser(id);
        System.out.println(user.getUsername());
    }
}
