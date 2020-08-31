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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import sun.security.provider.certpath.PKIXExtendedParameters;

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
    public <T> List<T> 查询分页表(Class<T> tClass,int page)
    {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(tClass)
                .compareFilter("lastUpdateTime", CompareTag.Small, System.currentTimeMillis() - ((AppConfigService.getConfigAsInteger(ConfigItem.媒体图片最大过期时间)) * 3600 * 1000))
                .pageLimitFilter(1, 20);
        List<T> entities = daoService.queryEntities(tClass, filter);

        return entities;
    }



    public <T> List<T> 查询需要更新的媒体图片(Class<T> tClass) {

        List<T> pkEntities = new ArrayList<>();
        int page = 1;
        List<T> pks = null;
        while(!CollectionUtils.isEmpty(pks = this.查询分页表(tClass,page)))
        {
            pkEntities.addAll(pks);
            page++;
        }
        return pkEntities;
    }


    public List<PkEntity> 查询需要更新Group的PK(int page) {


        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkType",CompareTag.Equal, PkType.内置相册)
                .andFilter()
                .compareFilter("isInvite",CompareTag.Equal, InviteType.公开)
                .pageLimitFilter(page,20);
        List<PkEntity> approveMessageEntities = daoService.queryEntities(PkEntity.class,filter);
        return approveMessageEntities;

    }

    public List<PkEntity> 查询需要更新的群组() {

        List<PkEntity> pkEntities = new ArrayList<>();
        int page = 1;
        List<PkEntity> pks = null;
        while(!CollectionUtils.isEmpty(pks = this.查询需要更新Group的PK(page)))
        {
            pkEntities.addAll(pks);
            page++;
        }
        return pkEntities;
    }

}
