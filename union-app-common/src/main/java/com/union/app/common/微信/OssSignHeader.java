package com.union.app.common.微信;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;

import javax.crypto.spec.SecretKeySpec;

import java.io.*;

import java.net.HttpURLConnection;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.*;

public class OssSignHeader {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private final static String CHARSET_UTF8 = "utf8";

    private final static String ALGORITHM = "HmacSHA1";

    public static String hmacSha1(String data, String key) {

        try {

            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);

            mac.init(keySpec);

            byte[] rawHmac;

            rawHmac = mac.doFinal(data.getBytes(CHARSET_UTF8));

            return new String(Base64.encodeBase64(rawHmac));

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    public static String buildSignData(String Date,String VERB,String CanonicalizedResource){

        String signData = "PUT" + "\n"+ "\n"+ "\n"

                + Date + "\n"

                + CanonicalizedResource;

        return signData;

    }

    public static String getGMTDate(){

        Calendar cd = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String str = sdf.format(cd.getTime());

        return str;

    }

    public static void main(String[] args) throws Exception{

        String date = getGMTDate();

        String ossBucket= "您的bucket名称";

        String accessKeyId= "您的AccessKey";

        String secretAccessKey= "您的AccessSecret";

        String resourcePath = "/xx/panda/102283/111.txt";

        String resourcePath1 = "panda/102283/111.txt";

        String VERB = "PUT";

        String url = "https://oss.211shopper.com";

        String Signature = (hmacSha1(buildSignData(date,VERB,resourcePath),secretAccessKey));

        String Authorization = "OSS " + accessKeyId + ":" + Signature;

        System.out.println(Authorization);

        Map<String,String> head = new HashMap<String,String>();

        head.put("Date",date);

        head.put("Authorization",Authorization);

        URL url1 = new URL(url + resourcePath1);

        HttpURLConnection connection ;

        StringBuffer sbuffer=null;

        try {

//添加 请求内容

            connection= (HttpURLConnection) url1.openConnection();

//设置http连接属性

            connection.setDoOutput(true);// http正文内，因此需要设为true, 默认情况下是false;

            connection.setRequestMethod("PUT"); // 可以根据需要 提交 GET、POST、DELETE、PUT等http提供的功能

//connection.setUseCaches(false);//设置缓存，注意设置请求方法为post不能用缓存

// connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Date", date);  //设置请 求的服务器网址，域名，例如***.**.***.***

            connection.setRequestProperty("Authorization", Authorization);//设定 请求格式 json，也可以设定xml格式的

//connection.setRequestProperty("Content-Length", obj.toString().getBytes().length + ""); //设置文件请求的长度



            connection.setReadTimeout(10000);//设置读取超时时间

            connection.setConnectTimeout(10000);//设置连接超时时间

            connection.connect();

            OutputStream out = connection.getOutputStream();//向对象输出流写出数据，这些数据将存到内存缓冲区中

            out.write(new String("测试数据").getBytes());            //out.write(new String("测试数据").getBytes());            //刷新对象输出流，将任何字节都写入潜在的流中

            out.flush();

// 关闭流对象,此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中

            out.close();

//读取响应

            if (connection.getResponseCode()==200)            {

// 从服务器获得一个输入流

                InputStreamReader inputStream =new InputStreamReader(connection.getInputStream());//调用HttpURLConnection连接对象的getInputStream()函数, 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。

                BufferedReader reader = new BufferedReader(inputStream);



                String lines;

                sbuffer= new StringBuffer("");



                while ((lines = reader.readLine()) != null) {

                    lines = new String(lines.getBytes(), "utf-8");

                    sbuffer.append(lines);                }

                reader.close();

            }else{

                System.out.println(connection.getResponseCode());

            }

//断开连接

            connection.disconnect();

            System.out.println("OK  "+url1);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}