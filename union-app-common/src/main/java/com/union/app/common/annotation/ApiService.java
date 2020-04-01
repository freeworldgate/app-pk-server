package com.union.app.common.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ApiService {

    String path();

    String desc();

/*
    boolean isTransaction() default false;

    Class<? extends Exception> rollbackFor() default ApiWithJpaTransactionalException.class;

*/


}
