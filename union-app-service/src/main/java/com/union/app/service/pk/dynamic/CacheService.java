package com.union.app.service.pk.dynamic;

import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CacheService {


    @Autowired
    RedisMapService redisMapService;

    @Autowired
    RedisSortSetService redisSortSetService;

//    public long 获取收藏积分(String pkId,String userId){ return redisMapService.getIntValue(CacheKeyName.打榜Follow(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.收藏一次的积分,100); }
//
//    public long 获取今日分享积分(String pkId,String userId){ return redisMapService.getIntValue(CacheKeyName.打榜Share(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.分享一次的积分,100) ;}
//
//    public long 获取用户排名(String pkId,String userId) { return redisSortSetService.getIndex(CacheKeyName.打榜排名列表(pkId),userId); }

//    public void 更新今日用户排名(String pkId,String userId){ double score = (获取收藏积分(pkId,userId) + 获取今日分享积分(pkId,userId)) * -1.0D;redisSortSetService.addEle(CacheKeyName.打榜排名列表(pkId),userId,score); }







}




