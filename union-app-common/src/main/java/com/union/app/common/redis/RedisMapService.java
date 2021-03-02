package com.union.app.common.redis;


import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.plateform.constant.ConfigItem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisMapService implements IRedisService {





    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void removeMapKey(String key,String mapKey) {
        redisTemplate.opsForHash().delete(key, mapKey);

    }


    public String getStringValue(String key,String mapKey){
        Object value = redisTemplate.opsForHash().get(key,mapKey);

        if(ObjectUtils.isEmpty(value)){
            return "";
        }
        else {
            return (String)value;
        }
    }
    public void setStringValue(String key,String mapKey,String value){
        redisTemplate.opsForHash().put(key,mapKey,value);
        redisTemplate.expire(key, AppConfigService.getConfigAsInteger(ConfigItem.RedisMapKey缓存时间), TimeUnit.SECONDS);
    }

    public long getlongValue(String key,String mapKey){
        Object value = redisTemplate.opsForHash().get(key,mapKey);
        if(ObjectUtils.isEmpty(value)){
            return 0L;
        }
        else {
            return Long.valueOf(value.toString());
        }
    }
    public void setLongValue(String key,String mapKey,long value){
        redisTemplate.opsForHash().put(key,mapKey,value);
    }
    public long valueIncr(String key,String mapKey,long value){
        long res = redisTemplate.opsForHash().increment(key,mapKey,value);
        return res;
    }

    public long valueIncr(String key,String mapKey)
    {
        long value = redisTemplate.opsForHash().increment(key,mapKey,1L);
        return value;
    }
    public long valueDecr(String key,String mapKey){
        long value = redisTemplate.opsForHash().increment(key,mapKey,-1L);
        if(value < 0){
            value = this.valueIncr(key,mapKey);
        }
        return value;
    }
    public void valueDecr(String key,String mapKey,long value){

        long res = redisTemplate.opsForHash().increment(key,mapKey,-value);
        if(res < 0){
            redisTemplate.opsForHash().put(key,mapKey,0L);
        }

    }

    public <T> List<T> values(String key,Class<T> tClass){
        List<T> allValues = new ArrayList<>();
        List<Object> values = redisTemplate.opsForHash().values(key);
        for(Object object:values)
        {
            allValues.add(JSON.parseObject(object.toString(),tClass));
        }
        return allValues;

    }


    public <T> T getValue(String key,String mapKey,Class<T> tClass){

        Object value = redisTemplate.opsForHash().get(key,mapKey);

        return JSON.parseObject(ObjectUtils.isEmpty(value)?"":value.toString(),tClass);

    }


    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public int size(String key) {
        if(redisTemplate.hasKey(key)) {
            Long size = redisTemplate.opsForHash().size(key);
            return ObjectUtils.isEmpty(size) ? 0 : size.intValue();
        }
        else
        {
            return 0;
        }
    }

    public boolean hasKey(String key, String mapKey) {
        return redisTemplate.opsForHash().hasKey(key,mapKey);


    }
}
