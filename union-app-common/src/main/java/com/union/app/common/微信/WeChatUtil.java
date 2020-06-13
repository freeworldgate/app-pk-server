package com.union.app.common.微信;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.wechat.AccessToken;
import com.union.app.domain.wechat.UserInfo;
import com.union.app.domain.wechat.WeChatUser;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.SpringApplication;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;

public class WeChatUtil
{


    public static WeChatUser login(String code){

        String WX_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

        String requestUrl = WX_URL.replace("APPID", "wx3a496d6928523d69").
                replace("SECRET", "e37b5d03733d2f437ce94ff39460cf7d").replace("JSCODE", code).
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

    public static String getAccess_token() {
        //获取access_token
//        https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential
        String WX_URL = "https://api.weixin.qq.com/cgi-bin/token?appid=APPID&secret=SECRET&grant_type=client_credential";

        String requestUrl = WX_URL.replace("APPID", "wx3a496d6928523d69").
                replace("SECRET", "e37b5d03733d2f437ce94ff39460cf7d");
        // 发起GET请求获取凭证
        RestTemplate restTemplate = new RestTemplate();

        String msg = restTemplate.getForObject(requestUrl,String.class);


        AccessToken accessToken = JSON.parseObject(msg, AccessToken.class);
        return accessToken.getAccess_token();
    }


    public static String uploadImg2Wx(String imgUrl) throws IOException {


        Result<MdlUpload> result=FileUpload.Upload(getAccess_token(),"image", OssStorage.downLoadFile(imgUrl));
        if(!ObjectUtils.isEmpty(result.getObj())){
            return result.getObj().getMedia_id();
        }
        return null;

    }



//
//    public static void main(String[] args) throws IOException
//    {
//
//        String mediaId = uploadImg2Wx("https://oss.211shopper.com/00590952-ebe1-4cfc-9d06-467de044714f/wx-1577269541719.jpg");
//
//
//        System.out.println(mediaId);
//
//    }





}
