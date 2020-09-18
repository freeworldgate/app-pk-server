package com.union.app.api.pk.管理;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.InviteType;
import com.union.app.entity.pk.PkCashierEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkType;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 上传内置公开群二维码 {


    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PkService pkService;

    @Autowired
    AppService appService;


    @RequestMapping(value = "/uploadInnerPublicGroupCode", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 上传群内置公开二维码(@RequestParam("password") String password,@RequestParam("pkId") String pkId,@RequestParam("passwd") String passwd,@RequestParam("url") String url) throws IOException, AppException {
        appService.验证Password(password);
        String mediaId = WeChatUtil.uploadImg2Wx(url);
        dynamicService.设置PK群组二维码MediaId(pkId,mediaId);
        dynamicService.设置PK群组二维码Url(pkId,url);


        return AppResponse.buildResponse(PageAction.信息反馈框("更新群组成功","更新群组成功..."));

    }

    @RequestMapping(value = "/uploadInnerPublicUserImg", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse uploadInnerPublicUserImg(@RequestParam("password") String password,@RequestParam("pkId") String pkId,@RequestParam("url") String url) throws IOException, AppException {
        appService.验证Password(password);
        User user = pkService.queryPkCreator(pkId);
        userService.修改用户头像(user.getUserId(),url);
        user = userService.queryUser(user.getUserId());




        return AppResponse.buildResponse(PageAction.执行处理器("success",user));

    }

}
