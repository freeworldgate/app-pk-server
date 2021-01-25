package com.union.app.service.pk.service;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.storgae.KeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class KeyService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisMapService redisMapService;


    public int queryKey(String key, KeyType keyType)
    {
        long value = redisMapService.getIntValue(keyType.getName(),key);
        return new Long(value).intValue();
    }

    private void 值加一(String key,KeyType keyType)
    {
        redisMapService.valueIncr(keyType.getName(),key);

    }
    private void 值减一(String key,KeyType keyType)
    {
        redisMapService.valueDecr(keyType.getName(),key);
    }



    public void 群组成员加一(int groupId) {
        值加一(String.valueOf(groupId),KeyType.群组成员);
    }

    public void 用户粉丝加一(String userId) {
        值加一(userId,KeyType.用户粉丝);
    }

    public void 用户粉丝减一(String userId) {
        值减一(userId,KeyType.用户粉丝);
    }

    public void 想认识我的人加一(String userId) {
        值加一(userId,KeyType.想认识我的人);
    }

    public void 想认识我的人减一(String userId) {
        值减一(userId,KeyType.想认识我的人);
    }

    public void 卡点打卡人数加一(String pkId) {

        值加一(pkId,KeyType.卡点人数);
    }




    public String 获取同步PK(KeyType listkey)
    {
        return redisTemplate.opsForList().rightPop(listkey.getName());
    }
    public void 同步Pk人数(KeyType listkey,String pkId)
    {
        //上一次PK打卡人数同步时间
        long lastUpdateTime = redisMapService.getIntValue(KeyType.PK同步时间Map.getName(),pkId);
        //如果时间间隔大于同步时间
        if(System.currentTimeMillis()-lastUpdateTime > AppConfigService.getConfigAsLong(ConfigItem.PK同步时间间隔)*1000)
        {
            redisTemplate.opsForList().leftPush(listkey.getName(),pkId);
            redisMapService.setLongValue(KeyType.PK同步时间Map.getName(),pkId,System.currentTimeMillis());
        }
    }


    public double 获取偏移量(int value) {
        Object key = redisTemplate.opsForValue().get("offset"+value);
        return Double.valueOf(key.toString());

    }

    public int 获取偏缩放等级(int value) {
        Object key = redisTemplate.opsForValue().get("scale"+value);

        return (int)key;
    }
}
