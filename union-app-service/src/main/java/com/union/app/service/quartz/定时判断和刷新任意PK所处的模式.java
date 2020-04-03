package com.union.app.service.quartz;

import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Component
public class 定时判断和刷新任意PK所处的模式 {



    @Autowired
    DynamicService dynamicService;



    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "*/5 * * * * ?") // 每分钟执行一次  刷新整个PKID的所有PAGE
    public void work() throws Exception {

        //TODO 动态信息  和  任务;

        //TODO 定时计算和刷新 PK 所处的模式

        String pkId = dynamicService.获取需要变更模式的PKID();
        if(StringUtils.isBlank(pkId)){return;}
        if(dynamicService.isInTask(pkId)){
            //生成任务
            dynamicService.生成PK打赏任务(pkId);
            //任务模式
            dynamicService.修改PK模式(pkId,1);

        }
        else
        {
            dynamicService.清理所有任务(pkId);

            dynamicService.修改PK模式(pkId,0);

        }










    }












}
