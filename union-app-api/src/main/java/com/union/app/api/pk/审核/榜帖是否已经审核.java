package com.union.app.api.pk.审核;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
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
public class 榜帖是否已经审核 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ApproveService approveService;

    @RequestMapping(path="/isPostApproved",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询审核信息(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {

        if(AppConfigService.getConfigAsBoolean(ConfigItem.对所有用户展示审核系统) || userService.canUserView(userId))
        {
            PostEntity postEntity    = postService.查询帖子ById(pkId,postId);
            if(  (postEntity.getStatu() == PostStatu.审核中) && StringUtils.isBlank(dynamicService.查询审核用户(pkId,postEntity.getPostId())))
            {
                return AppResponse.buildResponse(PageAction.执行处理器("noApprove","/pages/pk/editApproveComment/editApproveComment?pkId=" + pkId + "&postId=" + postEntity.getPostId()));
            }
            else
            {
                return AppResponse.buildResponse(PageAction.前端数据更新("verfiy",true));
            }
        }

        return AppResponse.buildResponse(PageAction.前端数据更新("verfiy",true));
    }

    @RequestMapping(path="/goApproving",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 请求审核(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {

        if(AppConfigService.getConfigAsBoolean(ConfigItem.对所有用户展示审核系统) || userService.canUserView(userId))
        {
            Date current = new Date();


            PostEntity postEntity    = postService.查询帖子ById(pkId,postId);
            if(  (postEntity.getStatu() == PostStatu.审核中))
            {
                boolean approve = StringUtils.isBlank(dynamicService.查询审核用户(pkId,postEntity.getPostId()));
                if(approve)
                {
                    return AppResponse.buildResponse(PageAction.执行处理器("noApprove","/pages/pk/editApproveComment/editApproveComment?pkId=" + pkId + "&postId=" + postEntity.getPostId()));
                }
                else
                {
                    return AppResponse.buildResponse(PageAction.信息反馈框("稍后重试",AppConfigService.getConfigAsInteger(ConfigItem.审核榜帖最大等待时间)  + "分钟内只能请求一次"));
                }
            }
            else
            {
                return AppResponse.buildResponse(PageAction.前端数据更新("verfiy",true));
            }


        }
        return AppResponse.buildResponse(PageAction.前端数据更新("verfiy",true));
    }

}
