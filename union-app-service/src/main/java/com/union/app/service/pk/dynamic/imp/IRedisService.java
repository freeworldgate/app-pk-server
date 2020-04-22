package com.union.app.service.pk.dynamic.imp;

import org.springframework.stereotype.Service;



interface IRedisService {



    boolean delete(String key);


    int size(String key);

}
