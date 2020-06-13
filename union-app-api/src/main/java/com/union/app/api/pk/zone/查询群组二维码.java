package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

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
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @RequestMapping(path="/viewGroupCode",method = RequestMethod.GET)
    public AppResponse 查询用户的POST(@RequestParam("pkId") String pkId, @RequestParam("userId") String userId) throws AppException, IOException {


       if(userService.isUserVip(userId)){
           if( !pkService.isPkCreator(pkId,userId) && org.apache.commons.lang.StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkId,new Date())))
           {
               return AppResponse.buildResponse(PageAction.信息反馈框("提示","今日群组未更新"));

           }
           else
           {

               return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/message/message?pkId=" + pkId,true));


           }






       }
       else
       {

           return AppResponse.buildResponse(PageAction.前端数据更新("haha",""));
       }














    }


}
