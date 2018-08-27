package com.yangkang.ssmdemo01.system.interceptor;

import com.yangkang.ssmdemo01.mvc.dao.impl.DaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * LogsInterceptor
 *
 * @author yangkang
 * @date 2018/8/23
 */
public class LogsInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LogsInterceptor.class);

    private ThreadLocal<String> logContext = new NamedThreadLocal<String>("log-id");

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    //先拦截器后aop切面通知,加入spring事务管理
    /**
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，
     * SpringMVC中的Interceptor拦截器是链式的，可以同时存在多个Interceptor，
     * 然后SpringMVC会根据声明的前后顺序一个接一个的执行，
     * 而且所有的Interceptor中的preHandle方法都会在Controller方法调用之前调用。
     * SpringMVC的这种Interceptor链式结构也是可以进行中断的，
     * 这种中断方式是令preHandle的返回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("==========LogsInterceptor run======="+request.getParameter("id"));
        String host = request.getRemoteHost();
        String url = request.getRequestURI();
        logger.debug("=========="+host+"-------"+url);
//        if(request.getParameter("id")!=null){
//            updateUser(request.getParameter("id"),"lili");
//        }
        return true;
    }

    private void updateUser(String userId,String userName) throws Exception{
        Map params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
        int result = (int)dao.update("UserSQL.updateUsernameById",params);
        int excep = 1/0;
    }
}
