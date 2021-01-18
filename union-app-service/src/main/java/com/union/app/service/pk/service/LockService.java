package com.union.app.service.pk.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {


    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public boolean getLock(String lockName, long millisecond) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockName, "lock",
                millisecond, TimeUnit.MILLISECONDS);
        return success != null && success;
    }

    public void releaseLock(String lockName) {
        redisTemplate.delete(lockName);
    }


}
