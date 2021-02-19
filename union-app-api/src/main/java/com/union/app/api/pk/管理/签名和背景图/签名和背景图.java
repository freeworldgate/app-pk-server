package com.union.app.api.pk.管理.签名和背景图;

import com.union.app.domain.pk.PkImage;
import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
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
public class 签名和背景图 {


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
    AppService appService;

    @Autowired
    LocationService locationService;



    @RequestMapping(path="/deletePkImagesByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse deletePkImages(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("imageId") String imageId) throws AppException, IOException, InterruptedException {

        appService.checkManager(userId);
        locationService.删除卡点图片(pkId,userId,imageId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }
    @RequestMapping(path="/setPkBackByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setPkBack(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("imageId") String imageId) throws AppException, IOException, InterruptedException {
        appService.checkManager(userId);
        //创建者
        locationService.设置卡点背景图片(pkId,userId,imageId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }



    @RequestMapping(path="/changeSignByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse buildPk(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("sign") String sign) throws AppException, IOException, InterruptedException {
        appService.checkManager(userId);
        locationService.修改签名(pkId,sign);


        return AppResponse.buildResponse(PageAction.执行处理器("success",null));

    }
}
