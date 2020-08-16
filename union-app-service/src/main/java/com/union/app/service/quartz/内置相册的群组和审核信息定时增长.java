package com.union.app.service.quartz;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.pk.HomePagePk;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkType;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class 内置相册的群组和审核信息定时增长 {



    @Autowired
    DynamicService dynamicService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    RedisMapService redisMapService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "* * * * */1 ?") // 每小时执行一次，更新群组以及审核信息
    public void work() throws Exception {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkType",CompareTag.Equal,PkType.内置相册);
        List<HomePagePk> pks = daoService.queryEntities(HomePagePk.class,filter);





    }












}
