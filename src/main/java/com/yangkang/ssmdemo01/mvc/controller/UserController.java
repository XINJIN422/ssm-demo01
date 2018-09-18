package com.yangkang.ssmdemo01.mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.entity.User2;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IUserService userService;

    @RequestMapping("/showUser")
    public void selectUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("=============selectUser start!================");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        long userId = Long.parseLong(request.getParameter("id"));
//        User user = this.userService.selectUser(userId);
        User user = this.userService.selectUser2(String.valueOf(userId));
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(user));
        response.getWriter().close();
        logger.debug("=============selectUser end!================");
//        selectUser3();
//        userService.selectUser4("1");
//        userService.selectUser2("1");
    }

//    public void selectUser3(){
//        logger.debug("=================just test==============");
//    }

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

    /**
     * 这个方法主要是前台来触发测试用的
     */
    @RequestMapping("/testInsertBatch")
    public void testInsertBatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");

        List<User2> user2List = new ArrayList<>();
        User2 user2 = new User2();
        user2.setEmail("112233@qq.com");
        user2.setPassword("112233");
        user2.setUsername("测试0");
        user2.setRole("test");
        user2.setRegtime(new Date());
        //1.首先测试如果非空字符如果为空报什么错,以及integer类型的包装类初始化是否为null
        //这边遇到一个问题,就是在mybatis中$与#的区别,${}在预编译前就将变量替换,并且字符串不自动加引号,
        //      而#{}则是先用?占位,之后如果是字符串会自动加引号;${}无法防止sql注入,#{}可以;另外还有<![CDATA[]]>防止<,>转义;
        //integer类型包装类初始化为null,这样之后的反射解析就不会出现无法判断0是默认初始值还是自己赋的值
        //非空字段如果为空会报该字段无值的错
        user2.setRegip("127.0.0.1");
        user2.setStatus(0);
        user2List.add(user2);
        //2.接下来测试大批量的数据执行效率
        // 2-1.第一种在service层循环遍历一个个插入,插入5001条数据耗时14388ms/5618ms/6171ms/6450ms/7905ms/7905ms/5840ms/7649ms,全程只有一个数据库连接会话被调用
        // 2-2.第二种在dao层通过动态生成sql一次性插入5001条数据,用时1350ms/1400ms,仅提交一次(如果对象过大可以拆开几次提交)
        // 2-3.第三种在dao层分批次提交批量插入,扩展为可以插入不同的对象,插入5001条数据,设置提交1次,耗时13878ms/9299ms,
        //      设置提交6次,耗时3840ms/3652ms,设置提交11次,耗时2980ms/3230ms,应该是比第二种长一点,比第一种短一点
        // 2-4.第四种在service层将5001条数据分成6个线程,每个线程启用一个数据源连接进行插入,
        int start = 1;
        long millis = new Date().getTime();
        while (start <= 5000){
            user2 = new User2();
            user2.setEmail(112233 + start + "@qq.com");
//            if (start != 233)  //测试第三种主动提交是否会影响事务,结果是不会,有异常依然会回滚
                user2.setPassword("112233");
            user2.setUsername("测试"+start);
            user2.setRole("test");
            user2.setRegtime(new Date());
            user2.setRegip("127.0.0.1");
            user2.setStatus(0);
            user2List.add(user2);
            start++;
        }
        logger.debug("循环生成5000个对象用时------------" + (new Date().getTime() - millis) + "ms");     //生成对象用时17ms/5ms/3ms/2ms/3ms/5ms
        int result = userService.testInsertBatch(user2List);
        if (result > 5000)
            response.getWriter().write("测试成功!\r\n");
        else
            response.getWriter().write("测试失败!\r\n");
        response.getWriter().close();
    }
}
