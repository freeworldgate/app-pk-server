package com.union.app.api.pk.管理.预置用户;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.user.User;
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
    DynamicService dynamicService;

    @Autowired
    KeyService keyService;
    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();




    @RequestMapping(path="/addPreUser",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 创建预置用户(@RequestParam("name") String name,@RequestParam("imgUrl") String imgUrl,@RequestParam("userId") String userId) throws AppException, IOException {
        appService.checkManager(userId);
        User user = appService.新增内置用户(name,imgUrl);

        return AppResponse.buildResponse(PageAction.执行处理器("success",user));

    }


    @RequestMapping(path="/editUserName",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 修改预置用户(@RequestParam("name") String name,@RequestParam("id") String id,@RequestParam("userId") String userId) throws AppException, IOException {
        appService.checkManager(userId);
        User user = appService.修改内置用户名称(id,name);
        keyService.刷新用户User缓存(user.getUserId());
        return AppResponse.buildResponse(PageAction.执行处理器("success",user));

    }
    @RequestMapping(path="/editUserImg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 修改预置用户头像(@RequestParam("imgUrl") String imgUrl,@RequestParam("id") String id,@RequestParam("userId") String userId) throws AppException, IOException {
        appService.checkManager(userId);
        User user = appService.修改内置用户头像(id,imgUrl);
        keyService.刷新用户User缓存(user.getUserId());
        return AppResponse.buildResponse(PageAction.执行处理器("success",user));

    }


}
