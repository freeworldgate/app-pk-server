package com.union.app.api.卡点.用户名片;

import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class 申请UserCard列表 {


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

    @RequestMapping(path="/queryUserCardApplys",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse queryUserCardApplys(@RequestParam("targetUserId") String targetUserId,@RequestParam("userId") String userId,@RequestParam("type") int type) throws AppException, IOException, InterruptedException {
        if(!StringUtils.equalsIgnoreCase(targetUserId,userId))
        {
            throw AppException.buildException(PageAction.信息反馈框("用户无权限","用户无权限查询该信心..."));
        }


        List<UserCardApply> applys = userService.查询UserCard申请列表(targetUserId,type,1);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("emptyImage",appService.查询背景(4)));
        dataSets.add(new DataSet("applys",applys));
        dataSets.add(new DataSet("page",1));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextUserCardApplys",method = RequestMethod.GET)
    public AppResponse queryHiddenPost(@RequestParam("targetUserId") String targetUserId,@RequestParam("userId") String userId,@RequestParam("type") int type,@RequestParam("page") int page) throws AppException, IOException {
        if(!StringUtils.equalsIgnoreCase(targetUserId,userId))
        {
            throw AppException.buildException(PageAction.信息反馈框("用户无权限","用户无权限查询该信心..."));
        }
        //页数不断递增，但是只有一百页。
        List<UserCardApply> applys = userService.查询UserCard申请列表(targetUserId,type,page+1);
        if(CollectionUtils.isEmpty(applys))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",applys));

    }

}
