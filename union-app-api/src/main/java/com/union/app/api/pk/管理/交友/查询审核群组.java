package com.union.app.api.pk.管理.交友;

import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.domain.pk.捞人.FindUser;
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
public class 查询审核群组 {

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


    @RequestMapping(path="/queryApprovingGroups",method = RequestMethod.GET)
    public AppResponse 查询审核群组(@RequestParam("userId") String userId) throws AppException, IOException {
        appService.checkManager(userId);
        List<DataSet> dataSets = new ArrayList<>();
        List<PkGroup> findUsers = groupService.查询待审核的群组(1);
        dataSets.add(new DataSet("groups",findUsers));
        dataSets.add(new DataSet("page",1));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextApprovingGroups",method = RequestMethod.GET)
    public AppResponse 查询审核群组(@RequestParam("page") int page,@RequestParam("userId") String userId) throws AppException, IOException {
        appService.checkManager(userId);
        //页数不断递增，但是只有一百页。
        List<PkGroup> findUsers = groupService.查询待审核的群组(page+1);
        if(CollectionUtils.isEmpty(findUsers))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("groups",findUsers));
        return AppResponse.buildResponse(PageAction.执行处理器("success",findUsers));


    }

}
