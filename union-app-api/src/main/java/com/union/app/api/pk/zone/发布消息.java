package com.union.app.api.pk.zone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/pk")
public class 发布消息 {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * redis生产者测试
     * @param data
     * @return
     */
    @GetMapping("/send1")
    String send1(String data) {
        redisTemplate.convertAndSend("Message1", data);
        return "success";
    }
    /**
     * redis生产者测试
     * @param data
     * @return
     */
    @GetMapping("/send2")
    String send2(String data) {
        redisTemplate.convertAndSend("Message2", data);
        return "success";
    }

}

