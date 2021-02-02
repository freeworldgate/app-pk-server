package com.union.app.common.dao;

import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.PostImage;
import com.union.app.entity.pk.BackImgEntity;
import com.union.app.entity.pk.PostImageEntity;
import com.union.app.entity.pk.kadian.label.RangeEntity;
import com.union.app.entity.user.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Service
public class KeyService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisMapService redisMapService;


    @Autowired
    AppDaoService daoService;

    public String queryStringValue(String key, KeyType keyType)
    {
        String value = redisMapService.getStringValue(keyType.getName(),key);
        return value;
    }
    public void 保存配置缓存(String configName,String configValue, KeyType keyType) {
        redisMapService.setStringValue(keyType.getName(),configName,configValue);
    }
    public void 刷新配置缓存(String configName, KeyType keyType) {
        redisMapService.removeMapKey(keyType.getName(),configName);
    }

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



    public void 群组成员加一(String groupId) {
        值加一(groupId,KeyType.群组成员);
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





    //打卡图片缓存
    public Collection<PostImage> 查询榜帖图片(String pkId, String postId) {
        List<PostImage> postImageList = new ArrayList<>();
        String postImgs = redisMapService.getStringValue(KeyType.打卡图片.getName()+pkId,postId);
        if(StringUtils.isBlank(postImgs)){

                EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                        .compareFilter("pkId",CompareTag.Equal,pkId)
                        .andFilter()
                        .compareFilter("postId",CompareTag.Equal,postId);
                List<PostImageEntity> postImageEntity = daoService.queryEntities(PostImageEntity.class,filter);

                postImageEntity.forEach(img->{
                    PostImage postImage = new PostImage();
                    postImage.setImgUrl(img.getImgUrl());
                    postImage.setImageId(img.getImgId());
                    postImage.setPkId(img.getPkId());
                    postImage.setPostId(img.getPostId());
                    postImage.setTime(TimeUtils.convertTime(img.getTime()));
                    postImageList.add(postImage);
                });
                if(!CollectionUtils.isEmpty(postImageEntity))
                {
                    this.保存图片(pkId,postId,postImageList);
                }
        }
        else
        {
            List<PostImage> images = JSON.parseArray(postImgs,PostImage.class);
            postImageList.addAll(images);
        }
        return postImageList;
    }

    public void 保存图片(String pkId, String postId, List<PostImage> postImages) {
        redisMapService.setStringValue(KeyType.打卡图片.getName()+pkId,postId,JSON.toJSONString(postImages));
    }

    public void 保存顶置POST图片(String pkId, List<PostImage> postImages) {
        redisMapService.setStringValue(KeyType.顶置图片.getName(),pkId,JSON.toJSONString(postImages));
    }






    //用户缓存
    public UserEntity queryUserEntity(String userId) {
        UserEntity userEntity = null;
        String userStr = redisMapService.getStringValue(KeyType.用户缓存.getName(),userId);
        if(StringUtils.isBlank(userStr)){

                if(StringUtils.equalsIgnoreCase("undefined",userId)|| StringUtils.equalsIgnoreCase("null",userId)|| StringUtils.equalsIgnoreCase("Nan",userId))
                {
                    return userEntity;
                }
                EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                        .compareFilter("userId",CompareTag.Equal,userId);
                userEntity = daoService.querySingleEntity(UserEntity.class,filter);
                if(!ObjectUtils.isEmpty(userEntity))
                {
                    this.saveUser(userEntity);
                }
        }
        else
        {
            userEntity = JSON.parseObject(userStr,UserEntity.class);
        }
        return userEntity;
    }

    public void saveUser(UserEntity userEntity) {
        String value = JSON.toJSONString(userEntity);
        redisMapService.setStringValue(KeyType.用户缓存.getName(),userEntity.getUserId(),value);
    }

    public Post 查询顶置图片集合(String pkId) {


        Post post = new Post();
        List<PostImage> postImages = this.查询PostImgsByPk(pkId);
        post.setPostImages(postImages);
        return CollectionUtils.isEmpty(postImages)?null:post;

    }

    private List<PostImage> 查询PostImgsByPk(String pkId) {
        List<PostImage> postImages = new ArrayList<>();
        String postImgs = redisMapService.getStringValue(KeyType.顶置图片.getName(),pkId);
        if(StringUtils.isBlank(postImgs))
        {
            if(redisMapService.getlongValue(KeyType.PK图片总量.getName(),pkId)<1){return postImages;}
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .pageLimitFilter(1,9)
                    .orderByFilter("time", OrderTag.DESC);
            List<PostImageEntity> postImageEntity = daoService.queryEntities(PostImageEntity.class,filter);
            postImageEntity.forEach(img->{
                PostImage postImage = new PostImage();
                postImage.setImgUrl(img.getImgUrl());
                postImage.setImageId(img.getImgId());
                postImage.setPkId(img.getPkId());
                postImage.setPostId(img.getPostId());
                postImage.setTime(TimeUtils.convertTime(img.getTime()));
                postImages.add(postImage);
            });
            if(!CollectionUtils.isEmpty(postImages))
            {
                this.保存顶置POST图片(pkId,postImages);
            }
        }
        else
        {
            List<PostImage> images = JSON.parseArray(postImgs,PostImage.class);
            postImages.addAll(images);
        }

        return postImages;
    }

    public void pk图片总量递增(String pkId, int num) {
        redisMapService.valueIncr(KeyType.PK图片总量.getName(),pkId,num);
    }

    public void pk图片总量递减(String pkId, int imgNum) {
        redisMapService.valueDecr(KeyType.PK图片总量.getName(),pkId,imgNum);
    }


    public void 保存缩放偏移缓存(RangeEntity rangeEntity) {

        redisMapService.setStringValue(KeyType.卡点偏移缩放.getName(),String.valueOf(rangeEntity.getPkRange()),JSON.toJSONString(rangeEntity));

    }

    public RangeEntity 查询缩放偏移缓存(int range) {
        String rangeStr = redisMapService.getStringValue(KeyType.卡点偏移缩放.getName(),String.valueOf(range));

        if(!StringUtils.isBlank(rangeStr))
        {
            return JSON.parseObject(rangeStr,RangeEntity.class);
//            return null;
        }
        return null;



    }

    public void 清除缩放缓存(int value) {
        redisMapService.removeMapKey(KeyType.卡点偏移缩放.getName(),String.valueOf(value));
    }





    public BackImgEntity 查询图片缓存(int type) {
        BackImgEntity backImgEntity = null;
        String backImgEntityStr = redisMapService.getStringValue(KeyType.配置图片类型缓存.getName(),String.valueOf(type));

        if(StringUtils.isBlank(backImgEntityStr))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                    .compareFilter("type",CompareTag.Equal,type)
                    .orderByRandomFilter();
            backImgEntity = daoService.querySingleEntity(BackImgEntity.class,filter);
            if(!ObjectUtils.isEmpty(backImgEntity))
            {
                redisMapService.setStringValue(KeyType.配置图片类型缓存.getName(),String.valueOf(type),JSON.toJSONString(backImgEntity));
            }

        }
        else
        {
            backImgEntity = JSON.parseObject(backImgEntityStr,BackImgEntity.class);
        }

        return backImgEntity;

    }


    public void 刷新图片缓存(int type) {
        redisMapService.removeMapKey(KeyType.配置图片类型缓存.getName(),String.valueOf(type));
    }
}