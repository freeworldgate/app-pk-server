package com.union.app.common.微信;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.wechat.AccessToken;
import com.union.app.domain.wechat.UserInfo;
import com.union.app.domain.wechat.WeChatUser;
import com.union.app.entity.pk.PostEntity;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;

public class WeChatUtil
{


    private static AccessToken token;
    private static long updateTime = 0L;

    public static final String appId="wx3a496d6928523d69";
    public static final String appSecret="e37b5d03733d2f437ce94ff39460cf7d";


//    管理端

    public static final String managerAppId="wx824b5474eeb86787";
    public static final String managerAppSecret="9276267c14fffcea699f1aa90d37de26";



    public static WeChatUser login(String code){

        String WX_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

        String requestUrl = WX_URL.replace("APPID", appId).
                replace("SECRET", appSecret).replace("JSCODE", code).
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


    public static WeChatUser manageLogin(String code){

        String WX_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

        String requestUrl = WX_URL.replace("APPID", managerAppId).
                replace("SECRET", managerAppSecret).replace("JSCODE", code).
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

        //一个小时更新一次
        if(updateTime < System.currentTimeMillis() - 1 * 3600 * 1000L)
        {
            String WX_URL = "https://api.weixin.qq.com/cgi-bin/token?appid=APPID&secret=SECRET&grant_type=client_credential";
            String requestUrl = WX_URL.replace("APPID", appId).
                    replace("SECRET", appSecret);
            // 发起GET请求获取凭证
            RestTemplate restTemplate = new RestTemplate();
            String msg = restTemplate.getForObject(requestUrl,String.class);
            AccessToken accessToken = JSON.parseObject(msg, AccessToken.class);
            token = accessToken;
            updateTime = System.currentTimeMillis();
            return token.getAccess_token();

        }
        else {

            return token.getAccess_token();

        }
    }


    public static String uploadImg2Wx(String imgUrl) throws IOException {


        Result<MdlUpload> result=FileUpload.Upload(getAccess_token(),"image", OssStorage.downLoadFile(imgUrl));
        if(!ObjectUtils.isEmpty(result.getObj())){
            return result.getObj().getMedia_id();
        }
        return null;

    }

    public static String 生成二维码(String pkId) throws IOException {


        String token = getAccess_token();

        String WX_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=TOKEN";
        String requestUrl = WX_URL.replace("TOKEN",token);
        // 发起GET请求获取凭证
        RestTemplate restTemplate = new RestTemplate();


        Map<String,Object> param = new HashMap<>();
        param.put("scene", pkId);
        param.put("path", "pages/pk/timepage/timepage");
        param.put("width", 430);
        param.put("auto_color", false);
        Map<String,Object> line_color = new HashMap<>();
        line_color.put("r", 0);
        line_color.put("g", 0);
        line_color.put("b", 0);
        param.put("line_color", line_color);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        HttpEntity requestEntity = new HttpEntity(param, headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, byte[].class, new Object[0]);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String url = OssStorage.uploadFileBytes(pkId,responseEntity.getBody());
            return url;
        }
        else
        {
            return null;
        }



















    }



    private static class PostEntity {

        String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }







}
