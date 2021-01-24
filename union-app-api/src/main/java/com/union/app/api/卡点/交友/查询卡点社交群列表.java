package com.union.app.api.卡点.交友;

import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
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
public class 查询卡点社交群列表 {


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
    GroupService groupService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @RequestMapping(path="/queryPkGroups",method = RequestMethod.GET)
    public AppResponse queryPkGroups(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {


        List<PkGroup> pkGroups = groupService.查询有效群组(pkId,userId);


        List<DataSet> dataSets = new ArrayList<>();


        dataSets.add(new DataSet("pkGroups",pkGroups));
        PkUserDynamicEntity entity = pkUserDynamicService.查询卡点用户动态表(pkId,userId);
        dataSets.add(new DataSet("postTimes",ObjectUtils.isEmpty(entity)?0:entity.getPostTimes()));
        dataSets.add(new DataSet("unLockGroups",ObjectUtils.isEmpty(entity)?0:entity.getUnLockGroups()));
        dataSets.add(new DataSet("emptyData",appService.查询背景(4)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

}
