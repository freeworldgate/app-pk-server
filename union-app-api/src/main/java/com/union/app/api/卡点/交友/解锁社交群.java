package com.union.app.api.卡点.交友;


import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.entity.pk.社交.PkGroupEntity;
import com.union.app.entity.pk.社交.PkGroupMemberEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @Autowired
    FindService findService;

    @Autowired
    GroupService groupService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;


    @RequestMapping(path="/unLockGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse unLockGroup(@RequestParam("groupId") String groupId,@RequestParam("userId") String userId) throws AppException, IOException {

        PkGroupEntity pkGroupEntity = groupService.查询用户群组EntityById(groupId);

        PkUserDynamicEntity pkUserDynamicEntity = pkUserDynamicService.查询卡点用户动态表(pkGroupEntity.getPkId(),userId);

        if(ObjectUtils.isEmpty(pkUserDynamicEntity))
        {
            pkUserDynamicEntity = pkUserDynamicService.initEntity(pkGroupEntity.getPkId(),userId);
        }

        if(pkUserDynamicEntity.getPostTimes()<=pkUserDynamicEntity.getUnLockGroups())
        {
            throw AppException.buildException(PageAction.信息反馈框("解锁群组超限","解锁群组超限,你可以通过发布打卡信息获得解锁权限"));
        }



        PkGroupMemberEntity pkGroupMemberEntity = groupService.解锁群组(groupId,userId);


        return AppResponse.buildResponse(PageAction.执行处理器("success",pkGroupMemberEntity));



    }





}
