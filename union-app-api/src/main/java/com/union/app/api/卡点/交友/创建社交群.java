package com.union.app.api.卡点.交友;


import com.union.app.domain.pk.交友.PkGroup;
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
public class 创建社交群 {


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



    @RequestMapping(path="/createGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse createGroup(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("url") String url,@RequestParam("groupName") String groupName,@RequestParam("groupDesc") String groupDesc) throws AppException, IOException {

        groupService.创建群组(pkId,userId,url,groupName,groupDesc);

        PkGroup pkGroup = groupService.查询用户群组(pkId,userId);

        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("userGroup",pkGroup));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));



    }




    @RequestMapping(path="/cancelGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse cancelGroup(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        groupService.删除群组(pkId,userId);

        return AppResponse.buildResponse(PageAction.前端数据更新("userGroup.statu",null));
    }

    @RequestMapping(path="/updateGroup",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse updateGroup(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("groupCard") String groupCard) throws AppException, IOException {

        groupService.更新群二维码(pkId,userId,groupCard);

        return AppResponse.buildResponse(PageAction.执行处理器("success",null));
    }





}
