package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.OSS存储.SceneType;
import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.task.PkTaskEntity;
import com.union.app.entity.pk.task.TaskStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.KeyName;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PkService {





    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AppDaoService daoService;

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

    public static Map<String,List<FeeTask>> taskCache = new ConcurrentHashMap<>();

    public static Map<String,List<FactualInfo>> factualCache = new ConcurrentHashMap<>();



    public List<Post> queryPkPost(String userId,String pkId,int page) throws IOException {
        List<Post> posts = postCacheService.getPostPage(userId,pkId,page);

        return posts;
    }


    public PkDetail querySinglePk(String pkId) throws IOException {
        PkDetail pkDetail = new PkDetail();
        PkEntity pk = this.querySinglePkEntity(pkId);
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setTopic(new String(pk.getTopic(),"UTF-8"));
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setWatchWord(new String(pk.getWatchWord(),"UTF-8"));
//        pkDetail.setTotalApprover(new KeyNameValue(1,dynamicService.查询今日审核员数量(pkId)));
//        pkDetail.setTotalSort(new KeyNameValue(2,dynamicService.查询今日打榜用户数量(pkId)));
        pkDetail.setTime(TimeUtils.translateTime(pk.getCreateTime()));
        pkDetail.setInvite(new KeyNameValue(pk.getIsInvite().getStatu(),pk.getIsInvite().getStatuStr()));
        pkDetail.setPkStatu(ObjectUtils.isEmpty(pk.getAlbumStatu())?new KeyNameValue(PkStatu.审核中.getStatu(),PkStatu.审核中.getStatuStr()):new KeyNameValue(pk.getAlbumStatu().getStatu(),pk.getAlbumStatu().getStatuStr()));
        return pkDetail;
    }



    public User queryPkCreator(String pkId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkEntity pkEntity = daoService.querySingleEntity(PkEntity.class,filter);

        User user = userService.queryUser(pkEntity.getUserId());
        return user;
    }







    public PkEntity querySinglePkEntity(String pkId)
    {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkEntity pk = daoService.querySingleEntity(PkEntity.class,filter);
        return pk;
    }





    public PkDynamicEntity 查询PK动态表(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkDynamicEntity pkDynamicEntity = daoService.querySingleEntity(PkDynamicEntity.class,filter);



        return pkDynamicEntity;
    }

    public int 查询Pk打赏金额(String pkId) {

        return AppConfigService.getConfigAsInteger(常量值.收款码金额,3);
    }


    private PkTaskEntity 获取指定类型任务Entity(String pkId, int type, int page) {
        EntityFilterChain filter = null;
        if(type == TaskStatu.打赏中.getStatu()){
            filter = EntityFilterChain.newFilterChain(PkTaskEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("statu",CompareTag.Equal,TaskStatu.打赏中)
                    .pageLimitFilter(page,1);;
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PkTaskEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("statu",CompareTag.Equal,  TaskStatu.已打赏)
                    .pageLimitFilter(page,1);
        }

        List<PkTaskEntity> postEntities = daoService.queryEntities(PkTaskEntity.class,filter);

        return CollectionUtils.isEmpty(postEntities)?null:postEntities.get(0);


    }


    public int 查询PK购买价格(String pkId) {
        return 100;
    }


    public boolean isPkCreator(String pkId, String userId) {
        User creator = this.queryPkCreator(pkId);
        return org.apache.commons.lang.StringUtils.equals(userId,creator.getUserId())? Boolean.TRUE:Boolean.FALSE;
    }

    public String 创建PK(String userId, String topic, String watchWord,boolean invite) throws UnsupportedEncodingException {
        String pkId = UUID.randomUUID().toString();
        PkEntity pkEntity = new PkEntity();

        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(TimeUtils.currentDateTime());
        pkEntity.setPkType(PkType.平台相册);
        pkEntity.setTopic(topic.getBytes("UTF-8"));
        pkEntity.setWatchWord(watchWord.getBytes("UTF-8"));
        pkEntity.setIsInvite(invite?InviteType.邀请:InviteType.公开);
        pkEntity.setUserId(userId);
//        if(!AppConfigService.getConfigAsBoolean(ConfigItem.对所有用户展示审核系统) && !userService.canUserView(userId))
        if(userService.canUserView(userId))
        {
            pkEntity.setAlbumStatu(PkStatu.已审核);
        }
        else
        {
            pkEntity.setAlbumStatu(PkStatu.审核中);

        }
        daoService.insertEntity(pkEntity);

        userService.创建榜次数加1(userId);



        return pkId;
    }

    public boolean 是否更新今日审核群(String pkId) {
        return !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkId,new Date()));
    }

    public boolean 是否更新今日公告(String pkId) throws UnsupportedEncodingException {
//        return !StringUtils.isBlank(dynamicService.查询PK公告消息Id(pkId));
        return !ObjectUtils.isEmpty(approveService.获取审核人员消息Entity(pkId));

    }
}
