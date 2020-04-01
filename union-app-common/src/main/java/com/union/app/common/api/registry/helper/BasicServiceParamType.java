package com.union.app.common.api.registry.helper;

import com.union.app.common.api.resolver.ServiceResolver;

import java.util.List;
import java.util.Map;


public enum BasicServiceParamType
{
    /*基本类型*/
    BYTE(byte.class),
    SHORT(short.class),
    INT(int.class),
    LONG(long.class),
    CHAR(char.class),
    FLOAT(float.class),
    DOUBLE(double.class),
    Boolean(boolean.class),

    /*对象类型*/
    BYTE_TYPE(Byte.class),
    SHORT_TYPE(Short.class),
    INT_TYPE(Integer.class),
    LONG_TYPE(Long.class),
    CHAR_TYPE(Character.class),
    FLOAT_TYPE(Float.class),
    DOUBLE_TYPE(Double.class),
    Boolean_TYPE(Boolean.class),

    /*数组类型*/
    BYTE_ARRAY(byte[].class),
    SHORT_ARRAY(short[].class),
    INT_ARRAY(int[].class),
    LONG_ARRAY(long[].class),
    CHAR_ARRAY(char[].class),
    FLOAT_ARRAY(float[].class),
    DOUBLE_ARRAY(double[].class),
    Boolean_ARRAY(boolean[].class),

    /*字符串类型*/
    STRING(String.class),
    STRING_ARRAY(String[].class),

    /*列表*/
    LISR(List.class),

    /*Map类型*/
    MAP(Map.class),




    API_RESOLVER(ServiceResolver.class),





    ;
    private Class<?> paramType;

    BasicServiceParamType(Class<?> paramType) {
        this.paramType = paramType;
    }

}
