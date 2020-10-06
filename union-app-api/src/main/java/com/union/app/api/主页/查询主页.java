package com.union.app.api.主页;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.plateform.constant.ConfigItem;
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
import org.springframework.util.StringUtils;
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
public class 查询主页 {

    @Autowired
    AppService appService;

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


    @RequestMapping(path="/queryHomePage",method = RequestMethod.GET)
    public AppResponse 查询主页(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId)  {



        List<PkDetail> pks = appService.随机主题(userId,pkId);


        if(!pkService.isVipView(userId,pkId)  && !AppConfigService.getConfigAsBoolean(ConfigItem.普通用户主题是否显示分享按钮和群组按钮))
        {
            pks.forEach(pk ->{
                PkButton pkButton = appService.显示按钮(PkButtonType.时间);
                pkButton.setName(pk.getTime());
                pk.setGroupInfo(pkButton);
            });
        }





        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("pks",pks));
        dataSets.add(new DataSet("page",1));

        User user = userService.queryUser(userId);
        if(ObjectUtils.isEmpty(user))
        {
            dataSets.add(new DataSet("user",user));
        }
        dataSets.add(new DataSet("imgBack",appService.查询背景(1)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
//
//    @RequestMapping(path="/nextHomePage",method = RequestMethod.GET)
//    public AppResponse 查询主页(@RequestParam("userId") String userId,@RequestParam("fromUser") String fromUser,@RequestParam("page") int page) throws AppException, IOException {
//        List<PkDetail> pkDetails = new ArrayList<>();
//
//        if(userService.canUserView(userId,fromUser))
//        {
//            List<PkDetail> pks = appService.查询用户主页(userId,page + 1,1);
//            pkDetails.addAll(pks);
//        }
//        else
//        {
//            List<PkDetail> pks = appService.查询用户主页(userId,page + 1,0);
//            pkDetails.addAll(pks);
//
//        }
//
//        if(pkDetails.size() == 0)
//        {
//            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));
//
//        }
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetails));
//
//    }

}
