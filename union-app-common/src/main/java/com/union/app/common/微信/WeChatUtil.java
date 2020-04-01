package com.union.app.common.微信;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.union.app.domain.wechat.UserInfo;
import com.union.app.domain.wechat.WeChatUser;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;

public class WeChatUtil
{


    public static WeChatUser login(String code){

        String WX_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

        String requestUrl = WX_URL.replace("APPID", "wx3a496d6928523d69").
                replace("SECRET", "af61063e84fc1834bb55351f34fa1390").replace("JSCODE", code).
                replace("authorization_code", "authorization_code");
        // 发起GET请求获取凭证
        RestTemplate restTemplate = new RestTemplate();
        WeChatUser weChatUser = new WeChatUser();
        String msg = restTemplate.getForObject(requestUrl,String.class);
        JSONObject json = JSON.parseObject(msg);
        weChatUser.setSession_key(json.getString("session_key"));
        weChatUser.setOpenid(json.getString("openid"));

        return weChatUser;

    }


    public static UserInfo getUserInfo(String encryptedData, String session_key, String iv) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {

        byte[] resultByte  = AES.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(session_key), Base64.decodeBase64(iv));
        String result = new String(resultByte,"utf-8");

        UserInfo userInfo = JSON.parseObject(result, UserInfo.class);

        return userInfo;
    }
}
