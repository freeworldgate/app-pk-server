package com.union.app.api.卡点.充值;

import com.union.app.domain.pk.捞人.CreateUserFind;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path="/pk")
public class 充值 {


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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    ComplainService complainService;

    @Autowired
    PayService payService;

    @Autowired
    FindService findService;

    @RequestMapping(path="/payForTime",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse payForTime(@RequestParam("userId") String userId) {

        //校验


        payService.充值时间(1,userId);




        return AppResponse.buildResponse(PageAction.前端数据更新("",""));

    }




    @RequestMapping(path="/payForPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse payForPk(@RequestParam("userId") String userId) {

        //校验


        payService.充值Pk(1,userId);




        return AppResponse.buildResponse(PageAction.前端数据更新("",""));

    }






}
