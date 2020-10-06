package com.union.app.common.redis;

import org.springframework.stereotype.Service;



interface IRedisService {



    boolean delete(String key);


    int size(String key);

}
