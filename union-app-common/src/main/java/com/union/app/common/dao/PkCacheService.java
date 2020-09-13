package com.union.app.common.dao;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.审核.ApproveCommentEntity;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.entity.用户.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import org.apache.commons.collections.map.LRUMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class PkCacheService {


    private static Map<Class<?>,LRUMap> cache = new HashMap<>();


    @Value("${spring.cache.lru.user}")
    private int userSize;

    @Value("${spring.cache.lru.pk}")
    private int pkSize;

    @Value("${spring.cache.lru.post}")
    private int postSize;

    @Value("${spring.cache.lru.message}")
    private int messageSize;

    @Value("${spring.cache.lru.comment}")
    private int commentSize;
    //最高一百页


    @PostConstruct
    public void init()
    {
        cache.put(PkEntity.class,new LRUMap(pkSize));
        cache.put(PostEntity.class,new LRUMap(postSize));
        cache.put(UserEntity.class,new LRUMap(userSize));
        cache.put(ApproveMessageEntity.class,new LRUMap(messageSize));
        cache.put(ApproveCommentEntity.class,new LRUMap(commentSize));

//        cache.put(PkEntity.class,new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.PKEntity缓存数量)));
//        cache.put(PostEntity.class,new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.POSTEntity缓存数量)));
//        cache.put(UserEntity.class,new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.USEREntity缓存数量)));
//        cache.put(ApproveMessageEntity.class,new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.APPROVEMESSAGEEntity缓存数量)));
//        cache.put(ApproveCommentEntity.class,new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.APPROVECOMMENTEntity缓存数量)));

    }

    public <T> void remove(T t)
    {

        LRUMap entityCache = cache.get(t.getClass());
        if(entityCache == null){return;}
        if(t instanceof PkEntity)
        {
            entityCache.remove(((PkEntity) t).getPkId());
        }
        if(t instanceof PostEntity)
        {
            entityCache.remove(((PostEntity) t).getPostId());
        }
        if(t instanceof UserEntity)
        {
            entityCache.remove(((UserEntity) t).getUserId());
        }
        if(t instanceof ApproveMessageEntity)
        {
            entityCache.remove(((ApproveMessageEntity) t).getPkId());
        }
        if(t instanceof ApproveCommentEntity)
        {
            ApproveCommentEntity approveCommentEntity = (ApproveCommentEntity) t;
            entityCache.remove((approveCommentEntity.getPkId() + "-" + approveCommentEntity.getPostId()));
        }



    }

    public <T> void save(T t)
    {
        LRUMap entityCache = cache.get(t.getClass());
        if(entityCache == null){return;}
        if(t instanceof PkEntity)
        {
            entityCache.put(((PkEntity) t).getPkId(),t);
        }
        if(t instanceof PostEntity)
        {
            entityCache.put(((PostEntity) t).getPostId(),t);
        }
        if(t instanceof UserEntity)
        {
            entityCache.put(((UserEntity) t).getUserId(),t);
        }
        if(t instanceof ApproveMessageEntity)
        {
            entityCache.put(((ApproveMessageEntity) t).getPkId(),t);
        }
        if(t instanceof ApproveCommentEntity)
        {
            ApproveCommentEntity approveCommentEntity = (ApproveCommentEntity) t;
            entityCache.put((approveCommentEntity.getPkId() + "-" + approveCommentEntity.getPostId()),t);
        }

    }


    public <T> void save(String id,T t)
    {
        LRUMap entityCache = cache.get(t.getClass());
        if(ObjectUtils.isEmpty(entityCache)){return;}
        entityCache.put(id,t);
    }


    public <T> T get(String id,Class<T> tClass)
    {
        LRUMap entityCache = cache.get(tClass);
        if(ObjectUtils.isEmpty(entityCache)){return null;}
        return (T)entityCache.get(id);
    }



}
