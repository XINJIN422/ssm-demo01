package com.yangkang.ssmdemo01.mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @RequestMapping("/showUser")
    public void selectUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        long userId = Long.parseLong(request.getParameter("id"));
//        User user = this.userService.selectUser(userId);
        User user = this.userService.selectUser2(String.valueOf(userId));
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(user));
        response.getWriter().close();
    }

    @RequestMapping("/updateUser")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        User userBefore = userService.selectUser2(userId);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(userBefore)+" \r\n");
        int result = userService.updateUser(userId, userName);
        response.getWriter().write(String.valueOf(result)+" \r\n");
        User userAfter = userService.selectUser2(userId);
        response.getWriter().write(objectMapper.writeValueAsString(userAfter)+" \r\n");
        //在updateUser2方法里造一个异常,看看是否全部回滚(实验后发现只是将该方法的事务回滚了,上面的方法没有回滚)
        int result2 = userService.updateUser2(userId,"kiki");
        response.getWriter().write(String.valueOf(result2)+" \r\n");
        User userLast = userService.selectUser2(userId);
        response.getWriter().write(objectMapper.writeValueAsString(userLast)+" \r\n");

        response.getWriter().close();
    }

}
