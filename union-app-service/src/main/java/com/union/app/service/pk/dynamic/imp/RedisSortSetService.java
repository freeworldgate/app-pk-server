package com.union.app.service.pk.dynamic.imp;


import com.alibaba.fastjson.JSON;
import com.union.app.domain.pk.integral.UserIntegral;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RedisSortSetService implements IRedisService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public Set<String> queryPage(String key,int page){
        Set<String> pageList = new HashSet<>();
        Set<String> sorts = redisTemplate.opsForZSet().range(key,  page * 50,(page+1) * 50 - 1);
        if(!CollectionUtils.isEmpty(sorts)){
            pageList.addAll(sorts);
        }
        return pageList;
    }


    public long getIndex(String key,String member){
        Long index = redisTemplate.opsForZSet().rank(key,member);
        return ObjectUtils.isEmpty(index)?-1:index;
    }

    public void addEle(String key,String value,double score){
        redisTemplate.opsForZSet().add(key,value,score);
    }

    public boolean isMember(String key ,String member){
        Long index = redisTemplate.opsForZSet().rank(key,member);
        return ObjectUtils.isEmpty(index)?false:true;
    }
    public Set<String> members(String key ){
        return redisTemplate.opsForZSet().range(key,0,this.size(key));
    }

    public void remove(String key, String member) {
        redisTemplate.opsForZSet().remove(key,member);
    }


    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public int size(String key) {
        return redisTemplate.opsForZSet().size(key).intValue();
    }


}
