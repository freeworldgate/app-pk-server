package com.union.app.common.redis;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RedisSortSetService implements IRedisService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public List<String> queryPage(String key,int page){
        List<String> pageList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> sorts = redisTemplate.opsForZSet().rangeWithScores(key,  page * 30,(page+1) * 30 - 1);
        if(!CollectionUtils.isEmpty(sorts)){
            for(ZSetOperations.TypedTuple<String> type:sorts){
                pageList.add(type.getValue());
            }
        }
        return pageList;
    }


    public long getIndex(String key,String member){
        Long index = redisTemplate.opsForZSet().rank(key,member);
        return ObjectUtils.isEmpty(index)?-1:index;
    }

    public boolean addEle(String key,String value,double score){

        return redisTemplate.opsForZSet().add(key,value,score);
    }

    public boolean isMember(String key ,String member){
        Long index = redisTemplate.opsForZSet().rank(key,member);
        return ObjectUtils.isEmpty(index)?false:true;
    }

    public Double getEleScore(String key,String member){
        return redisTemplate.opsForZSet().score(key,member);
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
        if(redisTemplate.hasKey(key)) {
            Long size = redisTemplate.opsForZSet().size(key);
            return ObjectUtils.isEmpty(size) ? 0 : size.intValue();
        }
        else
        {
            return 0;
        }
    }


    public String popEle(String key) {

        Set<ZSetOperations.TypedTuple<String>> sorts = redisTemplate.opsForZSet().rangeWithScores(key,  0,1);
        if(!CollectionUtils.isEmpty(sorts)){
            return CollectionUtils.lastElement(sorts).getValue();

        }
        return null;
    }
}
