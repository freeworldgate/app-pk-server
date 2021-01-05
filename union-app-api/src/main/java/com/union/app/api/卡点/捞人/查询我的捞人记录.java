package com.union.app.api.卡点.捞人;

import com.union.app.domain.pk.捞人.FindUser;
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
public class 查询我的捞人记录 {


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

    @RequestMapping(path="/queryMyFinds",method = RequestMethod.GET)
    public AppResponse queryMyFinds(@RequestParam("userId") String userId) throws IOException {

        List<FindUser> findUsers = findService.查询我的捞人记录(userId,1);



        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("findUsers",findUsers));
        dataSets.add(new DataSet("emptyImage",appService.查询背景(3)));


        dataSets.add(new DataSet("page",1));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
    @RequestMapping(path="/nextMyFinds",method = RequestMethod.GET)
    public AppResponse nextPkApprovingImagePage(@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException, InterruptedException {

        List<FindUser> findUsers = findService.查询我的捞人记录(userId,page+1);



        if(CollectionUtils.isEmpty(findUsers))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",findUsers));


    }

}
