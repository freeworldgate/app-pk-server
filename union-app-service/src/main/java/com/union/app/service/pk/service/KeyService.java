package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.PostImage;
import com.union.app.entity.pk.卡点.标签.OffSetEntity;
import com.union.app.entity.pk.卡点.标签.ScaleEntity;
import com.union.app.entity.user.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.storgae.KeyType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class KeyService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisMapService redisMapService;


    @Autowired
    AppDaoService daoService;

    public long queryKey(String key, KeyType keyType)
    {
        long value = redisMapService.getlongValue(keyType.getName(),key);
        return value;
    }

    private void 值加一(String key,KeyType keyType)
    {
        redisMapService.valueIncr(keyType.getName(),key);

    }
    private void 值减一(String key,KeyType keyType)
    {
        redisMapService.valueDecr(keyType.getName(),key);
    }



    public void 群组成员加一(int groupId) {
        值加一(String.valueOf(groupId),KeyType.群组成员);
    }

    public void 用户粉丝加一(String userId) {
        值加一(userId,KeyType.用户粉丝);
    }

    public void 用户粉丝减一(String userId) {
        值减一(userId,KeyType.用户粉丝);
    }

    public void 想认识我的人加一(String userId) {
        值加一(userId,KeyType.想认识我的人);
    }

    public void 想认识我的人减一(String userId) {
        值减一(userId,KeyType.想认识我的人);
    }

    public void 卡点打卡人数加一(String pkId) {

        值加一(pkId,KeyType.卡点人数);
    }




    public String 获取同步PK(KeyType listkey)
    {
        return redisTemplate.opsForList().rightPop(listkey.getName());
    }
    public void 同步Pk人数(KeyType listkey,String pkId)
    {
        //上一次PK打卡人数同步时间
        long lastUpdateTime = redisMapService.getlongValue(KeyType.PK同步时间Map.getName(),pkId);
        //如果时间间隔大于同步时间
        if(System.currentTimeMillis()-lastUpdateTime > AppConfigService.getConfigAsLong(ConfigItem.PK同步时间间隔)*1000)
        {
            redisTemplate.opsForList().leftPush(listkey.getName(),pkId);
            redisMapService.setLongValue(KeyType.PK同步时间Map.getName(),pkId,System.currentTimeMillis());
        }
    }


    public double 获取偏移量(int value) {
        Object key = redisTemplate.opsForValue().get("offset"+value);
        if(ObjectUtils.isEmpty(key)){
            OffSetEntity offSetEntity = 查询偏移表(value);
            if(ObjectUtils.isEmpty(offSetEntity))
            {
                return 0.0D;
            }
            redisTemplate.opsForValue().set("offset"+value,offSetEntity.getOffset()+"");
            key = offSetEntity.getOffset();
        }
        return Double.valueOf(key.toString());

    }



    public int 获取缩放等级(int value) {
        Object key = redisTemplate.opsForValue().get("scale"+value);
        if(ObjectUtils.isEmpty(key)){
            ScaleEntity scaleOffsetEntity = 查询缩放表(value);
            if(ObjectUtils.isEmpty(scaleOffsetEntity))
            {
                return 16;
            }
            redisTemplate.opsForValue().set("scale"+value,scaleOffsetEntity.getScale()+"");
            key = scaleOffsetEntity.getScale();
        }
        return (int)key;
    }

    private ScaleEntity 查询缩放表(int value) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(ScaleEntity.class)
                .compareFilter("typeRange", CompareTag.Equal,value);
        ScaleEntity scaleOffsetEntity = daoService.querySingleEntity(ScaleEntity.class,cfilter);
        return scaleOffsetEntity;

    }
    private OffSetEntity 查询偏移表(int value) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(OffSetEntity.class)
                .compareFilter("scale", CompareTag.Equal,value);
        OffSetEntity offSetEntity = daoService.querySingleEntity(OffSetEntity.class,cfilter);
        return offSetEntity;

    }

    public Collection<PostImage> 查询榜帖图片(String pkId, String postId) {
        Collection<PostImage> postImageList = new ArrayList<>();
        String postImgs = redisMapService.getStringValue(KeyType.打卡图片.getName()+pkId,postId);
        if(StringUtils.isBlank(postImgs)){return postImageList;}
        List<PostImage> images = JSON.parseArray(postImgs,PostImage.class);
        postImageList.addAll(images);
        return postImageList;


    }

    public void 保存图片(String pkId, String postId, List<PostImage> postImages) {
        redisMapService.setStringValue(KeyType.打卡图片.getName()+pkId,postId,JSON.toJSONString(postImages));
    }

    public UserEntity queryUserEntity(String userId) {
        String userStr = redisMapService.getStringValue(KeyType.用户缓存.getName(),userId);
        if(StringUtils.isBlank(userStr)){return null;}
        UserEntity userEntity = JSON.parseObject(userStr,UserEntity.class);
        return userEntity;
    }

    public void saveUser(UserEntity userEntity) {
        String value = JSON.toJSONString(userEntity);
        redisMapService.setStringValue(KeyType.用户缓存.getName(),userEntity.getUserId(),value);
    }
}
