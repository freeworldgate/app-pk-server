package com.union.app.service.quartz;

import com.alibaba.fastjson.JSON;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.service.pk.dynamic.DynamicItem;
import com.union.app.service.pk.dynamic.DynamicKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class TEST {



    @Autowired
    DynamicService dynamicService;



    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "*/1 * * * * ?") // 每分钟执行一次  刷新整个PKID的所有PAGE
    public void work() throws Exception {







        for(int i=0;i<10;i++){
            FactualInfo factualInfo = new FactualInfo();
            factualInfo.setFactualId(UUID.randomUUID().toString());
            factualInfo.setUser(RandomUtil.getRandomUser());
            factualInfo.setOperType(new KeyNameValue(1,"上传收款码"));
            factualInfo.setTime(RandomUtil.getRandomDate());
            dynamicService.添加动态("PK01",factualInfo);
            dynamicService.更新今日用户排名("PK01",RandomUtil.getRandomUser().getUserId());
        }











        List<FactualInfo> factualInfos = dynamicService.获取当前PK操作动态("PK01");
        for(FactualInfo factualInfo1:factualInfos){
            System.out.println("factualInfo=" + factualInfo1.toString());
        }





    }












}
