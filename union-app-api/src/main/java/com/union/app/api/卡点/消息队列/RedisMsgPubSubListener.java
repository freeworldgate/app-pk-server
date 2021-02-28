package com.union.app.api.卡点.消息队列;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.union.app.domain.pk.comment.PubType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * redis发布订阅消息监听器
 * @ClassName: RedisMsgPubSubListener
 * @Description: TODO
 * @author OnlyMate
 * @Date 2018年8月22日 上午10:05:35
 *
 */
@Component
public class RedisMsgPubSubListener implements MessageListener {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void onMessage(Message message, byte[] pattern) {
        String msgStr = message.toString();
        JSONObject jsonObject = JSON.parseObject(msgStr);
        String id = jsonObject.getString("id");
        int type = jsonObject.getIntValue("type");
//        if(type == PubType.POSTCOMMENT.getScene())
//        {
//            processComment(id);
//        }
//        else if(type == PubType.RESTORE.getScene())
//        {
//            processRestore(id);
//        }

    }

    private void processRestore(String id) {




    }

    private void processComment(String id) {
        
        
        
        
    }
    
    
    
    
    
    
}