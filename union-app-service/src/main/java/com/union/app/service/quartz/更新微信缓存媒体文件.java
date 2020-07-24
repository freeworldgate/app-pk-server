package com.union.app.service.quartz;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.entity.pk.PkCashierFeeCodeEntity;
import com.union.app.entity.pk.PkCashierGroupEntity;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.MediaService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class 更新微信缓存媒体文件 {



    @Autowired
    DynamicService dynamicService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    MediaService mediaService;

    @Scheduled(cron = "0 0 5 * * ?") //每天5点执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() throws Exception {

        List<PkCashierGroupEntity> groups = mediaService.查询需要更新的群组();
        List<PkCashierFeeCodeEntity> feeCodes = mediaService.查询需要更新的收款码();
        List<ApproveMessageEntity> messages = mediaService.查询需要更新的公告();

        for(PkCashierGroupEntity group:groups)
        {
            String mediaId = WeChatUtil.uploadImg2Wx(group.getGroupUrl());
            group.setGroupMediaId(mediaId);
            group.setLastUpdateTime(System.currentTimeMillis());
            daoService.updateEntity(group);

        }
        for(PkCashierFeeCodeEntity feeCode:feeCodes)
        {
            String mediaId = WeChatUtil.uploadImg2Wx(feeCode.getFeeCodeUrl());
            feeCode.setFeeCodeMediaId(mediaId);
            feeCode.setLastUpdateTime(System.currentTimeMillis());
            daoService.updateEntity(feeCode);

        }
        for(ApproveMessageEntity message:messages)
        {
            String mediaId = WeChatUtil.uploadImg2Wx(message.getImgUrl());
            message.setMediaId(mediaId);
            message.setLastUpdateTime(System.currentTimeMillis());
            daoService.updateEntity(message);

        }















    }












}
