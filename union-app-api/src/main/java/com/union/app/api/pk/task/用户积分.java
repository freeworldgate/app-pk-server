package com.union.app.api.pk.task;

import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.wechat.UserInfo;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicItem;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 用户积分 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;


    @RequestMapping(path="/userIntegral",method = RequestMethod.GET)
    public AppResponse 用户积分(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {


        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setFollow(dynamicService.获取收藏积分(pkId,userId));
        userIntegral.setShare(dynamicService.获取今日分享积分(pkId,userId));

        if(userInfoService.用户是否具有有效收款码(pkId,userId))
        {
            userIntegral.setSort(Boolean.TRUE);
            userIntegral.setIndex(dynamicService.获取用户排名(pkId,userId) + 1);
        }
        else
        {
            userIntegral.setSort(Boolean.FALSE);
        }













        return AppResponse.buildResponse(PageAction.执行处理器("success",userIntegral));

    }



}
