package com.union.app.api.卡点;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.daka.CreateLocation;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 修改签名 {


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
    LocationService locationService;

    @Autowired
    PayService payService;

    @RequestMapping(path="/changeSign",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse buildPk(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("sign") String sign) throws AppException, IOException, InterruptedException {

        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.信息反馈框("非法操作","非法操作!"));
        }
        locationService.修改签名(pkId,sign);


        return AppResponse.buildResponse(PageAction.执行处理器("success",null));

    }


}
