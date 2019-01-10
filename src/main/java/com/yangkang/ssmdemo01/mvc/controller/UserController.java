package com.yangkang.ssmdemo01.mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.entity.User2;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import com.yangkang.ssmdemo01.redis.MyRedisCacheUtil;
import com.yangkang.ssmdemo01.tools.SpringContextsUtil;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IUserService userService;

//    @Resource
    private MyRedisCacheUtil myRedisCacheUtil;

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
        // 2-3.第三种在dao层分批次预提交批量插入,上面方法是一个数据源连接只预提交一次,正式提交一次,这种方法一个数据源连接预提交多次,正式提交一次,statement:created->executed->closed
        //      并扩展为可以插入不同的对象,插入5001条数据,设置提交1次,耗时13878ms/9299ms,
        //      设置提交6次,耗时3840ms/3652ms,设置提交11次,耗时2980ms/3230ms,应该是比第二种长一点,比第一种短一点
        // 2-4.第四种在service层将5001条数据分成6个线程,每个线程启用一个数据源连接进行插入,用时1382ms/429ms/420ms/508ms/298ms/196ms,
        //      分成51个线程,用时1590ms,1278ms,968ms,598ms,1420ms,因为这边最大只有10个数据源连接,所以应该最大只有10个线程在同时运行,
        //      其实分成10个进程,然后综合第二和第三种方式分次提交,并且主键也不要让mysql自增长生成,用多线程在java里生成,这样应该会更快一些
        //      [多线程事务增强版]6个线程,耗时1720ms/467ms/760ms/258ms
        //      [多线程事务增强版2]6个线程,耗时1415ms/368ms/567ms/237ms
        //      [多线程事务增强版3]6个线程,耗时1360ms/594ms/508ms/270ms/255ms,缩小lock区间后,耗时1267ms,622ms,655ms,388ms,200ms,488ms,362ms
        //      [多线程事务增强版3+线程池]6个线程复用,耗时1920ms/784ms/393ms/529ms/195ms/294ms/172ms,线程死亡后耗时379ms
        int start = 1;
        long millis = new Date().getTime();
        while (start <= 5000){
            user2 = new User2();
            user2.setEmail(112233 + start + "@qq.com");
            //测试第三种主动提交是否会影响事务,结果是不会,有异常依然会回滚,异常报错在statement的executed阶段
            //测试第四种多线程时是否会影响事务,结果是会,有异常其他线程也未回滚,需要修改,testInsertBatch2TransactionEnhanced增加了多线程的事务控制
//            if (start != 1233)
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
//        int result = userService.testInsertBatch(user2List);
//        int result = userService.testInsertBatch2(user2List);
//        int result = userService.testInsertBatch2TransactionEnhanced(user2List);
//        int result = userService.testInsertBatch2TransactionEnhanced2(user2List);
//        int result = userService.testInsertBatch2TransactionEnhanced3(user2List);
        int result = userService.testInsertBatch2TransactionEnhanced3AndThreadPool(user2List);
        if (result > 5000)
            response.getWriter().write("测试成功!\r\n");
        else
            response.getWriter().write("测试失败!\r\n");
        response.getWriter().close();
    }

    @RequestMapping("/login")
    public String testShiroAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("GBK");       //chrome浏览器直接显示文本默认gbk,懒得下插件改编码格式了
        String exceptionClassName = (String)request.getAttribute(
                ((FormAuthenticationFilter)SpringContextsUtil.getBean("formAuthenticationFilter", FormAuthenticationFilter.class))
                .getFailureKeyAttribute());
        String error = null;
        if(UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if(IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if(exceptionClassName != null) {
            error = "其他错误：" + exceptionClassName;
        }
        if (error != null){
            response.getWriter().write(error + "\r\n");
            response.getWriter().close();
            return null;
        } else {
            return "redirect:../login.html";
        }
    }

    /**
     * 这个方法主要测试redistemplate读取key是否需要指定cachename(不需要)
     * ,读取后能否直接强转,还是有其他反序列化方法调用(直接强转即可)
     * ,写对象的时候是否会自动根据配置来序列化,写完能否被其他注解读取(直接写,会按照指定方式自动序列化;并且数据能被注解读取)
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/testRedisUtil")
    public void testRedisUtil(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("GBK");
        User user = (User)myRedisCacheUtil.get("[rediscache3-2]keyGenerator2--[1]");
        response.getWriter().write(new ObjectMapper().writeValueAsString(user));
        response.getWriter().close();
        myRedisCacheUtil.set("[rediscache3-2]keyGenerator2--[1]", user);
    }

    /**
     * 测试redis的事务
     * 测试证明,只要在方法上加上transaction注释,就能保证整个方法里的redisTemplate操作都在一个事务中
     * 不需要再调用redisTemplate.multi()和redisTemplate.exec()方法了
     * @param request
     * @param response
     */
    @RequestMapping("/testJedisTransaction")
    public void testJedisTransaction(HttpServletRequest request, HttpServletResponse response){
        userService.testJedisTransaction();
    }
}
