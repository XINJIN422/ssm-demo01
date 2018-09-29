package com.yangkang.ssmdemo01.tools;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * SpringContextsUtil
 * spring容器工具类,用以获得里面的bean
 *
 * @author yangkang
 * @date 2018/9/27
 */
public class SpringContextsUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextsUtil.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    /**
     * 获取一个以所给名字注册的bean的实例
     * @param beanName
     * @return
     * @throws BeansException
     */
    public static Object getBean(String beanName) throws BeansException{
        return applicationContext.getBean(beanName);
    }

    /**
     * 获取类型为requiredType的对象
     * 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     * @param beanName
     * @param requireType
     * @return
     * @throws BeansException
     */
    public static Object getBean(String beanName, Class requireType) throws BeansException{
        return applicationContext.getBean(beanName, requireType);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     * @param beanName
     * @return
     * @throws BeansException
     */
    public static boolean containsBean(String beanName){
        return applicationContext.containsBean(beanName);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     * @param beanName
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String beanName) throws NoSuchBeanDefinitionException{
        return applicationContext.isSingleton(beanName);
    }

    /**
     * 获取注册对象的类型
     * @param beanName
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static Class getType(String beanName) throws NoSuchBeanDefinitionException{
        return applicationContext.getType(beanName);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     * @param beanName
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String beanName) throws NoSuchBeanDefinitionException{
        return applicationContext.getAliases(beanName);
    }
}
