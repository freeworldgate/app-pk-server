package com.union.app.api.卡点.实时任务;


import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.city.CityEntity;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.pk.service.PkService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class 同步城市新创建卡点数量
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
    @Scheduled(cron = "0 */1 * * * ?") //每天5点执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() {
        String cityCode = "";
        while(StringUtils.isNotBlank(cityCode = keyService.获取待同步卡点数量的城市() ))
        {
            long pks = keyService.queryKey(cityCode,KeyType.城市卡点数量);
            Map<String,Object> map = new HashMap<>();
            map.put("pks",pks);
            appDao.updateColumById(CityEntity.class,"cityCode",Integer.valueOf(cityCode),map);
        }
        System.out.println("结束定时线程:"+Thread.currentThread().getId());

    }





}
