package com.union.app.service.pk.service.support;

public interface SingleCacheResolver<T> {




    T get(String key);


    void update(String key);




















}
