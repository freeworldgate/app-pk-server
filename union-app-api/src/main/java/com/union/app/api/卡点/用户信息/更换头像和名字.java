package com.union.app.api.卡点.用户信息;

import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.domain.pk.名片.UserCard;
import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 更换头像和名字 {


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
    LocationService locationService;

    @Autowired
    FindService findService;

    @RequestMapping(path="/setUserImg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse queryUserFind(@RequestParam("userId") String userId,@RequestParam("imgUrl") String imgUrl) {

        userService.更换头像(userId,imgUrl);
        User user = userService.queryUser(userId);
        return AppResponse.buildResponse(PageAction.前端数据更新("creator",user));

    }

    @RequestMapping(path="/setUserName",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setUserName(@RequestParam("userId") String userId,@RequestParam("userName") String userName) {

        userService.更换昵称(userId,userName);
        User user = userService.queryUser(userId);
        return AppResponse.buildResponse(PageAction.前端数据更新("creator",user));

    }
}
