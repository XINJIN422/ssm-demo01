package com.yangkang.ssmdemo01.mvc.dao;

import com.yangkang.ssmdemo01.mvc.entity.User;

public interface IUserDao {

    User selectUser(long id);

}
