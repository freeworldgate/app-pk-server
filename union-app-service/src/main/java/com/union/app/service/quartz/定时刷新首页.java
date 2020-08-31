package com.union.app.service.quartz;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.pk.HomePagePk;
import com.union.app.entity.pk.PkType;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Component
public class 定时刷新首页 implements ApplicationListener<ContextRefreshedEvent>{



    @Autowired
    PkDataService pkDataService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    RedisMapService redisMapService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "* */20 * * * ?") // 每小时执行一次，更新群组以及审核信息
    public void work() throws Exception {



        pkDataService.更新相册列表();









    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            pkDataService.更新相册列表();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
