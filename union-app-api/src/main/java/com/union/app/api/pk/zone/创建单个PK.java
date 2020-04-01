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
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @RequestMapping(path="/createPk",method = RequestMethod.GET)
    public AppResponse 创建单个PK() throws AppException, IOException {
        redisStringUtil.test();


        PkDetail pkDetail = new PkDetail();

        pkDetail.setPkId("PK01");
        pkDetail.setTopic("过了年");
        pkDetail.setWatchWord("坎坷的回乡之旅!");
        pkDetail.setUser(userService.queryUser("ozm2e4r6IgyVMlA6goFt7AbB3wVw"));


        String jsonStr = JSON.toJSONString(userService.queryUser("ozm2e4r6IgyVMlA6goFt7AbB3wVw"));
        System.out.println("JSON:" + jsonStr);

        ossStorage.setOssWidthMapCache(SceneType.存储PK缓存,"PK-Cache",pkDetail.getPkId(),pkDetail);

        return AppResponse.buildResponse(PageAction.信息反馈框("",""));
    }


}
