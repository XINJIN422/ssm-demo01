package com.yangkang.ssmdemo01.mvc.service.impl;

import com.yangkang.ssmdemo01.mvc.dao.impl.DaoSupport;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("userService")
//@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements IUserService {

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
        return (User)dao.findForObject("UserSQL.selectUserById",userId);
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
