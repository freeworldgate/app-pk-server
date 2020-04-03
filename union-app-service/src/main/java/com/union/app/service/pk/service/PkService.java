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
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.KeyName;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
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


    public static Map<String,List<FeeTask>> taskCache = new ConcurrentHashMap<>();

    public static Map<String,List<FactualInfo>> factualCache = new ConcurrentHashMap<>();



    public List<Post> queryPkPost(String userId,String pkId,int page) throws IOException {
        List<Post> posts = postCacheService.getPostPage(userId,pkId,page);

        return posts;
    }


    public PkDetail querySinglePk(String pkId) throws IOException {
        PkDetail pkDetail = ossStorage.getOssWidthMapCache(SceneType.存储PK缓存,"PK-Cache",pkId,PkDetail.class);
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
}
