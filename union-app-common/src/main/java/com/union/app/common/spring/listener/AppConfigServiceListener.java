package com.union.app.common.spring.listener;


import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
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










    }





}
