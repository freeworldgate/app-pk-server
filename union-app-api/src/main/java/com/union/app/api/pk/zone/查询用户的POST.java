package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.InviteType;
import com.union.app.entity.pk.PkEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping(path="/pk")
public class 查询用户的POST {


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
    AppService appService;


    @RequestMapping(path="/queryUserPost",method = RequestMethod.GET)
    public AppResponse 查询用户的POST(@RequestParam("pkId") String pkId, @RequestParam("userId") String userId) throws AppException, IOException {

            Date cureentDate = new Date();

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        if(pkEntity.getIsInvite()==InviteType.邀请) {
            InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId, userId);
            if (!pkService.isPkCreator(pkId, userId) && ObjectUtils.isEmpty(invitePkEntity)) {
                return AppResponse.buildResponse(PageAction.信息反馈框("非邀请用户", "仅邀请用户可见"));
            }
        }


            Post post = postService.查询用户帖子(pkId,userId);

//            return AppResponse.buildResponse(PageAction.执行处理器("uploadImgs",""));


            if(!ObjectUtils.isEmpty(post)){

                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/post/post?pkId=" + pkId + "&postId=" + post.getPostId(),true));
            }
            else
            {
//                return AppResponse.buildResponse(PageAction.执行处理器("createPost",""));
                return AppResponse.buildResponse(PageAction.执行处理器("uploadImgs",""));
            }













    }


}
