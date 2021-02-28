package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSONObject;
import com.union.app.domain.pk.comment.PubType;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;



    public void sendRedisMessage(String id, PubType type){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("type",type.getScene());
        redisTemplate.convertAndSend(常量值.用户动态消息队列主题, json.toJSONString());
    }
}
