package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class MediaService {





    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    MediaService appService;

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

    //所有媒体文件   提前六个小时更新



    public List<PkCashierGroupEntity> 查询需要更新的群组() {
        //还剩6小时就过期的图片
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("lastUpdateTime",CompareTag.Small, System.currentTimeMillis() - ((AppConfigService.getConfigAsInteger(ConfigItem.媒体图片最大过期时间))*3600*1000))
                .pageLimitFilter(1,20);
        List<PkCashierGroupEntity> pkCashierGroupEntities = daoService.queryEntities(PkCashierGroupEntity.class,filter);
        return pkCashierGroupEntities;
    }

    public List<PkCashierFeeCodeEntity> 查询需要更新的收款码() {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                .compareFilter("lastUpdateTime",CompareTag.Small, System.currentTimeMillis() - ((AppConfigService.getConfigAsInteger(ConfigItem.媒体图片最大过期时间))*3600*1000))
                .pageLimitFilter(1,20);
        List<PkCashierFeeCodeEntity> pkCashierFeeCodeEntities = daoService.queryEntities(PkCashierFeeCodeEntity.class,filter);
        return pkCashierFeeCodeEntities;
    }

    public List<ApproveMessageEntity> 查询需要更新的公告() {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(ApproveMessageEntity.class)
                .compareFilter("lastUpdateTime",CompareTag.Small, System.currentTimeMillis() - ((AppConfigService.getConfigAsInteger(ConfigItem.媒体图片最大过期时间))*3600*1000))
                .pageLimitFilter(1,20);
        List<ApproveMessageEntity> approveMessageEntities = daoService.queryEntities(ApproveMessageEntity.class,filter);
        return approveMessageEntities;

    }
}
