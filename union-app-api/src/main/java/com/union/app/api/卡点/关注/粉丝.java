package com.union.app.api.卡点.关注;

import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
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
public class 粉丝 {


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

    @RequestMapping(path="/queryFansUsers",method = RequestMethod.GET)
    public AppResponse queryApprovingPkImages(@RequestParam("targetId") String targetId) throws AppException, IOException, InterruptedException {

        //下一页
        List<User> followers = locationService.查询粉丝列表(targetId,1);




        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("followers",followers));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

    @RequestMapping(path="/nextFansUsers",method = RequestMethod.GET)
    public AppResponse nextPkApprovingImagePage(@RequestParam("targetId") String targetId,@RequestParam("page") int page) throws AppException, IOException, InterruptedException {


        List<User> followers  = locationService.查询粉丝列表(targetId,page+1);


        if(CollectionUtils.isEmpty(followers))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",followers));


    }



}
