package com.union.app.api.share;

import com.union.app.domain.pk.apply.ApplyOrder;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
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

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 判断是否可以创建审核订单 {


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
    OrderService orderService;

    @Autowired
    UserInfoService userInfoService;





    @RequestMapping(path="/applyOrder",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse getApplyOrder(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        String creatorId = pkService.querySinglePkEntity(pkId).getUserId();
        return AppResponse.buildResponse(PageAction.执行处理器("createPayOrder",creatorId));


//        String creatorId = pkService.querySinglePkEntity(pkId).getUserId();
//        ApplyOrder applyOrder = userInfoService.查询或创建订单(pkId,userId,creatorId);


    }



}
