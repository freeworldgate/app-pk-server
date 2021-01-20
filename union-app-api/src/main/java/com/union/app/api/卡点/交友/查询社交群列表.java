package com.union.app.api.卡点.交友;

import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.pk.卡点.捞人.FindStatu;
import com.union.app.entity.pk.卡点.捞人.FindUserEntity;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class 查询社交群列表 {


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
    GroupService groupService;

    @RequestMapping(path="/queryPkGroups",method = RequestMethod.GET)
    public AppResponse queryPkGroups(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {


        List<PkGroup> pkGroups = groupService.查询有效群组(pkId,userId);


        List<DataSet> dataSets = new ArrayList<>();


        dataSets.add(new DataSet("pkGroups",pkGroups));
        dataSets.add(new DataSet("emptyImage",appService.查询背景(1)));
        dataSets.add(new DataSet("userFindImage",appService.查询背景(3)));
        dataSets.add(new DataSet("backUrl",appService.查询背景(5)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

}
