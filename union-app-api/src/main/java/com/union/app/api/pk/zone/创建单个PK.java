package com.union.app.api.pk.zone;

import com.alibaba.fastjson.JSON;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.OSS存储.SceneType;
import com.union.app.domain.pk.PkDetail;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
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
import java.util.UUID;

@RestController
@RequestMapping(path="/pk")
public class 创建单个PK {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    UserService userService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;


    @Autowired
    AppService appService;

    @RequestMapping(path="/createPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 创建单个PK(@RequestParam("userId") String userId,@RequestParam("topic") String topic,@RequestParam("watchWord") String watchWord,@RequestParam("invite") boolean invite) throws AppException, IOException {


        String pkId = pkService.创建PK(userId,topic,watchWord,invite);

        PkDetail pkDetail = pkService.querySinglePk(pkId);
        appService.vip包装(pkDetail,userId,"");

        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetail));
    }


}
