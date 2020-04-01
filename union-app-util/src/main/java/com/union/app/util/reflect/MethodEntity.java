package com.union.app.util.reflect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@ToString
public class MethodEntity
{

    private String methodName;

    private Method method;

    private int modifier;

    private Collection<? extends Annotation> annotations = new ArrayList<>();

    private Collection<ParamterEntity> paramterEntities = new ArrayList<>();

    private Class<?> returnType;

    private Collection<Class<?>> exceptionType = new ArrayList<>();

}
