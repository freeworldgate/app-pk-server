package com.union.app.api.卡点.实时任务;


import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.entity.pk.PkEntity;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.pk.service.PkService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class 同步Pk人数到PkEntity表
{

    @Autowired
    AppDaoService appDao;

    @Autowired
    PkService pkService;

    @Autowired
    KeyService keyService;


    /**
     * 附近卡点的排序是按照PKEntity表的totalUser字段来排序的。所以要定时排序
     */
    @Scheduled(cron = "0 1 * * * ?") //每天5点执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() {

        for(int i=0;i<1000;i++)
        {
            String pkId = keyService.获取同步PK(KeyType.要同步的PK列表);
            if(!StringUtils.isBlank(pkId))
            {
                long totalUser = keyService.queryKey(pkId,KeyType.卡点人数);
                long totalImgs = keyService.queryKey(pkId,KeyType.PK图片总量);
                PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
                pkEntity.setTotalUsers(totalUser);
                pkEntity.setTotalImgs(totalImgs);
                appDao.updateEntity(pkEntity);
            }

        }




    }





}
