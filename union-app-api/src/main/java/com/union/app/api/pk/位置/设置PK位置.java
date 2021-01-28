package com.union.app.api.pk.位置;

import com.union.app.domain.pk.ApproveButton;
import com.union.app.domain.pk.PkLocation;
import com.union.app.entity.pk.PkLocationEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
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
public class 设置PK位置 {


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

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/setLocation",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置PK位置(@RequestParam("pkId") String pkId,@RequestParam("name") String name,@RequestParam("city") String city,@RequestParam("desc") String desc,@RequestParam("cityCode") String cityCode,@RequestParam("latitude") String latitude,@RequestParam("longitude") String longitude,@RequestParam("userId") String userId) throws AppException, IOException {

        if(locationService.isPkCreator(pkId,userId))
        {
            appService.设置PK位置(pkId,name,desc,city,cityCode,latitude,longitude);
            PkLocationEntity locatin = appService.查询PK位置(pkId);
            return AppResponse.buildResponse(PageAction.执行处理器("success",locatin));
        }

        return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));







    }



}
