package com.union.app.common.api.registry;

import com.union.app.common.api.registry.bean.ServiceBean;
import com.union.app.common.api.registry.bean.ServiceUrlBean;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ServiceRegistry {

    private static final Map<ServiceUrlBean,ServiceBean> serviceMap = new ConcurrentHashMap<>();

    private static final Map<String,ServiceUrlBean> serviceUrlMap = new ConcurrentHashMap<>();

    private static final Map<Class<?>,Object> serviceObjectMap = new ConcurrentHashMap<>();


    public static ServiceBean getService(String serviceUrl)
    {
        ServiceUrlBean serviceUrlBean = serviceUrlMap.get(serviceUrl.toUpperCase());
        if(serviceUrlBean != null)
        {
            return serviceMap.get(serviceUrlBean);
        }
        return null;
    }

    public static void registerService(String serviceUrl,ServiceUrlBean serviceUrlBean,ServiceBean serviceBean) throws IllegalAccessException, InstantiationException {
        Class<?> serviceClass = serviceBean.getServiceClass();
        Object serviceObject = serviceObjectMap.get(serviceClass);
        if(ObjectUtils.isEmpty(serviceObject))
        {
            serviceObject = serviceClass.newInstance();
            serviceObjectMap.put(serviceClass,serviceObject);
        }

        serviceBean.setServiceObject(serviceObject);
        serviceUrlMap.put(serviceUrl.toUpperCase(),serviceUrlBean);
        serviceMap.put(serviceUrlBean,serviceBean);
    }








}
