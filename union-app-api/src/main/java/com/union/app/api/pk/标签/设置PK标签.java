package com.union.app.api.pk.标签;

import com.union.app.entity.pk.PkLocationEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
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
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 设置PK标签 {


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

    @RequestMapping(path="/setPkTips",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置PK位置(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("tips") List<String> tips) throws AppException, IOException {

        if(pkService.isPkCreator(pkId,userId))
        {

            appService.设置标签(pkId,tips);
            return AppResponse.buildResponse(PageAction.前端数据更新("pk.tips",appService.查询PK标签信息(pkId)));

        }

        return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));







    }



}
