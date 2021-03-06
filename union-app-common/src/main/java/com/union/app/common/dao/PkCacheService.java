package com.union.app.common.dao;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.constant.ConfigItem;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PkCacheService {
    private static volatile String minPk=UUID.randomUUID().toString();
    private static final Map<String,AtomicInteger> pkSorts = new HashMap<>();
    private static final Map<String,HashMap<Integer,Long>> pkCacheUpdateTime = new HashMap<>();
    private static final Map<String,AtomicInteger> pkCacheTimes = new HashMap<>();
    private static final Map<String,HashMap<Integer,List<Post>>> postCache = new HashMap<>();


    private boolean PK排序超限(String pkId) {

        AtomicInteger times = new AtomicInteger(0);
        AtomicInteger minTimes = new AtomicInteger(0);
        pkCacheTimes.putIfAbsent(pkId,times);
        pkCacheTimes.putIfAbsent(minPk,minTimes);
        times = pkCacheTimes.get(pkId);
        minTimes = pkCacheTimes.get(minPk);

        if(pkSorts.size() < AppConfigService.getConfigAsInteger(ConfigItem.PK缓存数量))
        {
            pkSorts.put(pkId,times);
            if( times.get() > minTimes.get())
            {
                minPk = pkId;
            }
            return false;
        }
        else
        {
            if( times.get() > minTimes.get())
            {
                minPk = pkId;
                if(ObjectUtils.isEmpty(pkSorts.get(pkId)))
                {
                    pkSorts.remove(minPk);
                }
                pkSorts.put(pkId,times);

                return true;
            }
            else
            {
                return false;
            }



        }





    }



//
//    private static Map<Class<?>,LRUMap> cache = new HashMap<>();
//
//
//    @Value("${spring.cache.lru.user}")
//    private int userSize;
//
//    @Value("${spring.cache.lru.pk}")
//    private int pkSize;
//
//    @Value("${spring.cache.lru.post}")
//    private int postSize;
//
//    @Value("${spring.cache.lru.message}")
//    private int messageSize;
//
//    @Value("${spring.cache.lru.comment}")
//    private int commentSize;
//    //最高一百页
//
//
//    @PostConstruct
//    public void init()
//    {
//        cache.put(PkEntity.class,new LRUMap(pkSize));
//        cache.put(PostEntity.class,new LRUMap(postSize));
//        cache.put(UserEntity.class,new LRUMap(userSize));
//        cache.put(ApproveCommentEntity.class,new LRUMap(commentSize));
//
//    }
//
//    public <T> void remove(T t)
//    {
//
//        LRUMap entityCache = cache.get(t.getClass());
//        if(entityCache == null){return;}
//        if(t instanceof PkEntity)
//        {
//            entityCache.remove(((PkEntity) t).getPkId());
//        }
//        if(t instanceof PostEntity)
//        {
//            entityCache.remove(((PostEntity) t).getPostId());
//        }
//        if(t instanceof UserEntity)
//        {
//            entityCache.remove(((UserEntity) t).getUserId());
//        }
//        if(t instanceof ApproveMessageEntity)
//        {
//            entityCache.remove(((ApproveMessageEntity) t).getPkId());
//        }
//        if(t instanceof ApproveCommentEntity)
//        {
//            ApproveCommentEntity approveCommentEntity = (ApproveCommentEntity) t;
//            entityCache.remove((approveCommentEntity.getPkId() + "-" + approveCommentEntity.getPostId()));
//        }
//
//
//
//    }
//
//    public <T> void save(T t)
//    {
//        LRUMap entityCache = cache.get(t.getClass());
//        if(entityCache == null){return;}
//        if(t instanceof PkEntity)
//        {
//            entityCache.put(((PkEntity) t).getPkId(),t);
//        }
//        if(t instanceof PostEntity)
//        {
//            entityCache.put(((PostEntity) t).getPostId(),t);
//        }
//        if(t instanceof UserEntity)
//        {
//            entityCache.put(((UserEntity) t).getUserId(),t);
//        }
//        if(t instanceof ApproveMessageEntity)
//        {
//            entityCache.put(((ApproveMessageEntity) t).getPkId(),t);
//        }
//        if(t instanceof ApproveCommentEntity)
//        {
//            ApproveCommentEntity approveCommentEntity = (ApproveCommentEntity) t;
//            entityCache.put((approveCommentEntity.getPkId() + "-" + approveCommentEntity.getPostId()),t);
//        }
//
//    }
//
//
//    public <T> void save(String id,T t)
//    {
//        LRUMap entityCache = cache.get(t.getClass());
//        if(ObjectUtils.isEmpty(entityCache)){return;}
//        entityCache.put(id,t);
//    }
//
//
//    public <T> T get(String id,Class<T> tClass)
//    {
//        LRUMap entityCache = cache.get(tClass);
//        if(ObjectUtils.isEmpty(entityCache)){return null;}
//        return (T)entityCache.get(id);
//    }
//


}
