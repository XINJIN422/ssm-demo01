package com.yangkang.ssmdemo01.mvc.service;

import com.yangkang.ssmdemo01.mvc.entity.User;

public interface IUserService {
//    public User selectUser(long userId);
    public User selectUser2(String userId) throws Exception;
    public int updateUser(String userId, String userName) throws Exception;
    public int updateUser2(String userId, String userName) throws Exception;
}
