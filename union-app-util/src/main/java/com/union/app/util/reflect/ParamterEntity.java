package com.union.app.util.reflect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.Collection;

@Getter
@Setter
@ToString
public class ParamterEntity
{

    private String paramterName;

    private Class<?> paramterType;

    private Collection<? extends Annotation> paramterTypesAnnotations;







}
