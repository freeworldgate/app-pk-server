package com.union.app.service.pk.service;

import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.pk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
