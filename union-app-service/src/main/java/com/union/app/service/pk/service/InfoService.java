package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.Comment;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.GreatePkEntity;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.PkPostListEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.助力浏览评论分享.UserLikeEntity;
import com.union.app.entity.评论.PkCommentEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoService {




    @Autowired
    PayService payService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    InfoService appService;

    @Autowired
    PkService pkService;

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
    PostCacheService postCacheService;

    @Autowired
    ApproveService approveService;

    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    MediaService mediaService;

    @Autowired
    PkDataService pkDataService;

    @Autowired
    ComplainService complainService;


    public List<User> 查询示例用户(String pkId, int type) {
        List<User> users = new ArrayList<>();

        if(type == 0)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(GreatePkEntity.class)
                    .compareFilter("pkId", CompareTag.Equal, pkId)
                    .pageLimitFilter(1, AppConfigService.getConfigAsInteger(ConfigItem.信息展示页面用户数量))
                    .orderByRandomFilter();
            List<GreatePkEntity> entities = daoService.queryEntities(GreatePkEntity.class, filter);
            if(!CollectionUtils.isEmpty(entities))
            {
                entities.forEach(entity->{
                    User user = userService.queryUser(entity.getUserId());
                    if(!ObjectUtils.isEmpty(user))
                    {
                        users.add(user);
                    }
                });
            }
        }
        if(type == 1)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(InvitePkEntity.class)
                    .compareFilter("pkId", CompareTag.Equal, pkId)
                    .pageLimitFilter(1, AppConfigService.getConfigAsInteger(ConfigItem.信息展示页面用户数量))
                    .orderByRandomFilter();
            List<InvitePkEntity> entities = daoService.queryEntities(InvitePkEntity.class, filter);
            if(!CollectionUtils.isEmpty(entities))
            {
                entities.forEach(entity->{
                    User user = userService.queryUser(entity.getUserId());
                    if(!ObjectUtils.isEmpty(user))
                    {
                        users.add(user);
                    }
                });
            }
        }
        if(type == 2)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
                    .compareFilter("pkId", CompareTag.Equal, pkId)
                    .pageLimitFilter(1, AppConfigService.getConfigAsInteger(ConfigItem.信息展示页面用户数量))
                    .orderByRandomFilter();
            List<ComplainEntity> entities = daoService.queryEntities(ComplainEntity.class, filter);
            if(!CollectionUtils.isEmpty(entities))
            {
                entities.forEach(entity->{
                    User user = userService.queryUser(entity.getUserId());
                    if(!ObjectUtils.isEmpty(user))
                    {
                        users.add(user);
                    }
                });
            }
        }
        if(type == 3)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkPostListEntity.class)
                    .compareFilter("pkId", CompareTag.Equal, pkId)
//                    .andFilter()
//                    .compareFilter("statu",CompareTag.Equal,PostStatu.审核中)
                    .pageLimitFilter(1, AppConfigService.getConfigAsInteger(ConfigItem.信息展示页面用户数量))
                    .orderByRandomFilter();
            List<PkPostListEntity> entities = daoService.queryEntities(PkPostListEntity.class, filter);
            if(!CollectionUtils.isEmpty(entities))
            {
                entities.forEach(entity->{
                    User user = userService.queryUser(entity.getUserId());
                    if(!ObjectUtils.isEmpty(user))
                    {
                        users.add(user);
                    }
                });
            }
        }
        if(type == 4)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkPostListEntity.class)
                    .compareFilter("pkId", CompareTag.Equal, pkId)
//                    .andFilter()
//                    .compareFilter("statu",CompareTag.Equal,PostStatu.上线)
                    .pageLimitFilter(1, AppConfigService.getConfigAsInteger(ConfigItem.信息展示页面用户数量))
                    .orderByRandomFilter();
            List<PkPostListEntity> entities = daoService.queryEntities(PkPostListEntity.class, filter);
            if(!CollectionUtils.isEmpty(entities))
            {
                entities.forEach(entity->{
                    User user = userService.queryUser(entity.getUserId());
                    if(!ObjectUtils.isEmpty(user))
                    {
                        users.add(user);
                    }
                });
            }
        }

        return users;
    }




























    public int 查询数量(String pkId, int type) {
        if(type == 0 ){ return dynamicService.getKeyValue(CacheKeyName.点赞,pkId);}
        if(type == 1 ){ return dynamicService.getKeyValue(CacheKeyName.收藏,pkId);}
        if(type == 2 ){ return dynamicService.getKeyValue(CacheKeyName.投诉,pkId);}
        if(type == 3 ){ return dynamicService.getKeyValue(CacheKeyName.审核中数量,pkId);}
        if(type == 4 ){ return dynamicService.getKeyValue(CacheKeyName.已审核数量,pkId);}

        return 0;
    }

    public String 查询标题(int type) {

        if(type == 0 ){ return "喜欢";}
        if(type == 1 ){ return "收藏";}
        if(type == 2 ){ return "投诉";}
        if(type == 3 ){ return "审核中";}
        if(type == 4 ){ return "图册";}


        return "";
    }

    public String 查询图标(int type) {
        if(type == 0 ){ return "/images/greate.png";}
        if(type == 1 ){ return "/images/collection.png";}
        if(type == 2 ){ return "/images/complain.png";}
        if(type == 3 ){ return "/images/waiting2.png";}
        if(type == 4 ){ return "/images/pic.png";}


        return "";
    }

    public List<User> 查询喜欢信息(String pkId, int page) {
        List<User> users = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(GreatePkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .pageLimitFilter(page,20)
                .orderByFilter("time",OrderTag.DESC);
        List<GreatePkEntity> entities = daoService.queryEntities(GreatePkEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities))
        {
            entities.forEach(entity->{
                User user = userService.queryUser(entity.getUserId());
                user.setTime(TimeUtils.convertTime(entity.getTime()));
                users.add(user);
            });


        }
        return users;



    }
















}
