package com.union.app.common.OSS存储;

import com.alibaba.fastjson.JSON;
import com.union.app.common.id.KeyGetter;
import com.union.app.domain.pk.PostDynamic;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class CacheStorage {

    public static PostDynamic get(String postId, Class<PostDynamic> postDynamicClass) {
        return null;
    }


    @Autowired
    RedisStringUtil redisStringUtil;


    public <T> T getKey(String key,Class<T> tClass){

        String value = redisStringUtil.get(key);
        T t = JSON.parseObject(value,tClass);
        return t;
    }
    public <T> boolean setKey(String key,T t){
        return redisStringUtil.set(key,JSON.toJSONString(t));
    }

    public <T> T getMapKey(String key,String mapKey,Class<T> tClass){
        T t = redisStringUtil.getMapKey(key,mapKey,tClass);
        return t;
    }

    public <T> boolean setMapKey(String key,String mapKey,T t){
        return redisStringUtil.setMapKey(key,mapKey,t);
    }

    public boolean hasMapKey(String key, String item){
        return redisStringUtil.isMapKeyExist(key,item);
    }

    public long mapSize(String key){
        return redisStringUtil.mapSize(key);
    }

    public void deleteMapKey(String key, String mapKey) {
        redisStringUtil.deleteMapKey(key,mapKey);
    }


    public String genneratePostId(String pkId) {

        String postId = redisStringUtil.pop(KeyGetter.异常的POSTID列表(pkId));

        if(StringUtils.isEmpty(postId)){
            long seq = redisStringUtil.hincrement(KeyGetter.记录PK最新POST序列MAP(),pkId,1L);
            postId = KeyGetter.拼接POSTID(pkId, (int) seq);

        }
        return postId;
    }




    public void 记录已发布帖子最大序列值(String pkId,String postId) {
        redisStringUtil.setMapKey("POST-CURRENR-SEQ",pkId,postId);
    }

    public int 查询已发布帖子最大序列值Seq(String pkId) {
        String postSeq = redisStringUtil.getMapKey("POST-CURRENR-SEQ",pkId,String.class);
        int currentPostIdSeq = StringUtils.isEmpty(postSeq)?0:Integer.valueOf(postSeq.split("-")[postSeq.split("-").length-1]);
        return currentPostIdSeq;
    }

//    public PkDynamic getPkDynamic(String pkId) {
//
//        return null;
//    }

    public Map<String,String> getMap(String key) {
        return redisStringUtil.getMap(key);
    }


    public void setTimeMapKey(String s, String s1, String l, long i) {
        redisStringUtil.setTimeMapKey(s,s1,l,i);
    }


    public void deleteKey(String s) {
        redisStringUtil.del(s);
    }
}
