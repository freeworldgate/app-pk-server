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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 接收消息 {




    /**
     * 接收微信后台发来的用户消息
     * @return
     */
    @RequestMapping(value = "/checkToken", method = RequestMethod.POST)
    @ResponseBody
    public String receiveMessage(@RequestBody Map<String, Object> msg) {

        //用户openId
        String fromUserName = msg.get("FromUserName").toString();
        String createTime = msg.get("CreateTime").toString();
        String toUserName = msg.get("ToUserName").toString();
        String msgType = msg.get("MsgType").toString();
        if (msgType.equals("text")) { //收到的是文本消息,并将消息返回给客服

            WxSendMessage wxSendMessage = new WxSendMessage();
            wxSendMessage.setTouser(fromUserName);
            wxSendMessage.setMsgtype("text");
            WxText wxText = new WxText();
            wxText.setContent(msg.get("Content").toString());
            wxSendMessage.setText(wxText);
            sendMsToCustomer(wxSendMessage);
        }
        else if (msgType.equals("image")) { //收到的是文本消息,并将消息返回给客服

            WxSendMessage wxSendMessage = new WxSendMessage();
            wxSendMessage.setTouser(fromUserName);
            wxSendMessage.setMsgtype("image");
            WxImage wxImage = new WxImage();
            wxImage.setMedia_id(msg.get("MediaId").toString());
            wxSendMessage.setImage(wxImage);
            sendMsToCustomer(wxSendMessage);
        }
        else
        {
            WxSendMessage wxSendMessage = new WxSendMessage();
            wxSendMessage.setTouser(fromUserName);
            wxSendMessage.setMsgtype("link");
            WxLink wxLink = new WxLink();
            wxLink.setTitle("标题内容");
            wxLink.setUrl("/pages/pk/pk/pk");
            wxLink.setThumb_url(RandomUtil.getRandomImage());
            wxLink.setDescription("描述信息");
            wxSendMessage.setLink(wxLink);
            sendMsToCustomer(wxSendMessage);


        }



        return "success";
    }
    /**
     * 用户发送消息给客服
     *
     */
    private void sendMsToCustomer(WxSendMessage wxSendMessage) {
        AccessToken access_token = WeChatUtil.getAccess_token();
        System.out.println("access_token:--------" + access_token);

        RestTemplate restTemplate = new RestTemplate();
//        String jsonStr = json.toJSONString();
        //access_token
        String result = restTemplate.postForEntity("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" +
                access_token.getAccess_token(),wxSendMessage, String.class).getBody();
    }




}
