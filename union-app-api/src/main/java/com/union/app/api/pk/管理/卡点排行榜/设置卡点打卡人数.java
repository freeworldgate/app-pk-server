package com.union.app.api.pk.管理.卡点排行榜;

import com.union.app.common.dao.KeyService;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.pk.service.文字背景.TextService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path="/pk")
public class 设置卡点打卡人数 {

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
    KeyService keyService;

    @RequestMapping(path="/setUserNumbers",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setPkRange(@RequestParam("pkId") String pkId,@RequestParam("value") int value,@RequestParam("userId") String userId) throws AppException {

        appService.checkManager(userId);
        keyService.setMapKey(pkId, KeyType.卡点人数,value);
        keyService.通知同步图片和用户数量(pkId);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }





}
