package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.entity.pk.*;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 查询群组二维码 {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    LocationService locationService;

    @Autowired
    AppService appService;

    @RequestMapping(path="/viewGroupCode",method = RequestMethod.GET)
    public AppResponse 查询用户的POST(@RequestParam("pkId") String pkId, @RequestParam("userId") String userId) throws AppException, IOException {
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);



        if(locationService.isPkCreator(pkId,userId) ){return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/message/message?pkId=" + pkId + "&type=1",true));}


        if(!pkService.是否更新今日审核群(pkEntity)){return AppResponse.buildResponse(PageAction.信息反馈框("未更新主题群","请稍后再试..."));}

//        InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId,userId);
//        if(ObjectUtils.isEmpty(invitePkEntity))
//        {
//            return AppResponse.buildResponse(PageAction.信息反馈框("用户不可见","仅邀请用户可见..."));
//        }










        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/message/message?pkId=" + pkId + "&type=1",true));















    }


}
