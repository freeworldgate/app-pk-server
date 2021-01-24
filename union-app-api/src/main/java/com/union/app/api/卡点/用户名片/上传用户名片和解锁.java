package com.union.app.api.卡点.用户名片;

import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.domain.pk.名片.UserCard;
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
public class 上传用户名片和解锁 {


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

    @RequestMapping(path="/setUserCard",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse queryUserFind(@RequestParam("userCard") String userCard,@RequestParam("userId") String userId) throws IOException {

        userService.上传UserCard(userId,userCard);

        return AppResponse.buildResponse(PageAction.前端数据更新("userCard.userCard",userCard));

    }
    @RequestMapping(path="/userCardApply",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse userCardApply(@RequestParam("text") String text,@RequestParam("userId") String userId,@RequestParam("targetId") String targetId) throws IOException, AppException {

        userService.申请UserCard(targetId,userId,text);


        UserCard userCard = userService.查询UserCard(targetId);
        UserCardApply userApply = userService.查询用户名片留言(targetId,userId);
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("userApply",userApply));
        dataSets.add(new DataSet("userCard",userCard));
        dataSets.add(new DataSet("emptyImage",appService.查询背景(1)));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

}
