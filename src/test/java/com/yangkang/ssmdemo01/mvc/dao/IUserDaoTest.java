package com.yangkang.ssmdemo01.mvc.dao;

import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-config.xml"})
public class IUserDaoTest {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IUserService userService;

    @Test
    public void testSelectUser() throws Exception {
//        long id = 1;
//        User user = userDao.selectUser(id);
        String id = "1";
        User user = userService.selectUser2(id);
        System.out.println(user.getUsername());
    }
}
