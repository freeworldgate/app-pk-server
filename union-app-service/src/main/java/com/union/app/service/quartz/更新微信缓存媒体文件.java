package com.union.app.service.quartz;

import com.union.app.common.dao.AppDaoService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class 更新微信缓存媒体文件 {



    @Autowired
    DynamicService dynamicService;

    @Autowired
    AppDaoService daoService;


    @Autowired
    PkService pkService;

    @Scheduled(cron = "0 0 5 * * ?") //每天5点执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() throws Exception {

        //需要更新群组文件


//
//        List<PkEntity> pks = null;
//        int page = 1;
//        while(!CollectionUtils.isEmpty(pks=mediaService.查询需要更新的群组(page)))
//        {
//
//            for(PkEntity pk:pks)
//            {
//                String mediaId = WeChatUtil.uploadImg2Wx(dynamicService.查询PK群组二维码Url(pk.getPkId()));
//                dynamicService.设置PK群组二维码MediaId(pk.getPkId(),mediaId);
//                pkService.更新群组时间(pk.getPkId());
//
//            }
//            page++;
//
//
//        }



//        List<PkCashierEntity> pkCashiers = mediaService.查询需要更新的媒体图片(PkCashierEntity.class);
//        List<ApproveMessageEntity> messages = mediaService.查询需要更新的媒体图片(ApproveMessageEntity.class);
//        List<PkEntity> pks = mediaService.查询需要更新的群组();


//        for(PkCashierEntity cashier:pkCashiers)
//        {
//            String mediaId = WeChatUtil.uploadImg2Wx(cashier.getLinkUrl());
//            cashier.setMediaId(mediaId);
//            cashier.setLastUpdateTime(System.currentTimeMillis());
//            daoService.updateEntity(cashier);
//
//        }

//        for(ApproveMessageEntity message:messages)
//        {
//            String mediaId = WeChatUtil.uploadImg2Wx(message.getImgUrl());
//            message.setMediaId(mediaId);
//            message.setLastUpdateTime(System.currentTimeMillis());
//            daoService.updateEntity(message);
//
//        }

//        for(PkEntity pk:pks)
//        {
//            String mediaId = WeChatUtil.uploadImg2Wx(dynamicService.查询内置公开PK群组二维码Url(pk.getPkId()));
//            dynamicService.设置内置公开PK群组二维码MediaId(pk.getPkId(),mediaId);
//
//        }
//













    }












}
