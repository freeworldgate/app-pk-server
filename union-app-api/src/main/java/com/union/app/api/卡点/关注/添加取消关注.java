package com.union.app.api.卡点.关注;

import com.union.app.domain.pk.PkImage;
import com.union.app.domain.user.User;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
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


    @RequestMapping(path="/followUser",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 添加关注(@RequestParam("userId") String userId,@RequestParam("followerId") String followerId) throws AppException, IOException, InterruptedException {

        //创建者

        locationService.添加关注(userId,followerId);

        return AppResponse.buildResponse(PageAction.前端数据更新("followStatu",true));

    }


    @RequestMapping(path="/cancelFollow",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 取消关注(@RequestParam("userId") String userId,@RequestParam("followerId") String followerId) throws AppException, IOException, InterruptedException {

        //创建者

        locationService.取消关注(userId,followerId);

        return AppResponse.buildResponse(PageAction.前端数据更新("followStatu",false));

    }



}
