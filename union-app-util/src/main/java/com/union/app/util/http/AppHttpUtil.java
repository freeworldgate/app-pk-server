package com.union.app.util.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class AppHttpUtil {


    /**
     * Http连接池配置
     */
    @Autowired
    private HttpClientConfig httpClientConfig ;


    private CloseableHttpClient closeableHttpClient = HttpClients.createDefault();


    public HttpResponse httpGetService(String url, Map<String,String> params, Map<String,String> headers) throws IOException, URISyntaxException {
        URI uri = buildURI(url,params);
        HttpGet httpGet = new HttpGet(uri);
        addHeaders(httpGet,headers);
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
        String responseString = EntityUtils.toString(closeableHttpResponse.getEntity());
        HttpResponse httpResponse = new HttpResponse(statusCode,responseString);
        closeableHttpResponse.close();
        return httpResponse;
    }




    public <V> HttpResponse httpPostService(String url,Map<String,String> params,Map<String,String> headers,V postEntity) throws IOException, URISyntaxException {
        URI uri = buildURI(url,params);
        HttpPost httpPost = new HttpPost(uri);
        ObjectMapper objectMapper = new ObjectMapper();
        String postEntityString = objectMapper.writeValueAsString(postEntity);
        StringEntity stringEntity = new StringEntity(postEntityString, Charset.forName("UTF-8"));
        httpPost.setEntity(stringEntity);
        addHeaders(httpPost,headers);
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
        String resposneString = EntityUtils.toString(closeableHttpResponse.getEntity());
        HttpResponse httpResponse = new HttpResponse(statusCode,resposneString);
        closeableHttpResponse.close();
        return httpResponse;
    }

    private URI buildURI(String url, Map<String,String> params) throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(url);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(params))
        {
            for(String key:params.keySet())
            {
                NameValuePair nameValuePair = new BasicNameValuePair(key,params.get(key));
                nameValuePairList.add(nameValuePair);
            }
            uriBuilder.addParameters(nameValuePairList);
        }
        return uriBuilder.build();
    }



    private void addHeaders(HttpUriRequest httpUriRequest,Map<String,String> headers){

        if(CollectionUtils.isEmpty(headers))
        {
            headers = new HashMap<>();
        }
        for(String key:headers.keySet())
        {
            httpUriRequest.addHeader(key,headers.get(key));
        }
    }










/*



    private static SSLContext sslcontext;

    static {
        try {
            sslcontext = SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    //设置协议http和https对应的处理socket链接工厂的对象
    private static final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext))
            .build();
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

    //创建自定义的httpclient对象
//  private static final CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connManager).build();

    //繞過证书校验
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getDefault();

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, new SecureRandom());
        return sc;
    }
*/


















}
