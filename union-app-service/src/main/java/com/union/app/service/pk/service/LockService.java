package com.union.app.service.pk.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private String lock_key = "redis_lock"; //锁键
    private String lock_value = "redis_value"; //锁键

    protected long internalLockLeaseTime = 10000;//锁过期时间
    protected long timeout  = 30000;//锁过期时间


    private static final Long SUCCESS = 1L;



    public boolean getLock(String lockKey,LockType lockType){
        String key = lock_key+lockKey+lockType.getName();
        String value = lock_value+lockKey+lockType.getName();

        boolean ret = false;
        Long start = System.currentTimeMillis();
        try{
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

            RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

            for(;;)
            {
                Long result = redisTemplate.execute(redisScript, Collections.singletonList(key),value,internalLockLeaseTime);
                if(SUCCESS.equals(result)){
                    return true;
                }
                long l = System.currentTimeMillis() - start;
                if (l>=timeout) {
                    return false;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



        }catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    public boolean releaseLock(String lockKey,LockType lockType) {
        String key = lock_key+lockKey+lockType.getName();
        String value = lock_value+lockKey+lockType.getName();

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), value);
        if (SUCCESS.equals(result)) {
            return true;
        }

        return false;
    }
}
