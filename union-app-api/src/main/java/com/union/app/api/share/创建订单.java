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
public class 创建订单{


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


    @RequestMapping(path="/queryCreateOrder",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse getApplyOrder(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("cashierId") String cashierId) throws AppException, IOException {
        ApplyOrder applyOrder = userInfoService.查询或创建订单(pkId,userId,cashierId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",applyOrder));
    }




    @RequestMapping(path="/setOrderCut",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setOrderCut(@RequestParam("orderId") String orderId,@RequestParam("userId") String userId,@RequestParam("url") String url) throws AppException, IOException {
        userInfoService.设置订单截图(orderId,userId,url);
        ApplyOrder applyOrder = userInfoService.查询订单ById(orderId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",applyOrder));
    }

    @RequestMapping(path="/orderConfirm",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse orderConfirm(@RequestParam("orderId") String orderId,@RequestParam("userId") String userId) throws AppException, IOException
    {
        userInfoService.确定创建订单(orderId,userId);
        ApplyOrder applyOrder = userInfoService.查询订单ById(orderId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",applyOrder));
    }

}
