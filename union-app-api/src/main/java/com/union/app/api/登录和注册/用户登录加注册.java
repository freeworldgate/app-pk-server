package com.union.app.api.登录和注册;

import com.union.app.common.dao.KeyService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
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
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;

@RestController
@RequestMapping(path="/user")
public class 用户登录加注册 {

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

    @RequestMapping(path="/login",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public ApiResponse 登录(@RequestParam(value="code") String code, @RequestParam(value="encryptedData")String encryptedData, @RequestParam(value="iv")String iv, @RequestParam(value="appName")String appName, @RequestParam(value="pkId")String pkId) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
        System.out.println("---------------login----------------");
        WeChatUser weChatUser = WeChatUtil.login(code);
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

            userEntity.setUserType(UserType.普通用户);
            convert(userInfo,userEntity);
            userDynamicService.创建Dynamic表(userEntity.getUserId());
            userService.创建UserCardEntity(userEntity.getUserId());
            appDaoService.insertEntity(userEntity);

        }
        else
        {
//            String name = new String(userEntity.getNickName());
//            if(!org.apache.commons.lang.StringUtils.equals(name,userInfo.getNickName())){
//                userEntity.setNickName(userInfo.getNickName());
//                userEntity.setAvatarUrl(userInfo.getAvatarUrl());
//                appDaoService.updateEntity(userEntity);
//                keyService.刷新用户User缓存(userEntity.getUserId());
//            }


        }

        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setUserType(userEntity.getUserType().getType());
        userBasicInfo.setFromUser(userEntity.getFromUser());
        userBasicInfo.setUserId(userEntity.getOpenId());
        userBasicInfo.setImgUrl(userEntity.getAvatarUrl());
        userBasicInfo.setUserName(new String(userEntity.getNickName()));


        return ApiResponse.buildSuccessResponse(userBasicInfo);
    }





    @RequestMapping(path="/changeUser",method = RequestMethod.GET)
    public AppResponse changeUser() {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                .nullFilter("appName",false);
        UserEntity result = appDaoService.querySingleEntity(UserEntity.class,filter);
        if(ObjectUtils.isEmpty(result)){ return AppResponse.buildResponse(PageAction.信息反馈框("无appName用户","无appName用户"));}
        User user = new User();

        user.setUserName(new String(result.getNickName()));
        user.setUserId(result.getUserId());
        user.setUserType(ObjectUtils.isEmpty(result.getUserType())?UserType.普通用户.getType():result.getUserType().getType());
        user.setImgUrl(result.getAvatarUrl());



        return AppResponse.buildResponse(PageAction.执行处理器("success",user));
    }








    private void convert(UserInfo userInfo, UserEntity userEntity)
    {
        userEntity.setAvatarUrl(userInfo.getAvatarUrl());
        userEntity.setNickName(userInfo.getNickName());

    }


}
