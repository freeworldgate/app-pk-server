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
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkTipEntity;
import com.union.app.entity.pk.PostImageEntity;
import com.union.app.entity.pk.kadian.label.RangeEntity;
import com.union.app.entity.pk.city.CityEntity;
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
import java.util.concurrent.TimeUnit;

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

    public void setMapKey(String key, KeyType keyType,long value)
    {
        redisMapService.setLongValue(keyType.getName(),key,value);

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

    public void 卡点打卡人数加一(String pkId)
    {
        值加一(pkId,KeyType.卡点人数);
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




    public void 刷新用户User缓存(String userId) {
        redisMapService.removeMapKey(KeyType.用户缓存.getName(),userId);
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
        if(StringUtils.isBlank(postImgs)|| StringUtils.equalsIgnoreCase("null",postImgs))
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

    public void pk图片总量递增(String pkId, int imgNum) {
        if(imgNum == 0){return;}
        redisMapService.valueIncr(KeyType.PK图片总量.getName(),pkId,imgNum);
        通知同步图片和用户数量(pkId);
    }

    public void pk图片总量递减(String pkId, int imgNum) {
        if(imgNum == 0){return;}
        redisMapService.valueDecr(KeyType.PK图片总量.getName(),pkId,imgNum);
        通知同步图片和用户数量(pkId);
    }

    public void 通知同步图片和用户数量(String pkId) {
        redisTemplate.opsForSet().add(KeyType.卡点待同步队列.getName(),pkId);
    }


    public String 获取待同步图片和用户数量的卡点() { return redisTemplate.opsForSet().pop(KeyType.卡点待同步队列.getName()); }



    public void 通知同步城市卡点数量(int cityCode) { redisTemplate.opsForSet().add(KeyType.城市卡点数量同步队列.getName(),String.valueOf(cityCode)); }

    public String 获取待同步卡点数量的城市() { return redisTemplate.opsForSet().pop(KeyType.城市卡点数量同步队列.getName()); }



    public void 保存缩放偏移缓存(RangeEntity rangeEntity) {

        redisMapService.setStringValue(KeyType.卡点偏移缩放.getName(),String.valueOf(rangeEntity.getPkRange()),JSON.toJSONString(rangeEntity));

    }

    public RangeEntity 查询缩放偏移缓存(int range) {
        String rangeStr = redisMapService.getStringValue(KeyType.卡点偏移缩放.getName(),String.valueOf(range));

        if(!StringUtils.isBlank(rangeStr))
        {
            return JSON.parseObject(rangeStr,RangeEntity.class);
//             return null;
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


    public List<PkEntity> 查询全网排行() {
        List<PkEntity> pkEntities = new ArrayList<>();
        String pks = redisTemplate.opsForValue().get(KeyType.PK热度排行.getName());
        if(StringUtils.isBlank(pks)|| StringUtils.equalsIgnoreCase("null",pks.replace("\"","")))
        {
            pkEntities = 查询全网排行Entities();
        }
        else
        {
            List<PkEntity> images = JSON.parseArray(pks,PkEntity.class);
            pkEntities.addAll(images);
        }
        return pkEntities;
    }
    public synchronized List<PkEntity> 查询全网排行Entities()
    {
        List<PkEntity> pkEntities = new ArrayList<>();
        String pks = redisTemplate.opsForValue().get(KeyType.PK热度排行.getName());
        if(StringUtils.isBlank(pks)|| StringUtils.equalsIgnoreCase("null",pks.replace("\"","")))
        {
            EntityFilterChain sortFilter = EntityFilterChain.newFilterChain(PkEntity.class)
                    .compareFilter("totalImgs",CompareTag.Bigger,0L)
                    .andFilter()
                    .compareFilter("countrySet",CompareTag.Equal,Boolean.TRUE)
                    .orderByRandomFilter()
                    .pageLimitFilter(1,10);
            pkEntities = daoService.queryEntities(PkEntity.class,sortFilter);
            if(!CollectionUtils.isEmpty(pkEntities))
            {
                redisTemplate.opsForValue().set(KeyType.PK热度排行.getName(),JSON.toJSONString(pkEntities),AppConfigService.getConfigAsInteger(ConfigItem.热度排行榜缓存时间), TimeUnit.SECONDS);
            }

        }
        else
        {
            pkEntities = JSON.parseArray(pks,PkEntity.class);
        }

        return pkEntities;
    }


    public void 刷新全网排行榜()
    {
        redisTemplate.delete(KeyType.PK热度排行.getName());
    }

    public long getBackUpdate() {
        String updateTime = redisTemplate.opsForValue().get(KeyType.文字背景更新时间.getName());
        return StringUtils.isBlank(updateTime)?-1L:Long.valueOf(updateTime);
    }

    public void setBackUpdateFlag(long updateTime) {
        redisTemplate.opsForValue().set(KeyType.文字背景更新时间.getName(),String.valueOf(updateTime));
    }

    public List<PkTipEntity> 查询温馨提示(int type) {

        List<PkTipEntity> tips = new ArrayList<>();
        String tipStr = redisMapService.getStringValue(KeyType.温馨提示.getName(),String.valueOf(type));

        if(StringUtils.isBlank(tipStr) || StringUtils.equalsIgnoreCase("null",tipStr))
        {
            EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkTipEntity.class)
                    .compareFilter("type",CompareTag.Equal,type);
            tips = daoService.queryEntities(PkTipEntity.class,cfilter);

            if(!CollectionUtils.isEmpty(tips))
            {
                redisMapService.setStringValue(KeyType.温馨提示.getName(),String.valueOf(type),JSON.toJSONString(tips));
            }

        }
        else
        {
            tips = JSON.parseArray(tipStr,PkTipEntity.class);
        }
        return tips;
    }

    public void 清除温馨提示缓存(int type){
        redisMapService.removeMapKey(KeyType.温馨提示.getName(),String.valueOf(type));
    }

    public Collection<? extends Post> queryPosts(String pkId, int page) {

        return new ArrayList<>();
    }


    public CityEntity 查询城市名称(int cityCode) {
        if(cityCode == 0){return null;}
        CityEntity cityEntity = null;
        String cityStr = redisMapService.getStringValue(KeyType.城市码.getName(),String.valueOf(cityCode));
        if(StringUtils.isBlank(cityStr) || StringUtils.equalsIgnoreCase("null",cityStr))
        {
            EntityFilterChain cfilter = EntityFilterChain.newFilterChain(CityEntity.class)
                    .compareFilter("cityCode",CompareTag.Equal,cityCode);
            cityEntity = daoService.querySingleEntity(CityEntity.class,cfilter);
            if(!ObjectUtils.isEmpty(cityEntity))
            {
                redisMapService.setStringValue(KeyType.城市码.getName(),String.valueOf(cityCode),JSON.toJSONString(cityEntity));
            }
        }
        else
        {
            cityEntity = JSON.parseObject(cityStr,CityEntity.class);
        }
        return cityEntity;
    }

    public long 查询隐藏打卡信息(String pkId) {

        return redisMapService.getlongValue(KeyType.卡点已隐藏打卡数量.getName(),pkId);

    }
    public void 隐藏数量加1(String pkId) {
        redisMapService.valueIncr(KeyType.卡点已隐藏打卡数量.getName(),pkId);
    }

    public void 隐藏数量减1(String pkId) {
        redisMapService.valueDecr(KeyType.卡点已隐藏打卡数量.getName(),pkId);
    }

    public void 城市PK数量加一(int cityCode) {
        redisMapService.valueIncr(KeyType.城市卡点数量.getName(),String.valueOf(cityCode));
    }
}
