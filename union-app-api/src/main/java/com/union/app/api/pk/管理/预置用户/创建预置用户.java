package com.union.app.api.pk.管理.预置用户;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.entity.pk.PreUserEntity;
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
public class 创建预置用户 {

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




    @RequestMapping(path="/addPreUser",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 创建预置用户(@RequestParam("password") String password,@RequestParam("name") String name,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {
        appService.验证Password(password);
        PreUserEntity user = appService.新增内置用户(name,imgUrl);
//        PreUserEntity preUserEntity = new PreUserEntity();
//        preUserEntity.setUserId(user.getUserId());
//        preUserEntity.setUserName(name);
//        preUserEntity.setImgUrl(user.getImgUrl());

        return AppResponse.buildResponse(PageAction.执行处理器("success",user));



    }


    @RequestMapping(path="/editUserName",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 修改预置用户(@RequestParam("password") String password,@RequestParam("name") String name,@RequestParam("id") String id) throws AppException, IOException {

        PreUserEntity user = appService.修改内置用户名称(id,name);
//        PreUserEntity preUserEntity = new PreUserEntity();
//        preUserEntity.setUserId(user.getUserId());
//        preUserEntity.setUserName(name);
//        preUserEntity.setImgUrl(user.getImgUrl());

        return AppResponse.buildResponse(PageAction.执行处理器("success",user));

    }
    @RequestMapping(path="/editUserImg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 修改预置用户头像(@RequestParam("password") String password,@RequestParam("imgUrl") String imgUrl,@RequestParam("id") String id) throws AppException, IOException {

        PreUserEntity user = appService.修改内置用户头像(id,imgUrl);
//        PreUserEntity preUserEntity = new PreUserEntity();
//        preUserEntity.setUserId(user.getUserId());
//        preUserEntity.setUserName(user.getUserName());
//        preUserEntity.setImgUrl(user.getImgUrl());

        return AppResponse.buildResponse(PageAction.执行处理器("success",user));

    }


}
