package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkDynamic.*;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class DynamicService {

    @Autowired
    PkService pkService;

    @Autowired
    UserService userService;


    @Autowired
    PostService postService;

    @Autowired
    AppDaoService daoService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisMapService redisMapService;

    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    RedisStringUtil redisUtil;


    public void delKey(String key){


        redisTemplate.delete(key);}


    public int getKeyValue(String item,String key){
        Object value = redisTemplate.opsForHash().get(item,key);
        if(ObjectUtils.isEmpty(value)){
            return 0;
        }
        else {
            return (int)value;
        }
    }
    public void setKeyValue(String item,String key,long value){
        redisTemplate.opsForHash().put(item,key,value);
    }
    public int valueIncr(String item,String key){
        long value = redisTemplate.opsForHash().increment(item,key,1L);
        return new Long(value).intValue();
    }
    public int valueDecr(String item,String key){
        long value = redisTemplate.opsForHash().increment(item,key,-1L);
        if(value < 0){this.setKeyValue(item,key,0);value = 0;}
        return new Long(value).intValue();
    }








































    public int 计算今日剩余时间(String pkId) throws ParseException {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String dateStr = simpleDateFormat.format(new Date());
        Date today = null;

        today = simpleDateFormat.parse(dateStr);

        long leftTime = 24 * 60 * 60  - (System.currentTimeMillis() - today.getTime())/1000;


        return new Long(leftTime).intValue();
    }


    public boolean 是否开抢时间(String pkId) throws ParseException {

        int leftTime = this.计算今日剩余时间(pkId);
        if(!((60 * 60 > leftTime) && (leftTime > 15 * 60))){
            return false;
        }
        else
        {
            return true;
        }


    }




//    private int 已设置预审核员数量(String pkId) { return redisSortSetService.size(CacheKeyName.预设审核员列表(pkId)); }

//    public boolean 用户是否为今日审核员(String pkId, String approveUserId,Date date) {
//        User user = pkService.queryPkCreator(pkId);
//        if(StringUtils.equals(user.getUserId(),approveUserId))
//        {
//            return true;
//        }
//        return false;
//
//
//    }



    public void 设置PK群组二维码MediaId(String pkId, String mediaId) {
        redisMapService.setStringValue(CacheKeyName.群组二维码,pkId,mediaId);
    }
//    public void 设置内置公开PK群组二维码MediaId(String pkId, String mediaId) {
//        redisMapService.setStringValue(CacheKeyName.内置公开PK群组二维码(),pkId,mediaId);
//    }
    public String 查询PK群组二维码MediaId(String pkId) {
        return redisMapService.getStringValue(CacheKeyName.群组二维码,pkId);
    }
    public void 设置PK群组二维码Url(String pkId, String url) {
        redisMapService.setStringValue(CacheKeyName.群组URL,pkId,url);
    }
//    public void 设置内置公开PK群组二维码Url(String pkId, String url) {
//        redisMapService.setStringValue(CacheKeyName.内置公开PK群组URL(),pkId,url);
//    }
    public String 查询PK群组二维码Url(String pkId) {
        return redisMapService.getStringValue(CacheKeyName.群组URL,pkId);
    }

//    public String 查询内置公开PK群组二维码Url(String pkId) {
////        return redisMapService.getStringValue(CacheKeyName.内置公开PK群组URL(),pkId);
////    }
    public String 获取当前拉取图片(String fromUserName) {

        return redisMapService.getStringValue(CacheKeyName.拉取资源图片,fromUserName);

    }

    public void 当前拉取图片(String fromUserName, String mediaId) {

        redisMapService.setStringValue(CacheKeyName.拉取资源图片,fromUserName,mediaId);

    }

//    public String 查询今日审核员数量(String pkId) {
//
//        int size = redisMapService.size(CacheKeyName.所有审核人(pkId));
//        return String.valueOf(size);
//
//    }
//
//    public String 查询今日打榜用户数量(String pkId) {
//        int size = redisSortSetService.size(CacheKeyName.打榜排名列表(pkId));
//        return String.valueOf(size);
//
//
//    }



//    //
//    public long 查询群组分配的人数(String groupId) { return redisMapService.getIntValue(CacheKeyName.群组分配人数(),groupId); }
//    public long 群组分配的人数加一(String groupId) { return redisMapService.valueIncr(CacheKeyName.群组分配人数(),groupId); }
//
//
//    public long 查询收款码分配的人数(String feeCodeId) { return redisMapService.getIntValue(CacheKeyName.收款码分配人数(),feeCodeId); }
//    public long 收款码分配的人数加一(String feeCodeId) { return redisMapService.valueIncr(CacheKeyName.收款码分配人数(),feeCodeId); }
//










//    public long 查看内置相册已审核榜帖(String pkId) {
//        return redisMapService.getIntValue(CacheKeyName.内置相册已审核(),pkId);
//
//
//    }
//
//    public long 查看内置相册审核中榜帖(String pkId) {
//
//
//
//        return redisMapService.getIntValue(CacheKeyName.内置相册审核中(),pkId);
//    }

//    public boolean 查看内置相册群组状态(String pkId) {
//
//
//        return redisMapService.getIntValue(CacheKeyName.内置相册群组状态(),pkId) == 0;
//
//    }

//    public void 更新内置相册已审核数量(String pkId, int value) {
//        redisMapService.setLongValue(CacheKeyName.内置相册已审核(),pkId,Long.valueOf(value+""));
//    }
//
//    public void 更新内置相册审核中数量(String pkId, int value) {
//        redisMapService.setLongValue(CacheKeyName.内置相册审核中(),pkId,Long.valueOf(value+""));
//
//    }
//
//    public void 更新内置相册群组状态(String pkId) {
//        if(redisMapService.getIntValue(CacheKeyName.内置相册群组状态(),pkId) == 0)
//        {
//            redisMapService.setLongValue(CacheKeyName.内置相册群组状态(),pkId,1L);
//        }
//        else
//        {
//            redisMapService.setLongValue(CacheKeyName.内置相册群组状态(),pkId,0L);
//        }
//
//    }

//    public String 查询内置公开PK群组二维码MediaId(String pkId) {
//        return redisMapService.getStringValue(CacheKeyName.内置公开PK群组二维码(),pkId);
//    }

//    public void 更新内置相册参数(String pkId) {
//        if(RandomUtil.getRandomNumber()%2 == 0){
//            redisMapService.valueIncr(CacheKeyName.内置相册审核中(),pkId,1L);
//        }
//        if(RandomUtil.getRandomNumber()%2 == 0){
//            redisMapService.valueIncr(CacheKeyName.内置相册已审核(),pkId,1L);
//        }
//
//
//
//    }
//
//    public void 扫描次数加1(String pkId, String postId) {
//         redisMapService.valueIncr(CacheKeyName.PK扫码次数(pkId),postId);
//
//
//
//    }
//
//    public long 查询扫码次数(String pkId, String postId) {
//
//        return redisMapService.getIntValue(CacheKeyName.PK扫码次数(pkId),postId);
//
//    }

//    public long 查询收款码确认次数(String feeCodeId) { return redisMapService.getIntValue(CacheKeyName.收款码确认次数(),feeCodeId); }
//    public long 收款码确认次数加一(String feeCodeId) { return redisMapService.valueIncr(CacheKeyName.收款码确认次数(),feeCodeId); }
















//    public String 查询PK公告消息Id(String pkId) {
//        return redisMapService.getStringValue(CacheKeyName.审核消息ID(),pkId);
//
//    }
//    public void 设置PK公告消息Id(String pkId, String messageId) {
//        redisMapService.setStringValue(CacheKeyName.审核消息ID(),pkId,messageId);
//    }

//    public String 获取需要更新公告的PKId() {
//        return redisTemplate.opsForSet().pop(CacheKeyName.更新公告的PKId列表());
//
//    }
//
//    public void 添加需要更新MediaId的PKId(String pkId) {
//        redisTemplate.opsForSet().add(CacheKeyName.更新公告的PKId列表(),pkId);
//    }


}




