package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.ActiveTip;
import com.union.app.domain.pk.PkDetail;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 标签管理 {

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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();




    @RequestMapping(path="/queryTips",method = RequestMethod.GET)
    public AppResponse 查询内置PK(@RequestParam("password") String password) throws AppException, IOException {
        appService.验证Password(password);

        List<ActiveTip> tips = appService.查询所有标签信息();





        return AppResponse.buildResponse(PageAction.前端数据更新("tips",tips));

    }
    @RequestMapping(path="/addTip",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询内置PK(@RequestParam("password") String password,@RequestParam("tip") String tip) throws AppException, IOException {
        appService.验证Password(password);

        appService.添加Tip(tip);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));



    }



    @RequestMapping(path="/removeTip",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removeImg(@RequestParam("password") String password,@RequestParam("id") String id) throws AppException, IOException {
        appService.验证Password(password);

        appService.删除Tip(id);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));



    }







}