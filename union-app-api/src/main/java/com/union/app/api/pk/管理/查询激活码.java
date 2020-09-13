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
public class 查询激活码 {

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




    @RequestMapping(path="/queryActiveCode",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse activeCode(@RequestParam("password") String password,@RequestParam("applyCode") String applyCode) throws AppException, IOException {

        String code = appService.获取收款码(applyCode);

        return AppResponse.buildResponse(PageAction.前端数据更新("code",code));

    }
    @RequestMapping(path="/gennerateCodes",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse gennerateCode(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId) throws AppException, IOException {

        appService.生成激活码(cashierId);
        appService.新增储备激活码(cashierId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }



}
