package com.union.app.service.quartz;

import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkMode;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Component
public class 定时判断和刷新任意PK所处的模式 {



    @Autowired
    DynamicService dynamicService;



    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "* */50 * * * ?") // 每分钟执行一次  刷新整个PKID的所有PAGE
    @Transactional(rollbackOn = Exception.class)
    public void work() throws Exception {



//        dynamicService.设置周期最大值();



    }













}
