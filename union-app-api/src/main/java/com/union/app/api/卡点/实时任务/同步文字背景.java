package com.union.app.api.卡点.实时任务;


import com.union.app.common.dao.AppDaoService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.文字背景.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class 同步文字背景
{

    @Autowired
    AppDaoService appDao;

    @Autowired
    PkService pkService;

    @Autowired
    TextService textService;


    /**
     * 定时更新用户打卡的背景色列表
     */
    @Scheduled(cron = "5 * * * * ?") //每天5点执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() {

        textService.更新背景列表();



    }





}
