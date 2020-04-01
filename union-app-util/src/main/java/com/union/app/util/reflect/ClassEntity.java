package com.union.app.util.reflect;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;


@Setter
@Getter
@ToString
public class ClassEntity
{

    private String className;

    private Class<?> sourceClass;

    private Collection<? extends Annotation> classAnnotations = new ArrayList<>();

    private Collection<MethodEntity> methodEntities = new ArrayList<>();

    private Collection<FieldEntity> fieldEntities = new ArrayList<>();



}
