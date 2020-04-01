package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.share.CreatorInfo;
import com.union.app.domain.pk.share.PosterInfo;
import com.union.app.domain.pk.share.ShareInfo;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.service.cache.SingleCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TimeCacheService {




    private static SingleCacheService<ShareInfo> shareInfos = new SingleCacheService<ShareInfo>();
    private static SingleCacheService<CreatorInfo> creatorInfos = new SingleCacheService<CreatorInfo>();
    private static SingleCacheService<PosterInfo> posterInfos = new SingleCacheService<PosterInfo>();








    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;

    @Autowired
    AppDaoService daoService;



























    @Scheduled(cron = "*/60 * * * * ?") // 定时刷新页面
    public void work() throws Exception {







    }





}
