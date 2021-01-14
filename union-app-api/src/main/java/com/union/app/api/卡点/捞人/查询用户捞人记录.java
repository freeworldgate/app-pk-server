package com.union.app.api.卡点.捞人;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.用户.UserKvEntity;
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
import com.union.app.util.time.TimeUtils;
import org.apache.commons.collections4.CollectionUtils;
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
public class 查询用户捞人记录 {


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

    @RequestMapping(path="/queryUserFind",method = RequestMethod.GET)
    public AppResponse queryUserFind(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws IOException {

        FindUser findUser = findService.查询用户捞人记录(pkId,userId);
        PkDetail pk = locationService.querySinglePk(pkId);
        UserKvEntity userKvEntity = userService.queryUserKvEntity(userId);
        String leftTime = TimeUtils.剩余可打捞时间(userKvEntity.getFindTimeLength());

        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("findUser",findUser));
        dataSets.add(new DataSet("pk",pk));
        dataSets.add(new DataSet("leftTime",leftTime));

        dataSets.add(new DataSet("emptyImage",appService.查询背景(1)));
        dataSets.add(new DataSet("createFindUserImage",appService.查询背景(3)));



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

}
