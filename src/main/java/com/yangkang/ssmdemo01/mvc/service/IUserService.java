package com.yangkang.ssmdemo01.mvc.service;

import com.yangkang.ssmdemo01.mvc.entity.ShiroUser;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.entity.User2;

import java.util.List;

public interface IUserService {
//    public User selectUser(long userId);
    public User selectUser2(String userId) throws Exception;
    public void selectUser4(String noUse) throws Exception;
    public void selectUser5(String noUse,String noUse2) throws Exception;
    public int updateUser(String userId, String userName) throws Exception;
    public int updateUser2(String userId, String userName) throws Exception;
    public int testInsertBatch(List<User2> userList) throws Exception;
    public int testInsertBatch2(List<User2> userList) throws Exception;
    public int testInsertBatch2TransactionEnhanced(List<User2> userList) throws Exception;
    public int testInsertBatch2TransactionEnhanced2(List<User2> userList) throws Exception;
    public int testInsertBatch2TransactionEnhanced3(List<User2> userList) throws Exception;
    public int testInsertBatch2TransactionEnhanced3AndThreadPool(List<User2> userList) throws Exception;

    public ShiroUser findByUsername(String username);
    public int testOutFile();
    public int testInFile();
}
