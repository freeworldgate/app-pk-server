package com.union.app.api.share;

import com.union.app.domain.pk.apply.ApplyOrder;
import com.union.app.domain.pk.apply.ApproveCode;
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
public class 榜主审核收款码 {


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



    @RequestMapping(path="/approveUserCode",method = RequestMethod.GET)
    public AppResponse getApplyOrder(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {


        ApproveCode approveCode = userInfoService.查询不同类型用户收款码(pkId,userId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",approveCode));
    }


    @RequestMapping(path="/approveOrderConfirm1",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse cashierOrderConfirm1(@RequestParam("dynamicId") String dynamicId,@RequestParam("userId") String userId) throws AppException, IOException {


        userInfoService.审核通过(dynamicId,userId);
        ApproveCode approveCode = userInfoService.查询ApproveCodeById(dynamicId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",approveCode));
    }

    @RequestMapping(path="/approveOrderConfirm2",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse cashierOrderConfirm2(@RequestParam("dynamicId") String dynamicId,@RequestParam("userId") String userId) throws AppException, IOException {

        userInfoService.审核不通过(dynamicId,userId);
        ApproveCode approveCode = userInfoService.查询ApproveCodeById(dynamicId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",approveCode));
    }



}
