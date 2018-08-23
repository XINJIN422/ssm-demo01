package com.yangkang.ssmdemo01.mvc.service.impl;

import com.yangkang.ssmdemo01.mvc.dao.impl.DaoSupport;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
    public User selectUser2(String userId) throws Exception {
//        ((IUserService)AopContext.currentProxy()).selectUser5("param1","param2");
        return (User)dao.findForObject("UserSQL.selectUserById",userId);
    }

    @Override
    public void selectUser4(String noUse) throws Exception {
        logger.debug("============selectUser4=============");
    }

    @Override
    public void selectUser5(String noUse, String noUse2) throws Exception {
        logger.debug("============selectUser5=============");
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public int updateUser(String userId, String userName) throws Exception {
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
        return (int)dao.update("UserSQL.updateUsernameById",params);
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public int updateUser2(String userId, String userName) throws Exception {
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
//        return (int)dao.update("UserSQL.updateUsernameById2",params);
        int result = (int)dao.update("UserSQL.updateUsernameById2",params);
        int excep = 1/0;
        return result;
    }
}
