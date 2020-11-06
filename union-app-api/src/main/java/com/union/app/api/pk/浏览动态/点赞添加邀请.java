package com.union.app.api.pk.浏览动态;

import com.union.app.entity.pk.PkLocationEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class 点赞添加邀请 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    AppService appService;

    @RequestMapping(path="/addGap",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置PK位置(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("gap") int tag) throws AppException, IOException {
        if(tag == 1)
        {
            //点赞
            pkService.greate(pkId,userId);

        }


        if(tag == 3)
        {
            //邀请
            appService.添加邀请(pkId,userId);
            List<DataSet> dataSets = new ArrayList<>();
            dataSets.add(new DataSet("hasInivte",true));
            dataSets.add(new DataSet("inviteStatu",appService.查询状态(pkId,userId,3)));
            return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

        }

        return AppResponse.buildResponse(PageAction.前端数据更新("key","key"));







    }



}
