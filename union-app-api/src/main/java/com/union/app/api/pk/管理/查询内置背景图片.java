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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 查询内置背景图片 {

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




    @RequestMapping(path="/queryBackImgs",method = RequestMethod.GET)
    public AppResponse 查询内置PK(@RequestParam("type") int type,@RequestParam("userId") String userId) throws AppException, IOException {
        appService.checkManager(userId);

        List<BackImgEntity> pks = appService.查询内置背景图片(1,type);


        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));



    }

    @RequestMapping(path="/moreBackImgs",method = RequestMethod.GET)
    public AppResponse 查询内置PK(@RequestParam("page") int page,@RequestParam("type") int type,@RequestParam("userId") String userId) throws AppException, IOException {

        appService.checkManager(userId);

        List<BackImgEntity> pks = appService.查询内置背景图片(page+1,type);


        if(pks.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }




}
