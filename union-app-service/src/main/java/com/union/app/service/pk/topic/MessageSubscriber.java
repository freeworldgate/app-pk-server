package com.union.app.service.pk.topic;

import com.alibaba.fastjson.JSON;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.OSS存储.SceneType;
import com.union.app.domain.pk.PostMessage;
import com.union.app.plateform.storgae.SubType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
public class MessageSubscriber extends MessageListenerAdapter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    OssStorage ossStorage;

    @Override
    public void onMessage(Message message, byte[] bytes) {

        String content = new String(bytes);
        //内容
        PostMessage postMessage = JSON.parseObject(content,PostMessage.class);
        SubType subType = SubType.valueOf(postMessage.getType());







//        String key = postMessage.getPkId().concat("/").concat("Dynamic").concat("/").concat(postMessage.getPostId()).concat("/").concat(subType.getTopic())
//                .concat("/").concat("message" + );

//        ossStorage.set(key,postMessage);






        //key = PkId/Dynamic/PostId/Comment
        //                              |- CurrentPage : PostMessagePage[{},{},{}]
        //                              |- Message198  : PostMessagePage[{},{},{}]
        //                              |- Message197  : PostMessagePage[{},{},{}]
        //                              |- Message196  : PostMessagePage[{},{},{}]
        //key = PkId/Dynamic/PostId/Share
        //key = PkId/Dynamic/PostId/View
        //key = PkId/Dynamic/PostId/Follow









//      ossStorage.getOssWidthMapCache(SceneType.存储POST动态信息,"","CurrentPage",PostMessagePage.class);











    }








}
