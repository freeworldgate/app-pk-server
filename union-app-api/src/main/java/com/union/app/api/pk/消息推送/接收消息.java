package com.union.app.api.pk.消息推送;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.pk.wechat.AccessToken;
import com.union.app.domain.pk.客服消息.WxImage;
import com.union.app.domain.pk.客服消息.WxLink;
import com.union.app.domain.pk.客服消息.WxSendMessage;
import com.union.app.domain.pk.客服消息.WxText;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 接收消息 {


    @Autowired
    UserService userService;

    /**
     * 接收微信后台发来的用户消息
     * @return
     */
    @RequestMapping(value = "/checkToken", method = RequestMethod.POST)
    @ResponseBody
    public String receiveMessage(@RequestBody Map<String, Object> msg) {

        //用户openId


        userService.processMessage(msg,new Date());



        return "success";
    }
    /**
     * 用户发送消息给客服
     *
     */
    private boolean  sendMsToCustomer(WxSendMessage wxSendMessage) {



        String access_token = WeChatUtil.getAccess_token();
        System.out.println("access_token:--------" + access_token);

        RestTemplate restTemplate = new RestTemplate();
//        String jsonStr = json.toJSONString();
        //access_token
        String result = restTemplate.postForEntity("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" +
                access_token,wxSendMessage, String.class).getBody();

        return true;
    }




}
