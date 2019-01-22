package com.yangkang.ssmdemo01.system.interceptor;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MyPageInterceptor
 * 基于AOP的分页拦截器(所有Paging结尾的方法都会自动分页)
 *
 * @author yangkang
 * @date 2019/1/22
 */
@Aspect
@Component
public class MyPageInterceptor{

    @Around("execution(* com.yangkang.ssmdemo01.mvc.controller..*.*Paging(..))")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        PageHelper.startPage(2, 3);
        List list = (List)point.proceed();
        PageInfo pageInfo = new PageInfo<>(list, 3);
        return pageInfo;
    }
}
