package com.union.app.api.pk.zone;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.AppMessage;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.pk.审核.PkComment;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询PK认证 {


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
    UserInfoService userInfoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @RequestMapping(path="/queryPkApprove",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {




        PkDetail pkDetail = pkService.querySinglePk(pkId);
        ApproveMessage approveMessage = null;
        PkComment pkComment = null;
        AppMessage appMessage = null;

        if(userService.isUserVip(userId))
        {
            pkComment = appService.查询激活消息留言(pkId);
            appMessage = appService.查询激活消息(pkId,userId);
            approveMessage = approveService.查询PK公告消息(pkId);

        }
        pkDetail.setApproveMessage(approveMessage);



        appService.vip包装(pkDetail,userId,"");










        return AppResponse.buildResponse(PageAction.前端数据更新("pk",pkDetail));

    }

}
