package com.union.app.api.pk.管理.卡点排行榜;

import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.pk.service.文字背景.TextService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 设置卡点范围 {

    @Autowired
    AppService appService;

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
    FindService findService;

    @Autowired
    TextService textService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/setPkRange",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setPkRange(@RequestParam("radius") int radius,@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException {
        appService.checkManager(userId);
        locationService.设置卡点范围(pkId,radius);
        return AppResponse.buildResponse(PageAction.信息反馈框("卡点范围已更新","卡点范围已更新"));

    }
    @RequestMapping(path="/lockRange",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse lockRange(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException {
        appService.checkManager(userId);
        boolean lock = locationService.锁定或者解锁Range(pkId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",lock));

    }




}
