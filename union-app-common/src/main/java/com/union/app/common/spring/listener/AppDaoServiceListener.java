package com.union.app.common.spring.listener;


import com.union.app.common.dao.AppDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class AppDaoServiceListener implements ApplicationListener<ContextRefreshedEvent>
{

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    AppDaoService appDao;



    @Override
    @Transactional(rollbackOn = Exception.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
