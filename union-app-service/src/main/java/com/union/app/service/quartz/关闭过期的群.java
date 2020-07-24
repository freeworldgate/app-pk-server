package com.union.app.service.quartz;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.entity.pk.GroupStatu;
import com.union.app.entity.pk.PkCashierFeeCodeEntity;
import com.union.app.entity.pk.PkCashierGroupEntity;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.MediaService;
import com.union.app.service.pk.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class 关闭过期的群 {



    @Autowired
    DynamicService dynamicService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    MediaService mediaService;

    @Autowired
    AppService appService;

    @Scheduled(cron = "0 0 4 * * ?") // 每天凌晨4点钟执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() throws Exception {

        List<PkCashierGroupEntity> groups = appService.查询过期的群组();

        for(PkCashierGroupEntity group:groups)
        {
            group.setStatu(GroupStatu.停用);
            daoService.updateEntity(group);
        }















    }












}
