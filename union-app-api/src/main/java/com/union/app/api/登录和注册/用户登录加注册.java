package com.union.app.api.登录和注册;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.wechat.UserInfo;
import com.union.app.domain.wechat.WeChatUser;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.support.UserPostStatu;
import com.union.app.entity.用户.support.UserType;
import com.union.app.plateform.response.ApiResponse;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
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
    AppService appService;

    @RequestMapping(path="/login",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public ApiResponse 登录(@RequestParam(value="code") String code, @RequestParam(value="encryptedData")String encryptedData, @RequestParam(value="iv")String iv, @RequestParam(value="appName")String appName, @RequestParam(value="fromUser")String fromUser, @RequestParam(value="pkId")String pkId) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
        System.out.println("---------------login----------------");
        WeChatUser weChatUser = WeChatUtil.login(code);
        String openId = weChatUser.getOpenid();
        UserInfo userInfo = WeChatUtil.getUserInfo(encryptedData,weChatUser.getSession_key(),iv);

        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(UserEntity.class)
                .compareFilter("openId",CompareTag.Equal,openId)
                .andFilter()
                .compareFilter("appName",CompareTag.Equal,appName);

        UserEntity userEntity = appDaoService.querySingleEntity(UserEntity.class,entityFilterChain);


        if(ObjectUtils.isEmpty(userEntity))
        {
            userEntity = new UserEntity();

            userEntity.setFromUser(fromUser);
            userEntity.setOpenId(openId);
            userEntity.setSessionId(weChatUser.getSession_key());
            userEntity.setAppName(appName);
            userEntity.setUserId(openId);
            userEntity.setPkTimes(0);
            userEntity.setPostTimes(0);
            userEntity.setActivePkTimes(0);
            if(userService.canUserView(fromUser)){userEntity.setUserType(UserType.重点用户);}else{userEntity.setUserType(UserType.普通用户);}
            convert(userInfo,userEntity);
//            userService.新增注册用户(userEntity,appName,pkId,userEntity.getUserId(),fromUser);



            appService.添加邀请(pkId,userEntity.getUserId());









            appDaoService.insertEntity(userEntity);

        }
        else
        {
            String name = new String(userEntity.getNickName());
            if(!org.apache.commons.lang.StringUtils.equals(name,userInfo.getNickName())){
                userEntity.setNickName(userInfo.getNickName().getBytes());
                userEntity.setAvatarUrl(userInfo.getAvatarUrl());
                appDaoService.updateEntity(userEntity);
            }


        }

        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setUserType(userEntity.getUserType().getType());
        userBasicInfo.setFromUser(userEntity.getFromUser());
        userBasicInfo.setUserId(userEntity.getOpenId());
        userBasicInfo.setImgUrl(userEntity.getAvatarUrl());
        userBasicInfo.setUserName(new String(userEntity.getNickName()));


        return ApiResponse.buildSuccessResponse(userBasicInfo);
    }



//
//
//    @RequestMapping(path="/register",method = RequestMethod.POST)
//    @Transactional(rollbackOn = Exception.class)
//    public ApiResponse 注册(@RequestBody UserBasicInfo userBasicInfo) throws UnsupportedEncodingException, InvalidAlgorithmParameterException {
//
//        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(UserEntity.class)
//                .compareFilter("userId",CompareTag.Equal,userBasicInfo.getUserId());
//        UserEntity userEntity = appDaoService.querySingleEntity(UserEntity.class,entityFilterChain);
//
//
////        userEntity.setUserTel("13675118673");
////        userEntity.setUserTel(userBasicInfo.getUserTel());
////        userEntity.setUserSex(UserSex.valueOf(userBasicInfo.getUserSex()));
////        userEntity.setCityCode(RandomUtil.getRandomName());
////        userEntity.setCityCode(userBasicInfo.getUserCityCode());
////        userEntity.setJobCode(RandomUtil.getRandomName());
////        userEntity.setJobCode(userBasicInfo.getUserJobCode());
//        userEntity.setAvatarUrl(RandomUtil.getRandomImage());
////        userEntity.setAvatarUrl(userBasicInfo.getUserImg());
//        userEntity.setNickName(RandomUtil.getRandomName().getBytes(Charset.forName("utf-8")));
////        userEntity.setNickName(userBasicInfo.getUserName().getBytes(Charset.forName("utf-8")));
////        userEntity.setUserAge(RandomUtil.getRandomNumber());
////        userEntity.setUserAge(userBasicInfo.getUserAge());
//
//        if(!AppService.isBussinessApp(userBasicInfo.getAppName()))
//        {
//            userEntity.setUserType(userBasicInfo.getIsFromShare() == 1? UserType.重点用户:UserType.普通用户);
//        }
//        else
//        {
//            userEntity.setUserType(UserType.重点用户);
//        }
//
//        UserBasicInfo userInfo = new UserBasicInfo();
//        userInfo.setUserType(userEntity.getUserType().getType());
////        userInfo.setUserTel(userEntity.getUserTel());
////        userInfo.setUserAge(userEntity.getUserAge());
//
//        userInfo.setUserId(userEntity.getUserId());
//        userInfo.setImgUrl(userEntity.getAvatarUrl());
////        userInfo.setJob(RandomUtil.getRandomJob());
////        userInfo.setCity(RandomUtil.getRandomCity());
//        userInfo.setUserName(new String(userEntity.getNickName()));
////        userInfo.setUserSex(userEntity.getUserSex() == null? -1:userEntity.getUserSex().getSex());
//
//        appDaoService.updateEntity(userEntity);
//
//
//        return ApiResponse.buildSuccessResponse(userInfo);
//    }
//
//






    private void convert(UserInfo userInfo, UserEntity userEntity)
    {
        userEntity.setAvatarUrl(userInfo.getAvatarUrl());
        userEntity.setNickName(userInfo.getNickName().getBytes());

    }


}
