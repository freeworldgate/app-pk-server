package com.union.app.common.api.registry.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.List;

@Getter
@Setter
@ToString
public class ServiceParamBean
{

    private Class<?> serviceParamType;

    private String serviceParamName;

    private List<Annotation> annotationList;

}
