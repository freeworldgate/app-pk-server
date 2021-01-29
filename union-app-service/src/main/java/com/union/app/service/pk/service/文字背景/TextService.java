package com.union.app.service.pk.service.文字背景;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.pk.捞人.CreateUserFind;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.卡点.捞人.FindStatu;
import com.union.app.entity.pk.卡点.捞人.FindUserEntity;
import com.union.app.entity.pk.文字背景.TextBackEntity;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PostCacheService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

@Service
public class TextService {


    @Autowired
    AppDaoService daoService;

    private static Map<Integer,TextBack> backMap = new HashMap<>();

    private static volatile long updateTime;



    public List<TextBack> 查询背景() {
        List<TextBack> list = new ArrayList<>();
        if(backMap.isEmpty() || System.currentTimeMillis() - updateTime > 1 * 3600 * 1000)
        {
            
            backMap.putAll(查询TextBackEntity());


        }
        list.addAll(backMap.values());
        return list;

    }

    private synchronized Map<Integer,TextBack> 查询TextBackEntity() {
        Map<Integer,TextBack> backMap = new HashMap<>();
        if(backMap.isEmpty() || System.currentTimeMillis() - updateTime > 1 * 3600 * 1000)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(TextBackEntity.class)
                    .pageLimitFilter(1,30)
                    .orderByRandomFilter();
            List<TextBackEntity> textBackEntities = daoService.queryEntities(TextBackEntity.class,filter);
            textBackEntities.forEach(textBackEntity -> {
                TextBack textBack = new TextBack();
                textBack.setBackId(textBackEntity.getBackId());
                textBack.setBackColor(textBackEntity.getBackColr());
                textBack.setBackUrl(textBackEntity.getBackUrl());
                textBack.setFontColor(textBackEntity.getFontColor());
                backMap.put(textBackEntity.getBackId(),textBack);

            });


            updateTime = System.currentTimeMillis();
        }
        return backMap;

    }

    public TextBack 查询TextBackEntity(int backId) {
        TextBack textBack = new TextBack();
        textBack.setBackId(-1);
        textBack.setBackColor("fafafa");
        textBack.setFontColor("000000");
        textBack.setBackUrl("");

        EntityFilterChain filter = EntityFilterChain.newFilterChain(TextBackEntity.class)
                .compareFilter("backId",CompareTag.Equal,backId);
        TextBackEntity textBackEntity = daoService.querySingleEntity(TextBackEntity.class,filter);
        if(!ObjectUtils.isEmpty(textBackEntity))
        {
            textBack.setBackId(textBackEntity.getBackId());
            textBack.setBackUrl(textBackEntity.getBackUrl());
            textBack.setFontColor(textBackEntity.getFontColor());
            textBack.setBackColor(textBackEntity.getBackColr());
        }

        return textBack;
    }
}
