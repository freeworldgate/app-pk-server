package com.union.app.api.卡点.搜索创建;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.*;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 顶置删除 {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    UserService userService;

    @Autowired
    KeyService keyService;

    @Autowired
    PostService postService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @Autowired
    UserDynamicService userDynamicService;

    @RequestMapping(path="/topPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 顶置(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {

        locationService.卡点状态检查(pkId);



        if(locationService.isPkCreator(pkId,userId))
        {

                pkService.顶置图册是否到期(pkId, postId);

            return AppResponse.buildResponse(PageAction.执行处理器("success",""));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法操作","不具备权限"));
        }

    }

    @RequestMapping(path="/setTopPostTime",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setTopPostTime(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("value") int value) throws AppException, IOException {



        if(locationService.isPkCreator(pkId,userId))
        {

            if(value<1 || value>24*60){
                return AppResponse.buildResponse(PageAction.信息反馈框("顶置周期不能超过一天","顶置周期不能超过一天"));
            }



            pkService.修改首页图册(pkId,postId,value);
            Post post = postService.查询帖子(postId);

            return AppResponse.buildResponse(PageAction.执行处理器("success",post));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法操作","不具备权限"));
        }

    }










    @RequestMapping(path="/removePost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 删除(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {

        PostEntity postEntity = postService.查询帖子ById(postId);



        if(StringUtils.equals(userId,postEntity.getUserId()))
        {
            postService.删除打卡信息(postId);
            pkUserDynamicService.卡点用户打卡次数减一(pkId,userId);
            userDynamicService.用户总打榜次数减一(userId);



            return AppResponse.buildResponse(PageAction.执行处理器("success",""));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法操作","不具备权限"));
        }

    }



    @RequestMapping(path="/hiddenPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 隐藏(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {

        locationService.卡点状态检查(pkId);

        locationService.卡点隐藏打卡信息检查(pkId);


        PostEntity postEntity = postService.查询帖子ById(postId);



        if(locationService.isPkCreator(pkId,userId)  && StringUtils.equalsIgnoreCase(postEntity.getPkId(),pkId))
        {
            postService.隐藏打卡信息(postId);
//            locationService.打卡次数减一(pkId,userId);
            locationService.隐藏数量加1(postEntity.getPkId());
            return AppResponse.buildResponse(PageAction.执行处理器("success",""));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法操作","不具备权限"));
        }

    }



    @RequestMapping(path="/removeFromHiddenPosts",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removeFromHiddenPosts(@RequestParam("userId") String userId,@RequestParam("postId") String postId) throws AppException, IOException {


        PostEntity postEntity = postService.查询帖子ById(postId);

        locationService.卡点状态检查(postEntity.getPkId());


        if(locationService.isPkCreator(postEntity.getPkId(),userId))
        {
            postService.移除隐藏打卡信息(postId);
//            locationService.打卡次数加一(postEntity.getPkId(),userId);
            locationService.隐藏数量减1(postEntity.getPkId());
            return AppResponse.buildResponse(PageAction.执行处理器("success",""));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法操作","不具备权限"));
        }

    }
}
