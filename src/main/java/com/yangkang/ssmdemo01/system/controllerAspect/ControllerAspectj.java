package com.yangkang.ssmdemo01.system.controllerAspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * ControllerAspectj
 * 练习spring的注解式切面编程
 * @author yangkang
 * @date 2018/8/22
 */
@Aspect //定义切面 for aop
@Component //配合<context:component-scan/>，不然扫描不到 for auto scan
@Order(0) //定义该切面切入的顺序,因为spring事务管理也是一个切面,这边在事务之前执行;参数越小，优先级越高，默认的优先级最低 execute before @Transactional
public class ControllerAspectj {

    private static Logger logger = LoggerFactory.getLogger(ControllerAspectj.class);

//    //定义切入点
//    //经测试,控制层也可以编织切面,但是必须在spring-mvc.xml中配置spring aop
//    //经测试,定义的切入点和通知的参数和源方法参数个数类型不匹配启动会报错,切入点也不会匹配(不写参数倒可以)
////    @Pointcut(value = "execution(* com.yangkang.ssmdemo01.mvc.controller.*.*(..))")
//    @Pointcut(value = "execution(* com.yangkang.ssmdemo01.mvc.service.impl.*.*(..)) && args(noUse)", argNames = "noUse")
//    public void beforePointcut(String noUse){
//
//    }
//
//    //定义通知
//    @Before(value = "beforePointcut(noUse)", argNames = "noUse")
//    public void beforeAdvice(String noUse){
//        logger.debug("===========before point cut run============"+noUse);
//    }
//
//    //通知与切入点定义一步到位,并且和上面的通知相比执行顺序是由上往下执行的
//    @Before(value = "execution(* com.yangkang..*.impl.*.selectUser5(..)) && args(noUse,noUse2)", argNames = "noUse,noUse2")
//    public void beforeAdvice2(String noUse, String noUse2){
//        logger.debug("===========before point cut run2==========="+noUse+noUse2);
//    }
//
//    //JoinPoint参数
//    @Before(value = "execution(* com.yangkang..*.impl.*.selectUser*(..))")
//    public void beforeAdvice3(JoinPoint jp){
//        String packageName = jp.getSignature().getDeclaringTypeName();
//        String instanceName = jp.getThis().toString();
//        String methodName = jp.getSignature().getName();
//        Object[] args = jp.getArgs();
//        logger.debug("===========before point cut run3==========="
//                +packageName+"->"+instanceName+"->"+methodName+"->"+jp.getTarget().getClass()+"->"+jp.getTarget().getClass().getCanonicalName());
//        for (Object arg:args){
//            logger.debug("=========="+arg);
//        }
//    }

    //控制层切面
    @Before("execution(* com.yangkang.ssmdemo01.mvc.controller.*.*(..))")
    public void beforeAdvice4(){
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest(); //获取request 可以从中获取参数或cookie
        logger.debug("===========before point cut run4===========");
    }
}
