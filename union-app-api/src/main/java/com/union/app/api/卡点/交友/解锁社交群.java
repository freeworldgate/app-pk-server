package com.union.app.api.卡点.交友;


import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.entity.pk.社交.PkGroupMemberEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 解锁社交群 {


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
    OrderService orderService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    ComplainService complainService;

    @Autowired
    LocationService locationService;

    @Autowired
    FindService findService;

    @Autowired
    GroupService groupService;



    @RequestMapping(path="/unLockGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse createGroup(@RequestParam("groupId") int groupId,@RequestParam("userId") String userId) throws AppException, IOException {

        PkGroupMemberEntity pkGroupMemberEntity = groupService.解锁群组(groupId,userId);


        return AppResponse.buildResponse(PageAction.执行处理器("success",pkGroupMemberEntity));



    }





}
