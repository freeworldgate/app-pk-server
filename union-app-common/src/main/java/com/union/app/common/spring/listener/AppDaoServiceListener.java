package com.union.app.common.spring.listener;


import com.union.app.dao.spi.AppDaoService;
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
//
//        TypedQuery<AppUser> query = entityManager.createQuery("select p from AppUser p ",AppUser.class);
//
//        List<AppUser> appUserList = query.getResultList();
//
//        for(AppUser appUser:appUserList){
//            System.out.println(appUser.toString());
//        }
//        String[] values = new String[]{"bb0f2010-6d90-4833-a36d-6183b1e32972","de97d337-71ed-4e76-849a-451e8815daae"};
//
//        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(AppUser.class)
//                .orderByFilter("age",OrderTag.ASC)
//                .compareFilter("time",CompareTag.Small,Calendar.getInstance().getTime()).andFilter()
//                .nullFilter("address",false);
//
//

//        List<AppUser> appUsers = appDao.queryEntities(AppUser.class,entityFilterChain);
//
//        for(AppUser appUser:appUsers){
//            System.out.println(appUser.toString());
//        }
//
//        for(int i=0;i<100;i++)
//        {
//            AppUser appUser = new AppUser();
////          appDao.insertEntity(appUser);
//        }


        System.out.println("--------------------finish---------------------");


//      Map<String, Object> apiServices = event.getApplicationContext().getBeansWithAnnotation(ApiService.class);
//      for(Map.Entry<String,Object> entry:apiServices.entrySet())
//        {
//            ClassEntity classEntity = EntityAnalyser.analyserClass(entry.getKey().getClass());
//        }


    }
}
