package com.union.app.api.pk.管理;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.ActiveTip;
import com.union.app.domain.pk.PkDetail;
import com.union.app.entity.pk.ActiveTipEntity;
import com.union.app.entity.pk.BackImgEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 审核Tip管理 {

    @Autowired
    AppService appService;

    @Autowired
    AppDaoService daoService;

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

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();




    @RequestMapping(path="/queryTips",method = RequestMethod.GET)
    public AppResponse 查询内置PK(@RequestParam("userId") String userId) throws AppException, IOException {

        List<ActiveTip> tips = appService.查询所有提示信息();





        return AppResponse.buildResponse(PageAction.前端数据更新("tips",tips));

    }
    @RequestMapping(path="/addTip",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询内置PK(@RequestParam("userId") String userId,@RequestParam("tip") String tip) throws AppException, IOException {

        appService.添加Tip(tip);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));



    }



    @RequestMapping(path="/removeTip",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removeImg(@RequestParam("userId") String userId,@RequestParam("id") String id) throws AppException, IOException {

        appService.删除Tip(id);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));



    }







}
