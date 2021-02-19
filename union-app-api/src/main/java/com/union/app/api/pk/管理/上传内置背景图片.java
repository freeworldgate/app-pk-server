package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkDetail;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 上传内置背景图片 {

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

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();




    @RequestMapping(path="/uploadBackImg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询内置PK(@RequestParam("type") int type,@RequestParam("imgUrl") String imgUrl,@RequestParam("userId") String userId) throws AppException, IOException {

        appService.checkManager(userId);

        BackImgEntity pk = appService.设置内置背景图片(type,imgUrl);


        return AppResponse.buildResponse(PageAction.执行处理器("success",pk));



    }



    @RequestMapping(path="/removeImg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removeImg(@RequestParam("userId") String userId,@RequestParam("id") String id) throws AppException, IOException {

        appService.checkManager(userId);

        appService.删除内置背景图片(id);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));



    }




}
