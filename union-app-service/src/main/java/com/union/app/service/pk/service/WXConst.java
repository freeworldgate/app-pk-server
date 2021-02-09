package com.union.app.service.pk.service;

import com.union.app.common.微信.WeChatUtil;

public class WXConst {
    //微信小程序appid
    public static String appId = WeChatUtil.appId;
    //微信小程序appsecret
    public static String appSecret = WeChatUtil.appSecret;

    //微信商户号
    public static String mch_id="123";
    //微信支付的商户密钥
    public static final String key = "123";
    //获取微信Openid的请求地址
    public static String WxGetOpenIdUrl = "";
    //支付成功后的服务器回调url
    public static final String notify_url="https://www.211shopper.com/pk/pay/notify";
    //签名方式
    public static final String SIGNTYPE = "MD5";
    //交易类型
    public static final String TRADETYPE = "JSAPI";
    //微信统一下单接口地址
    public static final String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//    public static final String pay_url = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    public static final String query_url = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/";
}
