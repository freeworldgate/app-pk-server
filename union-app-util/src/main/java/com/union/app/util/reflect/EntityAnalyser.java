package com.union.app.util.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class EntityAnalyser
{

    public static ClassEntity analyserClass(Class<?> entityClass)
    {
        if(entityClass.isAnnotation()||entityClass.isAnonymousClass()||
           entityClass.isArray()||entityClass.isEnum()||
           entityClass.isInterface()||entityClass.isPrimitive() || Modifier.isAbstract(entityClass.getModifiers())){
            return null;
        }
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassName(entityClass.getSimpleName());
        classEntity.setSourceClass(entityClass);
        classEntity.setClassAnnotations(Arrays.asList(entityClass.getAnnotations()));

        Collection<MethodEntity> methodEntities = analyserMethod(entityClass);
        Collection<FieldEntity> fieldEntities = analyserField(entityClass);

        classEntity.setMethodEntities(methodEntities);
        classEntity.setFieldEntities(fieldEntities);
        return classEntity;

    }

    private static Collection<FieldEntity> analyserField(Class<?> entityClass)
    {
        Collection<FieldEntity> fieldEntities = new ArrayList<>();
        Field[] fields = entityClass.getFields();
        for(Field field:fields)
        {
            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setField(field);
            fieldEntity.setFieldName(field.getName());
            fieldEntity.setFieldClass(field.getDeclaringClass());
            fieldEntity.setAnnotations(Arrays.asList(field.getAnnotations()));
        }

        return fieldEntities;
    }

    private static Collection<MethodEntity> analyserMethod(Class<?> entityClass)
    {
        Collection<MethodEntity> methodEntities = new ArrayList<>();
        Method[] methods = entityClass.getMethods();
        for(Method method:methods)
        {
            MethodEntity methodEntity = new MethodEntity();
            methodEntity.setMethodName(method.getName());
            methodEntity.setMethod(method);
            methodEntity.setModifier(method.getModifiers());
            methodEntity.setAnnotations(Arrays.asList(method.getAnnotations()));
            methodEntity.setExceptionType(Arrays.asList(method.getExceptionTypes()));
            methodEntity.setReturnType(method.getReturnType());
            Collection<ParamterEntity> paramterEntities = analyserMethdParamters(method);
            methodEntity.setParamterEntities(paramterEntities);
            methodEntities.add(methodEntity);
        }
        return methodEntities;
    }

    private static Collection<ParamterEntity> analyserMethdParamters(Method method) {
        Collection<ParamterEntity> paramterEntities = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for(Parameter parameter:parameters)
        {
            ParamterEntity paramterEntity = new ParamterEntity();
            paramterEntity.setParamterName(parameter.getName());
            paramterEntity.setParamterType(parameter.getType());
            paramterEntity.setParamterTypesAnnotations(Arrays.asList(parameter.getAnnotations()));
        }
        return paramterEntities;
    }


}
