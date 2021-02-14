package com.union.app.api.卡点.交友;

import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 成员 {


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
    AppService appService;

    @Autowired
    GroupService groupService;


    @RequestMapping(path="/queryGroupMembers",method = RequestMethod.GET)
    public AppResponse queryGroupMembers(@RequestParam("groupId") String groupId) throws AppException, IOException, InterruptedException {

        //下一页
        List<User> members = groupService.查询成员列表(groupId,1);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("members",members));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

    @RequestMapping(path="/nextGroupMembers",method = RequestMethod.GET)
    public AppResponse nextGroupMembers(@RequestParam("groupId") String groupId,@RequestParam("page") int page) throws AppException, IOException, InterruptedException {


        List<User> members = groupService.查询成员列表(groupId,page+1);


        if(CollectionUtils.isEmpty(members))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",members));


    }



}
