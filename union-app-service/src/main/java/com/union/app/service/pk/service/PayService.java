package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.daka.CreateLocation;
import com.union.app.domain.pk.支付.Pay;
import com.union.app.domain.pk.支付.PayOrder;
import com.union.app.entity.pk.PkTipEntity;
import com.union.app.entity.pk.支付.PayEntity;
import com.union.app.entity.pk.支付.PayOrderEntity;
import com.union.app.entity.pk.支付.PayType;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

@Service
public class PayService {

    @Autowired
    UserService userService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    AppService appService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    RestTemplate restTemplate;


    public Object all支付方案(String userId,HttpServletRequest request) {
        return null;
    }

    public Object single支付方案(String userId,HttpServletRequest request) {
        return null;
    }


    public PayOrderEntity getPayInfo(String userId,PayOrderEntity payOrderEntity) throws Exception {


            JSONObject json = new JSONObject();

            //生成的随机字符串
            String nonce_str = Util.getRandomStringByLength(32);
            //商品名称
            String description = PayType.valueType(payOrderEntity.getType()).getStatuStr();

            //获取本机的ip地址
            String spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
            String orderNo = payOrderEntity.getOrderId();
            String money = String.valueOf(payOrderEntity.getPayValue());//支付金额，单位：分，这边需要转成字符串类型，否则后面的签名会失败

            Map<String, Object> packageParams = new HashMap<String, Object>();
            packageParams.put("appid", WXConst.appId);
            packageParams.put("mch_id", WXConst.mch_id);
            packageParams.put("description", description);
            packageParams.put("out_trade_no", orderNo);//商户订单号
//            packageParams.put("time_expire", TimeUtils.订单失效时间());//商户订单号
            packageParams.put("notify_url", WXConst.notify_url);
            JSONObject amount = new JSONObject();
            amount.put("total",money);
            amount.put("currency","CNY");
            packageParams.put("amount", amount);
            JSONObject payer = new JSONObject();
            amount.put("openid",payOrderEntity.getUserId());
            packageParams.put("payer", payer);



            // 除去数组中的空值和签名参数
            packageParams = this.paraFilter(packageParams);
            String prestr = this.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串


            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = this.sign(prestr, WXConst.key, "utf-8").toUpperCase();


            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml version='1.0' encoding='gbk'>" + "<appid>" + WXConst.appId + "</appid>"
                    + "<body><![CDATA[" + description + "]]></body>"
                    + "<mch_id>" + WXConst.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WXConst.notify_url + "</notify_url>"
                    + "<openid>" + userId + "</openid>"
                    + "<out_trade_no>" + orderNo + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + money + "</total_fee>"
                    + "<trade_type>" + WXConst.TRADETYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";


//            System.out.println("调试模式_统一下单接口 请求XML数据：" + xml);


            //调用统一下单接口，并接受返回的结果
            String result = this.httpRequest(WXConst.pay_url, "POST", xml);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(WXConst.pay_url,xml,String.class);
            if(responseEntity.getStatusCodeValue() == HttpURLConnection.HTTP_OK){
                String resStr = responseEntity.getBody();
                JSONObject jsonObject = JSONObject.parseObject(resStr);
                String prepay_id = (String) jsonObject.get("prepay_id");
                Long timeStamp = System.currentTimeMillis() / 1000;
                String stringSignTemp = "appId=" + WXConst.appId + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=" + WXConst.SIGNTYPE + "&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = this.sign(stringSignTemp, WXConst.key, "utf-8").toUpperCase();

                payOrderEntity.setNonceStr(nonce_str);
                payOrderEntity.setPackageStr("prepay_id=" + prepay_id);
                payOrderEntity.setTimeStamp(String.valueOf(timeStamp));
                payOrderEntity.setPaySign(paySign);
                payOrderEntity.setSignType(WXConst.SIGNTYPE);
                payOrderEntity.setAppId(WXConst.appId);
                daoService.insertEntity(payOrderEntity);
                return payOrderEntity;

            }


//            System.out.println("调试模式_统一下单接口 返回XML数据：" + result);


            // 将解析结果存储在HashMap中
//            Map map = this.doXMLParse(result);


//            String return_code = (String) map.get("return_code");//返回状态码


            //返回给移动端需要的参数

//            if(return_code == "SUCCESS" || return_code.equals(return_code)){
//                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
//                Long timeStamp = System.currentTimeMillis() / 1000;
//                String stringSignTemp = "appId=" + WXConst.appId + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=" + WXConst.SIGNTYPE + "&timeStamp=" + timeStamp;
//                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
//                String paySign = this.sign(stringSignTemp, WXConst.key, "utf-8").toUpperCase();
//
//                payOrderEntity.setNonceStr(nonce_str);
//                payOrderEntity.setPackageStr("prepay_id=" + prepay_id);
//                payOrderEntity.setTimeStamp(String.valueOf(timeStamp));
//                payOrderEntity.setPaySign(paySign);
//                payOrderEntity.setSignType("MD5");
//                payOrderEntity.setAppId(WXConst.appId);
//
//                daoService.insertEntity(payOrderEntity);
//
//                return payOrderEntity;
//            }
            throw AppException.buildException(PageAction.信息反馈框("内部错误","内部错误"));

    }




    public String sign(String text, String key, String input_charset) {
        text = text + "&key=" + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    public boolean verify(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    public byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
    /**
     * 生成6位或10位随机数 param codeLength(多少位)
     * @return
     */
    public String createCode(int codeLength) {
        String code = "";
        for (int i = 0; i < codeLength; i++) {
            code += (int) (Math.random() * 9);
        }
        return code;
    }
    @SuppressWarnings("unused")
    private boolean isValidChar(char ch) {
        if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
            return true;
        if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
            return true;// 简体中文汉字编码
        return false;
    }
    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public Map<String, Object> paraFilter(Map<String, Object> sArray) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key).toString();
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public String createLinkString(Map<String, Object> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key).toString();
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    public String httpRequest(String requestUrl,String requestMethod,String outputStr){
        // 创建SSLContext
        StringBuffer buffer = null;
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //往服务器端写内容
            if(null !=outputStr){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public String urlEncodeUTF8(String source){
        String result=source;
        try {
            result=java.net.URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public Map doXMLParse(String strxml) throws Exception {
        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map m = new HashMap();
        InputStream in = String2Inputstream(strxml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

//关闭流
        in.close();

        return m;
    }
    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    public String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }
    public InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }





    public void wxNotify(HttpServletRequest request,HttpServletResponse response) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        //sb为微信返回的xml
        String notityXml = sb.toString();
        String resXml = "";
        System.out.println("接收到的报文：" + notityXml);


        Map map = this.doXMLParse(notityXml);


        String returnCode = (String) map.get("return_code");
        if("SUCCESS".equals(returnCode)){
            //验证签名是否正确
            if(this.verify(this.createLinkString(map), (String)map.get("sign"), WXConst.key, "utf-8")){
                /**此处添加自己的业务逻辑代码start**/




                /**此处添加自己的业务逻辑代码end**/


                //通知微信服务器已经支付成功
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            }
        }else{
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        System.out.println(resXml);
        System.out.println("微信支付回调数据结束");


        BufferedOutputStream out = new BufferedOutputStream(
                response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }


    public void 充值时间(int day, String userId) {
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
        Map<String,Object> map = new HashMap<>();
        map.put("findTimeLength",userDynamicEntity.getFindTimeLength() + day*24*3600*1000);
        daoService.updateColumById(userDynamicEntity.getClass(),"userId",userDynamicEntity.getUserId(),map);

    }

    public void 用户创建卡点(CreateLocation createLocation) throws AppException {
        if(!AppConfigService.getConfigAsBoolean(ConfigItem.卡点创建收费)){
            return;
        }


        if(appService.是否是免费城市(createLocation.getCity()))
        {
            return;
        }

        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(createLocation.getUserId());
        int pk = userDynamicEntity.getPk();
        if(pk < 1)
        {
            throw AppException.buildException(PageAction.执行处理器("pay",""));
        }
        else
        {

            Map<String,Object> map = new HashMap<>();
            map.put("pk",pk-1);
            daoService.updateColumById(userDynamicEntity.getClass(),"userId",userDynamicEntity.getUserId(),map);

        }


    }

    public void 充值Pk(int pkTimes, String userId) {
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
        int pk = userDynamicEntity.getPk();

        Map<String,Object> map = new HashMap<>();
        map.put("pk",pk+pkTimes);
        daoService.updateColumById(userDynamicEntity.getClass(),"userId",userDynamicEntity.getUserId(),map);

    }

    public Pay 查询充值选项(int type) throws AppException {
        Pay pay = new Pay();
        PayType payType = PayType.valueType(type);
        if(ObjectUtils.isEmpty(payType)){throw  AppException.buildException(PageAction.信息反馈框("内部错误","内部错误"));}
        List<PayEntity>  pays = 查询充值选项Entity(payType);
        if(CollectionUtils.isEmpty(pays)){throw  AppException.buildException(PageAction.信息反馈框("内部错误","内部错误"));}

        pay.setPayType(payType.getStatu());
        pay.setTitle(payType.getStatuStr());
        pay.setTips(appService.查询温馨提示(payType.getStatu()));
        pay.setPayItems(pays);
        pay.setSelectPay(pays.get(0));
        return pay;
    }

    private List<PayEntity> 查询充值选项Entity(PayType payType) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PayEntity.class)
                .compareFilter("payType", CompareTag.Equal,payType.getStatu())
                .orderByFilter("pay", OrderTag.ASC);
        List<PayEntity> payEntities = daoService.queryEntities(PayEntity.class,cfilter);
        payEntities.forEach(payEntity -> {

            payEntity.setImg(appService.查询背景(10));
            payEntity.setSelectImg(appService.查询背景(10));

        });
        return payEntities;
    }

    public PayOrderEntity 创建订单(String userId, int type, String payId) throws AppException {

        PayType payType = PayType.valueType(type);
        if(ObjectUtils.isEmpty(payType)){throw  AppException.buildException(PageAction.信息反馈框("内部错误","内部错误"));}
        PayEntity payEntity = 查询充值选项EntityById(payType,payId);
        if(ObjectUtils.isEmpty(payEntity)){throw  AppException.buildException(PageAction.信息反馈框("内部错误","内部错误"));}
        String orderId = this.获取OrderId();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setUserId(userId);
        payOrderEntity.setType(payEntity.getPayType());
        payOrderEntity.setPayValue(payEntity.getPay());
        payOrderEntity.setValue(payEntity.getValue());
        payOrderEntity.setTime(System.currentTimeMillis());
        payOrderEntity.setTitle(payType.getStatuStr());


        return payOrderEntity;

    }


    private String 获取OrderId() {
        return  UUID.randomUUID().toString();
    }

    private PayEntity 查询充值选项EntityById(PayType payType,String payId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PayEntity.class)
                .compareFilter("payType", CompareTag.Equal,payType.getStatu())
                .andFilter()
                .compareFilter("payId", CompareTag.Equal,payId);
        PayEntity payEntity = daoService.querySingleEntity(PayEntity.class,cfilter);

        return payEntity;
    }

    public boolean 订单是否成功(String orderId) {
        String queryOrder = WXConst.query_url+orderId+"?mchid="+WXConst.mch_id;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(queryOrder,String.class);
        if(responseEntity.getStatusCodeValue() == HttpURLConnection.HTTP_OK )
        {
            String resStr = responseEntity.getBody();
            JSONObject jsonObject = JSONObject.parseObject(resStr);
            if(StringUtils.equalsIgnoreCase("SUCCESS",(String)jsonObject.get("trade_state")))
            {
                return true;
            }
        }
        return false;
    }

    public void 处理成功订单(String orderId) throws AppException {
        PayOrderEntity payOrderEntity = 查询OrderEntity(orderId);
        if(ObjectUtils.isEmpty(payOrderEntity)){throw  AppException.buildException(PageAction.信息反馈框("订单不存在","订单不存在"));}
        UserDynamicEntity userDynamicEntity = userDynamicService.queryUserDynamicEntity(payOrderEntity.getUserId());
        PayType payType = PayType.valueType(payOrderEntity.getType());


        Map<String,Object> map = new HashMap<>();
        if(payType == PayType.卡点)
        {
            map.put("pk",userDynamicEntity.getPk() + payOrderEntity.getValue());
        }
        else if(payType == PayType.时间)
        {
            map.put("findTimeLength",userDynamicEntity.getFindTimeLength() + payOrderEntity.getValue() * 24 * 3600 * 1000);
        }
        else if(payType == PayType.群组)
        {
            map.put("mygroups",userDynamicEntity.getMygroups() + payOrderEntity.getValue());
        }
        else
        {
            if(ObjectUtils.isEmpty(payOrderEntity)){throw  AppException.buildException(PageAction.信息反馈框("订单不存在","订单不存在"));}
        }
        daoService.updateColumById(UserDynamicEntity.class,"userId",userDynamicEntity.getUserId(),map);
    }



    public PayOrderEntity 查询OrderEntity(String orderId)
    {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                .compareFilter("orderId", CompareTag.Equal,orderId);
        PayOrderEntity payOrderEntity = daoService.querySingleEntity(PayOrderEntity.class,cfilter);
        return payOrderEntity;

    }


}
