package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.dao.KeyService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.common.dao.PkCacheService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PkService {


    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    RedisMapService redisMapService;


    @Autowired
    AppDaoService daoService;

    @Autowired
    PkService pkService;

    @Autowired
    AppService appService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;


    @Autowired
    PkCacheService pkCacheService;

    @Autowired
    KeyService keyService;

    public static Map<String,List<FeeTask>> taskCache = new ConcurrentHashMap<>();

    public static Map<String,List<FactualInfo>> factualCache = new ConcurrentHashMap<>();



    public List<Post> queryPkPost(String pkId,int page) throws IOException {
        List<Post> posts = new ArrayList<>();
        //统计每分钟请求次数
        if(saveRequest(pkId,page)) {
            posts.addAll(keyService.queryPosts(pkId, page));
        }
        if(CollectionUtils.isEmpty(posts)) {
            posts.addAll(查询Post(pkId, page));
        }
        if(saveRequest(pkId,page)) {
            saveRequestEntity(pkId,page,posts);
        }
        return posts;
    }

    private void saveRequestEntity(String pkId, int page, List<Post> posts) {


    }

    private boolean saveRequest(String pkId, int page) {

            //阈值


        return true;



    }

    private List<Post> 查询Post( String pkId, int page) throws UnsupportedEncodingException {

        List<Post> posts = queryPosts(pkId,page);

        return posts;

    }



    public List<Post> queryPosts(String pkId,int page) {
        List<Post> posts = new LinkedList<>();
        List<PostEntity> pageList =  pkService.查询签到列表(pkId,page);
        for(PostEntity postEntity:pageList)
        {
            Post post = postService.translate(postEntity);

            if(!ObjectUtils.isEmpty(post)) {
                posts.add(post);
            }
        }
        return posts;

    }

    private List<PostEntity> 查询签到列表(String pkId, int page) {

        List<PostEntity> ids = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("statu",CompareTag.NotEqual,PostStatu.隐藏)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("time",OrderTag.DESC);

        List<PostEntity> entities = daoService.queryEntities(PostEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities)){ids.addAll(entities);}
        return ids;






    }




    public PkDetail querySinglePk(PkEntity pk) throws IOException {
        if(ObjectUtils.isEmpty(pk)){return null;}
        String pkId = pk.getPkId();
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pk.getPkId());

        pkDetail.setUser(userService.queryUser(pk.getUserId()));

        pkDetail.setTime(TimeUtils.convertTime(pk.getTime()));
        pkDetail.setBackUrl(pk.getBackUrl());
        pkDetail.setTopPostId(pk.getTopPostId());
        pkDetail.setTopPost(postService.查询顶置帖子(pk));
        return pkDetail;
    }


    public User queryPkCreator(String pkId){
        PkEntity pkEntity =  querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){return null;}
        User user = userService.queryUser(pkEntity.getUserId());
        return user;
    }


    public PkEntity querySinglePkEntity(String pkId)
    {
        if(StringUtils.isBlank(pkId) || StringUtils.equalsIgnoreCase("undefined",pkId)|| StringUtils.equalsIgnoreCase("Nan",pkId)|| StringUtils.equalsIgnoreCase("null",pkId)){
            return null;
        }

            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId);
            PkEntity pkEntity = daoService.querySingleEntity(PkEntity.class,filter);



        return pkEntity;
    }








    public boolean isPkCreator(String pkId, String userId) {
        PkEntity pk = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pk)){return false;}
        return org.apache.commons.lang.StringUtils.equals(userId,pk.getUserId())? Boolean.TRUE:Boolean.FALSE;
    }

    public String 创建PK(String userId, String topic, String watchWord,boolean invite) throws UnsupportedEncodingException {
        String pkId = IdGenerator.getPkId();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setTime(System.currentTimeMillis());
        if(userService.是否是遗传用户(userId))
        {
//            pkEntity.setPkType(PkType.运营相册);
        }
        else
        {
//            pkEntity.setPkType(PkType.审核相册);
        }



        pkEntity.setUserId(userId);

        daoService.insertEntity(pkEntity);
        userService.创建榜次数加1(userId);
        return pkId;
    }








    public boolean 是否更新今日审核群(PkEntity pkEntity) {
//        if(pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.公开)
//        {
//            return !StringUtils.isBlank(dynamicService.查询内置公开PK群组二维码MediaId(pkEntity.getPkId()));
//        }
        return !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkEntity.getPkId()));
    }









    public void 更新群组时间(String pkId) {

        Map<String,Object> map = new HashMap<>();
        map.put("updateTime",System.currentTimeMillis());
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);

    }



    private void 更新PK审核数量(String pkId) {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        dynamicService.valueIncr(CacheKeyName.已审核数量,pkId);
        dynamicService.valueDecr(CacheKeyName.审核中数量,pkId);
//        pkEntity.setApproved(pkEntity.getApproved() + 1);
//        pkEntity.setApproving(pkEntity.getApproving() - 1);
//        daoService.updateEntity(pkEntity);

    }

    public void 添加一个审核中(String pkId) {
//        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        dynamicService.valueIncr(CacheKeyName.审核中数量,pkId);

////        pkEntity.setApproving(pkEntity.getApproving() + 1);
//        daoService.updateEntity(pkEntity);
    }

    public void 减少一个审核中(String pkId) {
//        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        dynamicService.valueDecr(CacheKeyName.审核中数量,pkId);
//        pkEntity.setApproving(pkEntity.getApproving() - 1);
//        daoService.updateEntity(pkEntity);
    }




    public boolean isCreatedByVip(String pkId) {
        if(StringUtils.isBlank(pkId)){return false;}
        if(StringUtils.equalsIgnoreCase("undefined",pkId) || StringUtils.equalsIgnoreCase("null",pkId) || StringUtils.equalsIgnoreCase("Nan",pkId)) {return false;}
        PkEntity pkEntity =  querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){return false;}
        UserEntity creator = userService.queryUserEntity(pkEntity.getUserId());
        if(ObjectUtils.isEmpty(creator)){return false;}
        return org.apache.commons.lang.ObjectUtils.equals(creator.getUserType(),UserType.重点用户);




    }


    public void 顶置图册是否到期(String pkId, String postId) throws AppException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);

        if(!StringUtils.isBlank(pkEntity.getTopPostId()) && !TimeUtils.是否顶置已经过期(pkEntity.getTopPostSetTime(), pkEntity.getTopPostTimeLength()))
        {

            String leftTime = TimeUtils.顶置剩余时间(pkEntity.getTopPostSetTime(),pkEntity.getTopPostTimeLength());

            throw AppException.buildException(PageAction.信息反馈框("操作失败!","当前顶置未到期,剩余"+ leftTime+"!"));
        }





    }


    public void 修改首页图册(String pkId, String postId,int value) throws AppException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);

        if(!StringUtils.isBlank(pkEntity.getTopPostId()) && !TimeUtils.是否顶置已经过期(pkEntity.getTopPostSetTime(), pkEntity.getTopPostTimeLength()))
        {
            String leftTime = TimeUtils.顶置剩余时间(pkEntity.getTopPostSetTime(),pkEntity.getTopPostTimeLength());

            throw AppException.buildException(PageAction.信息反馈框("操作失败!","当前顶置未到期,剩余"+ leftTime+"!"));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("topPostId",postId);
        map.put("topPostSetTime",System.currentTimeMillis());
        map.put("topPostTimeLength",value*1L);
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);




    }



}
