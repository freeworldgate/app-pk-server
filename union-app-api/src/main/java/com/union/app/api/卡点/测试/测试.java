package com.union.app.api.卡点.测试;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.spring.context.SpringContextUtil;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.IdGen.IdService;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.RedisService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(path="/pk")
public class 测试 {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;


    @Autowired
    AppService appService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    LocationService locationService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    LockService lockService;

    @Autowired
    IdService idService;



    @RequestMapping(path="/redis",method = RequestMethod.GET)
    public void test111() {

        Map<String,String> map = new ConcurrentHashMap<>();
        for(int i=0;i<1000;i++)
        {

            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    for(int i=0;i<1000;i++){
                        long value = idService.gennerateSortId();
                        map.put(String.valueOf(value),"AAA");
                        System.out.println("测试值value = " + value);
                    }
                    Thread.sleep(5000);
                    System.out.println("map大小:"+map.size());
                }
            }).start();
        }



    }

    @RequestMapping(path="/test",method = RequestMethod.GET)
    public void test() {

        Map<String, Object> uriVariables = new HashMap<>();
//
//        uriVariables.put("userId","ozm2e4r6IgyVMlA6goFt7AbB3wVw");
//        uriVariables.put("latitude",31.904063313802084D);
//        uriVariables.put("longitude",118.9038058810764D);
        for(int i=0;i<50;i++)
        {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<100;i++){

                        RestUtil restUtil = SpringContextUtil.getBean(RestUtil.class);
                        String res = restUtil.get("https://www.211shopper.com/pk/queryHomePage?userId=ozm2e4r6IgyVMlA6goFt7AbB3wVw&latitude=31.904063313802084&longitude=118.9038058810764",uriVariables);
                        System.out.println("Thread-"+Thread.currentThread().getName()+" res = " + res);
                    }
                }
            }).start();
        }



    }

    @RequestMapping(path="/api",method = RequestMethod.GET)
    public void apiTest() {

        Map<String, Object> uriVariables = new HashMap<>();
        Stack<Integer> stack = new Stack<>();

        for(int i=0;i<100;i++)
        {
            stack.push(i);

        }
        for(int i=0;i<100;i++)
        {
            int value = stack.pop();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RestUtil restUtil = SpringContextUtil.getBean(RestUtil.class);
                    String res = restUtil.get("http://localhost:8080/pk/test11?start="+ value*30 +"&end="+( value+1)*30,uriVariables);
                    System.out.println(" res = " + res);
                }
            }).start();
        }


    }

    @RequestMapping(path="/test11",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public void test(@RequestParam("start") int start,@RequestParam("end") int end) throws AppException {

        String userId = "ozm2e4r6IgyVMlA6goFt7AbB3wVw";
        for(int i=0;i<100;i++) {
            if (lockService.getLock(userId, LockType.用户粉丝数量)) {
                UserDynamicEntity userDynamicEntity = userDynamicService.queryUserDynamicEntity(userId);
                userDynamicEntity.setFans(userDynamicEntity.getFans()+1);
                daoService.updateEntity(userDynamicEntity);
//                daoService.updateColumById(UserDynamicEntity.class, "userId", userDynamicEntity.getUserId(), "fans", userDynamicEntity.getFans() + 1);

                lockService.releaseLock(userId, LockType.用户粉丝数量);
            } else {
                throw AppException.buildException(PageAction.信息反馈框("系统错误", "系统错误"));
            }
        }




//        Map<String, Object> uriVariables = new HashMap<>();
//
//        new Thread(new Runnable() {
//            @SneakyThrows
//            @Override
//            public void run() {
//
//                for(int i=start;i<end;i++)
//                {
//
////                        String userId = "ozm2e4r6IgyVMlA6goFt7AbB3wVw";
////
////                        if(lockService.getLock(userId, LockType.用户粉丝数量)) {
////                            UserDynamicEntity userDynamicEntity = userDynamicService.queryUserDynamicEntity(userId);
////
////                            daoService.updateColumById(UserDynamicEntity.class,"userId",userDynamicEntity.getUserId(),"fans",userDynamicEntity.getFans()+1);
////
////                            lockService.releaseLock(userId,LockType.用户粉丝数量);
////                        }
////                        else
////                        {
////                            throw AppException.buildException(PageAction.信息反馈框("系统错误","系统错误"));
////                        }
//
////                    try {
////                        final int s = i;
////                        RestUtil restUtil = SpringContextUtil.getBean(RestUtil.class);
////                        String res = restUtil.get("http://localhost:8080/pk/followUser?followerId=ozm2e4r6IgyVMlA6goFt7AbB3wVw&userId=USER" + s, uriVariables);
////                        System.out.println("Thread = " + Thread.currentThread().getName() + " res = " + res);
////                    }catch (Exception e)
////                    {
////                        e.printStackTrace();
////
////                    }
//                }
//
//            }
//        }).start();
//


    }


}
