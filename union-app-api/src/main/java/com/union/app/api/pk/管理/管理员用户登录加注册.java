package com.union.app.api.pk.管理;

import com.union.app.api.登录和注册.UserBasicInfo;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.user.User;
import com.union.app.domain.wechat.UserInfo;
import com.union.app.domain.wechat.WeChatUser;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.response.ApiResponse;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;

@RestController
@RequestMapping(path="/user")
public class 管理员用户登录加注册 {

    @Autowired
    AppDaoService appDaoService;

    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    PkService pkService;

    @Autowired
    KeyService keyService;



    @RequestMapping(path="/managerLogin",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public ApiResponse 管理员用户登录加注册(@RequestParam(value="code") String code, @RequestParam(value="encryptedData")String encryptedData, @RequestParam(value="iv")String iv, @RequestParam(value="appName")String appName) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
        System.out.println("---------------login----------------");
        WeChatUser weChatUser = WeChatUtil.manageLogin(code);
        String openId = weChatUser.getOpenid();
        UserInfo userInfo = WeChatUtil.getUserInfo(encryptedData,weChatUser.getSession_key(),iv);

        UserEntity userEntity = userService.queryUserEntity(openId);
        if(ObjectUtils.isEmpty(userEntity))
        {
            userEntity = new UserEntity();
            userEntity.setOpenId(openId);
            userEntity.setSessionId(weChatUser.getSession_key());
            userEntity.setAppName(appName);
            userEntity.setUserId(openId);

            userEntity.setUserType(UserType.管理用户);
            convert(userInfo,userEntity);
            userDynamicService.创建Dynamic表(userEntity.getUserId());
            userService.创建UserCardEntity(userEntity.getUserId());
            appDaoService.insertEntity(userEntity);

        }


        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setUserType(userEntity.getUserType().getType());
        userBasicInfo.setFromUser(userEntity.getFromUser());
        userBasicInfo.setUserId(userEntity.getOpenId());
        userBasicInfo.setImgUrl(userEntity.getAvatarUrl());
        userBasicInfo.setUserName(new String(userEntity.getNickName()));


        return ApiResponse.buildSuccessResponse(userBasicInfo);
    }







    private void convert(UserInfo userInfo, UserEntity userEntity)
    {
        userEntity.setAvatarUrl(userInfo.getAvatarUrl());
        userEntity.setNickName(userInfo.getNickName());

    }


}
