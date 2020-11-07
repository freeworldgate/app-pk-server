package com.union.app.api.邀请;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkButton;
import com.union.app.domain.pk.PkButtonType;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkType;
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
public class 查询邀请 {

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


    @RequestMapping(path="/queryInvites",method = RequestMethod.GET)
    public AppResponse 查询邀请信息(@RequestParam("userId") String userId) throws AppException, IOException {

//        List<PkDetail> pkDetails = new ArrayList<>();

        List<PkDetail> pks = appService.查询用户邀请(userId,1);

        if(!userService.是否是遗传用户(userId)  && !AppConfigService.getConfigAsBoolean(ConfigItem.普通用户主题是否显示分享按钮和群组按钮))
        {
            pks.forEach(pk ->{
                PkButton pkButton = appService.显示按钮(PkButtonType.时间);
                pkButton.setName(pk.getTime());
                pk.setGroupInfo(pkButton);
            });
        }





        List<DataSet> dataSets = new ArrayList<>();
        if(CollectionUtils.isEmpty(pks))
        {
            dataSets.add(new DataSet("pkEnd",true));}
        else
        {
            pks.forEach(pk->{
                pk.setUserBack(appService.查询背景(0));
            });

            dataSets.add(new DataSet("pks",pks));
            dataSets.add(new DataSet("pkEnd",false));
        }
;
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("pageTag",true));
        dataSets.add(new DataSet("imgBack",appService.查询背景(3)));








        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextInvitePage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

        List<PkDetail> pks = appService.查询用户邀请(userId,page+1);

        if(pks.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

}
