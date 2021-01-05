package com.union.app.api.卡点.用户名片;

import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.domain.pk.捞人.FindUser;

import com.union.app.domain.user.User;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询用户名片 {


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

    @RequestMapping(path="/queryUserCard",method = RequestMethod.GET)
    public AppResponse queryUserFind(@RequestParam("targetId") String targetId,@RequestParam("userId") String userId) throws IOException {

        User target = userService.queryUser(targetId);
        UserCardApply userApply = userService.查询用户名片留言(target.getUserId(),userId);
        String userCard = userService.查询Pk创建者名片(target.getUserId());


        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("target",target));
        dataSets.add(new DataSet("userApply",userApply));
        dataSets.add(new DataSet("emptyImage",appService.查询背景(1)));
        dataSets.add(new DataSet("userCard",userCard));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

}
