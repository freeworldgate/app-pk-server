package com.union.app.api.pk.审核;

import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.pk.审核.ApproveComplain;
import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.PostEntity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 设置审核人员 {


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

    @RequestMapping(path="/setApprover",method = RequestMethod.GET)
    public AppResponse 用户积分(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId,@RequestParam("approveUserId") String approveUserId) throws AppException, IOException {


        PostEntity postEntity = postService.查询帖子ById(pkId,postId);
        if(!StringUtils.equals(userId,postEntity.getUserId())){return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}

        dynamicService.设置帖子的审核用户(pkId,postId,approveUserId);



        return AppResponse.buildResponse(PageAction.前端数据更新("",""));

    }



}
