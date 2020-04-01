package com.union.app.util.reflect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
@ToString
public class FieldEntity
{

    private String fieldName;

    private Field field;

    private Class<?> fieldClass;

    private Collection<? extends Annotation> annotations = new ArrayList<>();






}
