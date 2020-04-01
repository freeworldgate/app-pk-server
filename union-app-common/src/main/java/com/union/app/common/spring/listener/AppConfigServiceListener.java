package com.union.app.common.spring.listener;


import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class AppConfigServiceListener implements ApplicationListener<ContextRefreshedEvent>
{

    @Autowired
    AppDaoService appDao;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        insertConfig("name1","value1","配置");
        insertConfig("name2","value2","配置");
        insertConfig("name3","1","配置");
        insertConfig("name4","2","配置");
        insertConfig("name5",String.valueOf(Boolean.TRUE),"配置");
        insertConfig("name6",String.valueOf(Boolean.FALSE),"配置");

        System.out.println("查询配置 name1: " + AppConfigService.getConfigAsString("name1","valueDefault1"));
        System.out.println("查询配置 name2: " + AppConfigService.getConfigAsString("name2","valueDefault1"));
        System.out.println("查询配置 name3: " + AppConfigService.getConfigAsInteger("name3",1000));
        System.out.println("查询配置 name4: " + AppConfigService.getConfigAsInteger("name4",1000));
        System.out.println("查询配置 name5: " + AppConfigService.getConfigAsBoolean("name5",Boolean.FALSE));
        System.out.println("查询配置 name6: " + AppConfigService.getConfigAsBoolean("name6",Boolean.TRUE));





    }

    public void insertConfig(String configName,String configValue,String desc)
    {
        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(ConfigEntity.class).compareFilter("configName",CompareTag.Equal,configName);
        List<ConfigEntity> configs = appDao.queryEntities(ConfigEntity.class,entityFilterChain);
        if(configs.isEmpty()) {
            ConfigEntity configEntity = new ConfigEntity();
            configEntity.setColumSwitch(ColumSwitch.ON);
            configEntity.setConfigDesc(desc);
            configEntity.setConfigName(configName);
            configEntity.setConfigValue(configValue);
            configEntity.setCreateTime(Calendar.getInstance().getTime());
            configEntity.setDefaultValue(configValue);
            appDao.insertEntity(configEntity);
        }
    }



}
