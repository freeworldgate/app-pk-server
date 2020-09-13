package com.union.app.common.spring.listener;


import com.union.app.common.dao.AppDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

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
