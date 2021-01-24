package com.union.app.api.卡点.关注;

import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path="/pk")
public class 添加取消关注 {


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
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    LocationService locationService;


    @RequestMapping(path="/followUser",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 添加关注(@RequestParam("userId") String userId,@RequestParam("followerId") String followerId) throws AppException {

        //创建者
        try {
            locationService.添加关注(userId, followerId);
            //无并发场景
            userDynamicService.用户关注数量加一(userId);
            //高并发场景
            userDynamicService.用户粉丝数量加一(followerId);
            return AppResponse.buildResponse(PageAction.前端数据更新("followStatu",true));
        }catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }



    }


    @RequestMapping(path="/cancelFollow",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 取消关注(@RequestParam("userId") String userId,@RequestParam("followerId") String followerId) throws AppException{

        //创建者

        locationService.取消关注(userId,followerId);
        userDynamicService.用户关注数量减一(userId);
        userDynamicService.用户粉丝数量减一(followerId);
        return AppResponse.buildResponse(PageAction.前端数据更新("followStatu",false));

    }



}
