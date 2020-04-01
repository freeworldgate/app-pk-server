package com.union.app.service.pk.service.cache;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SingleCacheService<T> {




    private HashMap<String,T>  singleCacheMap = new HashMap<>();


//    public T getSingleCacheValue(String key){
//        T t = singleCacheMap.get(key);
//        if(!ObjectUtils.isEmpty(t)){
//            return t;
//        }
//        TimePolicy timePolicy =  获取时间策略();
//
//    }
//    public T getListCacheValue(String key){}
//    public T getListPageCacheValue(String key,int page){}




































    @Scheduled(cron = "*/60 * * * * ?") // 定时刷新页面
    public void work() throws Exception {







    }





}
