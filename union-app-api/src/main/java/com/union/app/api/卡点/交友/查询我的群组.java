package com.union.app.api.卡点.交友;

import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.domain.pk.捞人.FindUser;
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
public class 查询我的群组 {


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
    GroupService groupService;

    @RequestMapping(path="/queryMyGroups",method = RequestMethod.GET)
    public AppResponse queryMyFinds(@RequestParam("userId") String userId) throws IOException {

        List<PkGroup> pkGroups = groupService.查询我的Groups(userId,1);



        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("pkGroups",pkGroups));
        dataSets.add(new DataSet("emptyImage",appService.查询背景(3)));
        dataSets.add(new DataSet("page",1));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
    @RequestMapping(path="/nextMyGroups",method = RequestMethod.GET)
    public AppResponse nextPkApprovingImagePage(@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException, InterruptedException {

        List<PkGroup> pkGroups = groupService.查询我的Groups(userId,page+1);

        if(CollectionUtils.isEmpty(pkGroups))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pkGroups));


    }

}
