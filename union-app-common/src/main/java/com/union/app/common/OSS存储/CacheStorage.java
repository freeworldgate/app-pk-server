package com.union.app.common.OSS存储;

import com.alibaba.fastjson.JSON;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheStorage {


    @Autowired
    RedisStringUtil redisStringUtil;


    public <T> T getKey(String key,Class<T> tClass){

        String value = redisStringUtil.get(key);
        T t = JSON.parseObject(value,tClass);
        return t;
    }
    public <T> boolean setKey(String key,T t){
        return redisStringUtil.set(key,JSON.toJSONString(t));
    }

    public <T> T getMapKey(String key,String mapKey,Class<T> tClass){
        T t = redisStringUtil.getMapKey(key,mapKey,tClass);
        return t;
    }

    public <T> boolean setMapKey(String key,String mapKey,T t){
        return redisStringUtil.setMapKey(key,mapKey,t);
    }

    public boolean hasMapKey(String key, String item){
        return redisStringUtil.isMapKeyExist(key,item);
    }

    public long mapSize(String key){
        return redisStringUtil.mapSize(key);
    }

    public void deleteMapKey(String key, String mapKey) {
        redisStringUtil.deleteMapKey(key,mapKey);
    }



}
