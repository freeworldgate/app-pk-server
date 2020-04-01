package com.union.app.common.api.registry.helper;

import com.union.app.common.annotation.ApiService;
import com.union.app.common.api.registry.ServiceRegistry;
import com.union.app.common.api.registry.bean.ServiceBean;
import com.union.app.common.api.registry.bean.ServiceExceptionBean;
import com.union.app.common.api.registry.bean.ServiceParamBean;
import com.union.app.common.api.registry.bean.ServiceUrlBean;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;






public abstract class ServiceBeanHelper
{
/*

    */
/**
     * 注册服务类
     * @param serviceClass
     *//*

    public static void analyserClass(Class<?> serviceClass) throws AppException {
        //获取所有服务
        List<Method> services = getServiceMethod(serviceClass);
        for(Method method:services)
        {
            try {
                analyserServiceMethod(serviceClass,method);
            } catch (Exception e) {
                throw AppException.buildAppException(ResultCode.E05000002,e);
            }
        }
    }
*/









    private static void analyserServiceMethod(Class<?> serviceClass, Method method) throws InstantiationException, IllegalAccessException {
        ServiceUrlBean serviceUrlBean = analyserServiceUrl(serviceClass,method);
        ServiceBean serviceBean = analyserServiceBean(serviceUrlBean,serviceClass,method);
        ServiceRegistry.registerService(serviceUrlBean.contactUrl(),serviceUrlBean,serviceBean);
    }

    private static ServiceBean analyserServiceBean(ServiceUrlBean serviceUrlBean,Class<?> serviceClass, Method method)
    {
        ServiceBean serviceBean = new ServiceBean();
        serviceBean.setServiceClass(serviceClass);
        serviceBean.setServiceMethod(method);
        serviceBean.setResponseClass(method.getReturnType());
        serviceBean.setServiceUrl(serviceUrlBean);
        serviceBean.setServiceExceptionBean(analyserServiceException(method));
        serviceBean.setServiceParamBeans(analyserServiceParam(method));
        return serviceBean;
    }


    private static List<ServiceExceptionBean> analyserServiceException(Method method)
    {
        List<ServiceExceptionBean> serviceExceptionBeans = new ArrayList<>();
        Class<?>[] exceptionClasses = method.getExceptionTypes();
        for(Class<?> exceptionClass:exceptionClasses)
        {
            ServiceExceptionBean serviceExceptionBean = new ServiceExceptionBean();
            serviceExceptionBean.setExceptionClass(exceptionClass);
            serviceExceptionBeans.add(serviceExceptionBean);
        }
        return serviceExceptionBeans;
    }

    private static List<ServiceParamBean> analyserServiceParam(Method method)
    {
        List<ServiceParamBean> serviceParamBeans = new ArrayList<>();
        Parameter[] paramters = method.getParameters();
        for(Parameter parameter : paramters)
        {
            ServiceParamBean serviceParamBean = analyserServiceSingleParam(parameter);
            serviceParamBeans.add(serviceParamBean);
        }
        return serviceParamBeans;
    }

    private static ServiceParamBean analyserServiceSingleParam(Parameter parameter)
    {
        ServiceParamBean serviceParamBean = new ServiceParamBean();
        serviceParamBean.setServiceParamName(parameter.getName());
        /*处理形参类型*/
        serviceParamBean.setServiceParamType(parameter.getType());
        serviceParamBean.setAnnotationList(Arrays.asList(parameter.getAnnotations()));
        System.out.println(serviceParamBean.toString());
        return serviceParamBean;
    }


    private static ServiceUrlBean analyserServiceUrl(Class<?> serviceClass, Method method)
    {
        ServiceUrlBean serviceUrlBean = new ServiceUrlBean();

        if(serviceClass.isAnnotationPresent(ApiService.class))
        {
            ApiService apiService = serviceClass.getAnnotation(ApiService.class);
            serviceUrlBean.setServiceUrl(apiService.path());
            serviceUrlBean.setServiceDesc(apiService.desc());
        }
        ApiService apiService = method.getAnnotation(ApiService.class);
        serviceUrlBean.setApiUrl(apiService.path());
        serviceUrlBean.setApiDesc(apiService.desc());
        return serviceUrlBean;
    }

    /**
     * @param serviceClass
     * @return
     */
    private static List<Method> getServiceMethod(Class<?> serviceClass)  {
        List<Method> serviceMethods = new ArrayList<>();
        Method[] methods = serviceClass.getDeclaredMethods();
        for(Method method:methods)
        {
            if(method.isAnnotationPresent(ApiService.class))
            {
                int modifier = method.getModifiers();
                if(Modifier.isAbstract(modifier)||Modifier.isStatic(modifier)||Modifier.isPrivate(modifier)||Modifier.isProtected(modifier))
                {
//                    throw AppException.buildAppException(ResultCode.E05000001,null);
                }
                serviceMethods.add(method);
            }
        }
        return serviceMethods;
    }


}
