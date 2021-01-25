package com.union.app.api.卡点.交友;


import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.社交.GroupStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查詢用戶社交群 {


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



    @RequestMapping(path="/queryUserGroup",method = RequestMethod.GET)
    public AppResponse createGroup(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        PkGroup pkGroup = groupService.查询用户群组(pkId,userId);
        if(!ObjectUtils.isEmpty(pkGroup) && pkGroup.getMembers() >= 200)
        {
            pkGroup.setStatu(new KeyValuePair(GroupStatu.已过期.getStatu(),GroupStatu.已过期.getStatuStr()));
        }
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("userGroup",pkGroup));
        dataSets.add(new DataSet("backUrl",appService.查询背景(7)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
    @RequestMapping(path="/queryGroupLength",method = RequestMethod.GET)
    public AppResponse queryGroupLength(@RequestParam("pkId") String pkId) throws AppException, IOException {

        PkEntity pk = locationService.querySinglePkEntity(pkId);
//        List<DataSet> dataSets = new ArrayList<>();
//        dataSets.add(new DataSet("userGroup",pkGroup));
//        dataSets.add(new DataSet("emptyImage",appService.查询背景(1)));
//        dataSets.add(new DataSet("createFindUserImage",appService.查询背景(3)));
        return AppResponse.buildResponse(PageAction.执行处理器("length",pk));

    }









}
