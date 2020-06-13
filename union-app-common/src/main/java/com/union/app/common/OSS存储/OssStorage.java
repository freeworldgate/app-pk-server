package com.union.app.common.OSS存储;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.*;
import com.union.app.common.api.runtime.ApiRuntimePro;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.id.KeyGetter;
import com.union.app.plateform.constant.常量值;
import com.union.app.util.file.FileUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@DependsOn(value = "springContextUtil")
public class OssStorage {

//    private static String endpoint = "oss-accelerate.aliyuncs.com";
    private static String endpoint = AppConfigService.getConfigAsString(常量值.OSS基础地址,"https://oss.211shopper.com");
//    private static String accessKeyId = "LTAI4FqqnPNfxT3TpYdXqq9N";
//    private static String accessKeySecret = "mB8LeZ70pRJiXa1CCQisVECf6R0XXY";
    private static String accessKeyId = "LTAIzxViWZTSLgfe";
    private static String accessKeySecret = "MPyvdgboQZhezjdhPCXPr7lUoaAfpe";
    private static String bucketName = AppConfigService.getConfigAsString(常量值.OSSBuket名称,"211shop");
    private static OSS  ossClient = new OSSClientBuilder().build("oss-accelerate.aliyuncs.com", accessKeyId, accessKeySecret);

    private static Map<String,ConcurrentHashMap<String,AtomicInteger>> lruCache = new HashMap<String,ConcurrentHashMap<String,AtomicInteger>>();

    @Autowired
    ApiRuntimePro apiRuntimePro;


    @Autowired
    CacheStorage cacheStorage;



    private static final Semaphore semaphore = new Semaphore(300,true);

    public static File downLoadFile(String key) throws IOException {

        key = key.replace(endpoint + "/","");
        File downLoadFile = new File(AppConfigService.getConfigAsString(常量值.本地文件存储地址,System.getProperty("user.dir")) + "/" + key);
        FileUtils.forceMkdirParent(downLoadFile);
        OSSObject object = ossClient.getObject(bucketName,key);
        // 获取ObjectMeta
        ObjectMetadata meta = object.getObjectMetadata();

        // 获取Object的输入流
        InputStream objectContent = object.getObjectContent();

        ObjectMetadata objectData = ossClient.getObject(new GetObjectRequest(bucketName, key), downLoadFile);
        objectContent.close();

        return downLoadFile;

    }





















    public <T> boolean set(String key,T t) throws IOException {

                String threadName = Thread.currentThread().getName();

                PutObjectRequest request = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(JSON.toJSONString(t).getBytes("UTF-8")));
                String id = String.valueOf(Thread.currentThread().getId());
                try {
                        long start = System.currentTimeMillis();
                        semaphore.acquire(1);
                        PutObjectResult putObjectResult = ossClient.putObject(request);

                        if(this.isKeyExist(key)){
                            long end = System.currentTimeMillis();
                            System.out.println("Thread-" + Thread.currentThread().getName() + " - Cost:" + (end-start)/1000.0D   + "------------------剩余信号量 " + semaphore.availablePermits());
                            return true;
                        }
                        else
                        {
                            System.out.println("-----------------------------------fail-notExist---------------------------------------");
                            return false;
                        }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-----------------------------------fail-httpExcetion---------------------------------------");
                    return false;
                }finally {
                    semaphore.release(1);
                }

    }

    public <T> T get(String key,Class<T> tClass) throws IOException {

        OSSObject result = ossClient.getObject(bucketName,key);
        ResponseMessage responseMessage = result.getResponse();
        int code = responseMessage.getHttpResponse().getStatusLine().getStatusCode();
        if(code == 200){
            HttpEntity httpEntity = responseMessage.getHttpResponse().getEntity();
            String content = EntityUtils.toString(httpEntity,"UTF-8");
            T t = JSON.parseObject(content,tClass);
            result.close();
            return t;
        }
        else
        {
            if(!ObjectUtils.isEmpty(result)){result.close();}
            return null;
        }

    }

    public <T> List<T> getList(String key, Class<T> tClass) throws IOException {
        if(!this.isKeyExist(key)){return null;}
        OSSObject result = ossClient.getObject(bucketName,key);

        ResponseMessage responseMessage = result.getResponse();
        int code = responseMessage.getHttpResponse().getStatusLine().getStatusCode();
        if(code == 200){
            HttpEntity httpEntity = responseMessage.getHttpResponse().getEntity();
            String content = EntityUtils.toString(httpEntity,"UTF-8");
            List<T> list = JSONArray.parseArray(content,tClass);
            result.close();
            return list;
        }
        else
        {
            return null;
        }
    }

    private static final Semaphore existSemaphore = new Semaphore(1000,true);

    public boolean isKeyExist(String userPkPostKey) {
        boolean isExist = false;
        long start = System.currentTimeMillis();
        try {
            existSemaphore.acquire(1);
            isExist = ossClient.doesObjectExist(bucketName,userPkPostKey);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            existSemaphore.release(1);
        }
        long end = System.currentTimeMillis();
        System.out.println("Thread-isKeyExist-" + Thread.currentThread().getName() + " - Cost:" + (end-start)/1000.0D   + "------------------剩余信号量 " + existSemaphore.availablePermits());

        return isExist;


    }

    public <T> void setList(String key, List<T> list) throws IOException {
        byte[] bytes = JSONArray.toJSONString(list).getBytes("UTF-8");
        PutObjectRequest request = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(bytes));
        ossClient.putObject(request);
    }

    public boolean delete(String key) {
        try
        {
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
            request.setKey(key);
            ossClient.deleteObjects(request);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private <T> Map<String,T> getMap(String key,Class<T> tClass) throws IOException {
        List<String> keys = new ArrayList<String>();
        Map<String,T> results = new HashMap<String,T>();

        int maxKeys = 100;
        String nextMarker = null;
        ObjectListing objectListing;

        do {

            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withMarker(nextMarker).withMaxKeys(maxKeys);
            listObjectsRequest.setPrefix(key.concat("/"));
            objectListing = ossClient.listObjects(listObjectsRequest);

            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                keys.add(s.getKey());
            }

            nextMarker = objectListing.getNextMarker();

        } while (objectListing.isTruncated());


        for(String singleKey:keys){
            T t = this.get(singleKey,tClass);
            results.put(singleKey.replace(key.concat("/"),"").trim(),t);
        }
        return results;
    }

    private void deleteMapKey(String key, String mapKey) {
        cacheStorage.deleteMapKey(key,mapKey);
    }

    public <T> Map<String,T> getOssMap(SceneType sceneType,String key,Class<T> tClass) throws IOException {
        String dir = "MAP-KEY-".concat(key).concat("-").concat(sceneType.getCode());
        return this.getMap(dir,tClass);
    }

    //with Cache
    public boolean deleteOssWidthMapCache(SceneType sceneType, String key, String mapKey) {

        String dir = "MAP-KEY-".concat(key).concat("-").concat(sceneType.getCode());
        String singleMapKey = mapKey;
        String ossKey = dir.concat("/").concat(singleMapKey);
        this.deleteMapKey(dir,singleMapKey);
        this.delete(ossKey);
        return true;
    }

    public <T> T getOssWidthMapCache(SceneType sceneType, String key, String mapKey, Class<T> tClass) throws IOException {

        String dir = "MAP-KEY-".concat(key).concat("-").concat(sceneType.getCode());
        String ossKey = dir.concat("/").concat(mapKey);
        T t = cacheStorage.getMapKey(dir,mapKey,tClass);
        if(org.springframework.util.ObjectUtils.isEmpty(t)){
            t = this.get(ossKey,tClass);
            if(cacheStorage.mapSize(dir) < AppConfigService.getConfigAsInteger(sceneType.getCode() + "-Max-Cache-Size",100)) {
                cacheStorage.setMapKey(dir, mapKey, t);
            }
        }
        return t;
    }

    public <T> void setOssWidthMapCache(SceneType sceneType,String key, String mapKey,T t) throws IOException {

        String dir = "MAP-KEY-".concat(key).concat("-").concat(sceneType.getCode());
        String singleMapKey = mapKey;
        String ossKey = dir.concat("/").concat(singleMapKey);


        this.set(ossKey,t);



        if(cacheStorage.hasMapKey(dir,singleMapKey)){

            if(cacheStorage.mapSize(dir) < AppConfigService.getConfigAsInteger(sceneType.getCode()  + "-Max-Cache-Size",100)) {
                cacheStorage.setMapKey(dir, mapKey, t);
            }
        }

    }


    public String getCurrentTopId(String pkId,String postId){


        return null;

    }


    public String getCurrentTopId(String prefix) {

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withMaxKeys(1);
        listObjectsRequest.setPrefix(prefix);
        ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();

        if(CollectionUtils.isEmpty(sums)){
            return null;
        }
        return sums.get(0).getKey();
    }





}









