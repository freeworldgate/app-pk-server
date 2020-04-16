package com.union.app.service.quartz;

import com.union.app.common.config.AppConfigService;
import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.apply.PayOrderEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.UserInfoService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Component
public class 处理过期的订单 {



    @Autowired
    DynamicService dynamicService;



    @Autowired
    UserInfoService userInfoService;


    @Scheduled(cron = "* */50 * * * ?") // 每分钟执行一次  刷新整个PKID的所有PAGE
    @Transactional(rollbackOn = Exception.class)
    public void work() throws Exception {



        String orderId = dynamicService.获取过期订单();

        if(StringUtils.isBlank(orderId)){return;}

        PayOrderEntity orderEntity = userInfoService.获取OrderEntityById(orderId);

        if(ObjectUtils.equals(orderEntity.getOrderStatu(),OrderStatu.订单确认中))
        {

            int leftSeconds = userInfoService.获取订单剩余时间(orderEntity);

            if(leftSeconds == 0){

                userInfoService.确认已收款(orderEntity.getOrderId(),orderEntity.getCashierId());

            }



        }













//        //TODO 动态信息  和  任务;
//
//        //TODO 定时计算和刷新 PK 所处的模式
//
//        String pkId = dynamicService.获取需要变更模式的PKID();
//        if(StringUtils.isBlank(pkId)){return;}
//        if(dynamicService.isInTask(pkId)){
//            //生成任务
//            dynamicService.生成PK打赏任务(pkId);
//            //任务模式
//            dynamicService.修改PK模式(pkId,1);
//
//        }
//        else
//        {
//            dynamicService.清理所有任务(pkId);
//
//            dynamicService.修改PK模式(pkId,0);
//
//        }
//
//








    }












}
