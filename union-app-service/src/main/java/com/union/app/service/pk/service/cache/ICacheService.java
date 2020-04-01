package com.union.app.service.pk.service.cache;

import org.springframework.scheduling.annotation.Scheduled;


public interface ICacheService<T> {


    T getValue(String key);


    void update();

    TimePolicy getTimePolicy();










}
