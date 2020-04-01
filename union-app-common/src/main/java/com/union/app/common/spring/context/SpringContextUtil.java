package com.union.app.common.spring.context;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 直接通过Spring 上下文获取SpringBean,用于多线程环境
 * by lida @20170629
 * @author root
 */
@Component("springContextUtil")
public class SpringContextUtil implements ApplicationContextAware {

    private static ConfigurableApplicationContext applicationContext;

    private static BeanDefinitionRegistry beanDefinitionRegistry;
    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringContextUtil.applicationContext = (ConfigurableApplicationContext)applicationContext;
        SpringContextUtil.beanDefinitionRegistry = (DefaultListableBeanFactory)SpringContextUtil.applicationContext.getBeanFactory();
    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * 获取对象 这里重写了bean方法，起主要作用
     * example: getBean("userService")//注意： 类名首字母一定要小写！
     */
    public static Object getBean(String beanId) throws BeansException {
        return applicationContext.getBean(beanId);
    }

    public static <T> T getBean(Class<T> tClass)
    {
        return applicationContext.getBean(tClass);
    }


    public static void registerBean(String beanId,Class<?> clazz) {
        // get the BeanDefinitionBuilder
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(clazz);
        // get the BeanDefinition
        BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
        // register the bean
        beanDefinitionRegistry.registerBeanDefinition(beanId,beanDefinition);
    }












}
