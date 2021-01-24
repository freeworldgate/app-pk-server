package com.union.app.listener;


import com.union.app.common.dao.AppDaoService;
import com.union.app.entity.pk.社交.PkGroupEntity;
import com.union.app.entity.pk.社交.PkGroupMemberEntity;
import com.union.app.entity.用户.UserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.GroupService;
import com.union.app.service.pk.service.LockService;
import com.union.app.service.pk.service.LockType;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class AppDaoServiceListener implements ApplicationListener<ContextRefreshedEvent>,Runnable
{

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    AppDaoService daoService;

    @Autowired
    LockService lockService;


    @Autowired
    UserDynamicService userDynamicService;

    @SneakyThrows
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
//
//        for(int i=0;i<1000;i++)
//        {
//            String userId = "USER"+i;
//            UserDynamicEntity userDynamicEntity = userDynamicService.queryUserDynamicEntity(userId);
//            if(ObjectUtils.isEmpty(userDynamicEntity))
//            {
//                userDynamicService.创建Dynamic表(userId);
//            }
////            daoService.insertEntity(userDynamicEntity);
//        }








//        Thread.sleep(1000*5);


//        RestTemplate restTemplate = new RestTemplate();
//
//        Map<String, String> uriVariables = new HashMap<>();
//        uriVariables.put("userId","ozm2e4r6IgyVMlA6goFt7AbB3wVw");
//        uriVariables.put("followerId","U1");
//        ResponseEntity<String> res = restTemplate.getForEntity("http://127.0.0.1:8080/pk/followUser",String.class,uriVariables);
//        System.out.println(res.getBody());

        for(int i=0;i<1;i++) {
//            updateMembers();
//            Thread thread = new Thread(new AppDaoServiceListener());
//            thread.start();
        }



    }
    public void updateMembers(){

//        for(int i=0;i<100;i++){
//            if(lockService.getLock(String.valueOf("1187"), LockType.群组锁)){
//
//                PkGroupEntity pkGroupEntity = groupService.查询用户群组EntityById(1187);
//
//                pkGroupEntity.setMembers(pkGroupEntity.getMembers()+1);
//                daoService.updateEntity(pkGroupEntity);
//
//                //释放锁
//                lockService.releaseLock(String.valueOf("1187"), LockType.群组锁);
//            }
//            else
//            {
//                System.out.println("异常...");
//            }
//
//        }

    }


    @Override
    public void run() {
        updateMembers();
    }
}
