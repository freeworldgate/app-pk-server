package com.union.app.common.api.registry.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@ToString
public class ServiceBean
{

    /**
     * 所屬類的API描述
     */
    private Class<?> serviceClass;
    private Method serviceMethod;
    private Class<?> responseClass;
    private ServiceUrlBean serviceUrl;
    private List<ServiceExceptionBean> serviceExceptionBean;
    private List<ServiceParamBean> serviceParamBeans;
    private Object serviceObject;







}
