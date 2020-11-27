package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
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
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 添加相册到主页预览 {

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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();



    @RequestMapping(path="/addToGeneticHome",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse addToGeneticHome(@RequestParam("pkId") String pkId,@RequestParam("value") int value,@RequestParam("password") String password) throws AppException, IOException {
        appService.验证Password(password);
        appService.添加到主页预览(pkId,value,1);
        PkDetail pkDetail = pkService.querySinglePk(pkId);
//        pkDetail.setGeneticPriority(appService.查询优先级(pkId,1));
//        pkDetail.setNonGeneticPriority(appService.查询优先级(pkId,2));
//
        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetail));
    }

    @RequestMapping(path="/addToNonGeneticHome",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse addToNonGeneticHome(@RequestParam("pkId") String pkId,@RequestParam("value") int value,@RequestParam("password") String password) throws AppException, IOException {
        appService.验证Password(password);
        appService.添加到主页预览(pkId,value,2);
        PkDetail pkDetail = pkService.querySinglePk(pkId);
//        pkDetail.setGeneticPriority(appService.查询优先级(pkId,1));
//        pkDetail.setNonGeneticPriority(appService.查询优先级(pkId,2));

        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetail));
    }




    @RequestMapping(path="/removePkFromHomPage",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removePkFromHomPage(@RequestParam("pkId") String pkId,@RequestParam("password") String password,@RequestParam("type") int type) throws AppException, IOException {
        appService.验证Password(password);
        appService.移除主页预览(pkId,type);



        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }



}
