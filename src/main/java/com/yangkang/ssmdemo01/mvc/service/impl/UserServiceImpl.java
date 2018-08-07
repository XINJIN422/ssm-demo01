package com.yangkang.ssmdemo01.mvc.service.impl;

import com.yangkang.ssmdemo01.mvc.dao.IUserDao;
import com.yangkang.ssmdemo01.mvc.dao.impl.DaoSupport;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserDao userDao;

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    public User selectUser(long userId) {
        return this.userDao.selectUser(userId);
    }

    @Override
    public User selectUser2(String userId) throws Exception {
        return (User)dao.findForObject("UserSQL.selectUserById",userId);
    }
}
