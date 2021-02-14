package com.union.app.api.卡点.用户背景;

import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.domain.pk.名片.UserCard;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
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
public class 更新用户背景 {


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
    LocationService locationService;

    @Autowired
    FindService findService;

    @Autowired
    UserDynamicService userDynamicService;

    @RequestMapping(path="/updateUserBack",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse queryUserFind(@RequestParam("targetUserId") String targetUserId,@RequestParam("userId") String userId,@RequestParam("url") String url) throws IOException {
        if(StringUtils.equals(targetUserId,userId))
        {
            userDynamicService.修改背景(targetUserId,url);
            return AppResponse.buildResponse(PageAction.执行处理器("success",""));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法用户操作","非法用户操作"));
        }



    }


}
