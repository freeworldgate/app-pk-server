package com.union.app.api.pk.管理.交友;

import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
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
public class 审批Group {

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
    GroupService groupService;


    @RequestMapping(path="/passGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 审批Group(@RequestParam("groupId") String groupId) throws AppException, IOException {

        groupService.审批(groupId);

        PkGroup pkGroup = groupService.查询ByGroupId(groupId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",pkGroup));

    }
    @RequestMapping(path="/passUpdatingGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 审批UpdatingGroup(@RequestParam("groupId") String groupId) throws AppException, IOException {

        groupService.审批UpdatingGroup(groupId);
//        PkGroup pkGroup = groupService.查询ByGroupId(groupId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }

}
