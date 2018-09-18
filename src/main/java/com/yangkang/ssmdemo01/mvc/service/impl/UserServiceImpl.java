package com.yangkang.ssmdemo01.mvc.service.impl;

import com.yangkang.ssmdemo01.mvc.dao.impl.DaoSupport;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.entity.User2;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("userService")
//@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements IUserService {

    private static Logger logger = LoggerFactory.getLogger("UserServiceImpl.class");
//    @Resource
//    private IUserDao userDao;

    @Resource(name = "daoSupport")
    private DaoSupport dao;

//    public User selectUser(long userId) {
//        return this.userDao.selectUser(userId);
//    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(value = "users",key = "#userId")
    public User selectUser2(String userId) throws Exception {
//        ((IUserService)AopContext.currentProxy()).selectUser5("param1","param2");
        logger.debug("============selectUser2=============");
        return (User)dao.findForObject("UserSQL.selectUserById",userId);
    }

    @Override
    @CacheEvict(value = "users",key = "#noUse")
    public void selectUser4(String noUse) throws Exception {
        logger.debug("============selectUser4=============");
        int tmp = 1/0;  //测试事务回滚时,是否缓存也回滚(实验证明,缓存确实会回滚)
    }

    @Override
    public void selectUser5(String noUse, String noUse2) throws Exception {
        logger.debug("============selectUser5=============");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = "users", key = "#userId")
    public int updateUser(String userId, String userName) throws Exception {
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
        return (int)dao.update("UserSQL.updateUsernameById",params);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = "users", key = "#userId")
    public int updateUser2(String userId, String userName) throws Exception {
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
//        return (int)dao.update("UserSQL.updateUsernameById2",params);
        int result = (int)dao.update("UserSQL.updateUsernameById2",params);
        int excep = 1/0;
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int testInsertBatch(List<User2> userList) throws Exception {
        long millis = new Date().getTime();
        //第一种插入方法
//        logger.debug("-----------批量插入测试START!-----------");
//        int flag = 0;
//        for (User2 user2 : userList)
//            flag += (int)dao.saveBean(user2);
//        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");

        //第二种插入方法
//        logger.debug("-----------批量插入测试START!-----------");
//        int flag = (int)dao.saveBeans(userList);
//        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");

        //第三种插入方法
        logger.debug("-----------批量插入测试START!-----------");
        int flag = (int)dao.saveBeans2(userList);
        logger.debug("-----------批量插入测试END!-----------用时:" + (new Date().getTime() - millis) + "ms");

        return flag;
    }

    @Override
    public int testInsertBatch2(List<User2> userList) throws Exception {
        //第四种插入方法

        return 0;
    }
}
