package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.OSS存储.SceneType;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.*;
import com.union.app.entity.pk.*;
import com.union.app.plateform.storgae.KeyName;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService {


    @Autowired
    AppDaoService daoService;


    public OrderEntity queryOrder(String pkId, String userId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(OrderEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        OrderEntity orderEntity = daoService.querySingleEntity(OrderEntity.class,filter);
        return orderEntity;
    }

    public boolean 存在打赏订单且状态未完成(String pkId, String userId) {
        return false;
    }

    public boolean 存在打赏订单且状态已完成(String pkId, String userId) {
        return false;
    }
}
