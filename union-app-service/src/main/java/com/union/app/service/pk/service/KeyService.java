package com.union.app.service.pk.service;

import com.union.app.common.redis.RedisMapService;
import com.union.app.plateform.storgae.KeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyService {



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
}
