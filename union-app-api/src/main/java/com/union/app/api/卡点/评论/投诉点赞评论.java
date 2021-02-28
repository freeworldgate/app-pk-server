package com.union.app.api.卡点.评论;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.PubType;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.MessageService;
import com.union.app.service.pk.dynamic.RedisService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.评论.CommentService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path="/pk")
public class 投诉点赞评论 {


    @Autowired
    AppDaoService daoService;

    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    CommentService commentService;

    @Autowired
    KeyService keyService;

    @RequestMapping(path="/complainPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 投诉(@RequestParam("postId") String postId,@RequestParam("userId") String userId,@RequestParam("type") int type) {

        postService.添加投诉(postId,userId,type);

        keyService.通知同步队列(postId, TimeSyncType.POSTCOMPLAIN.getScene());
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }









    @RequestMapping(path="/publishComment",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 添加评论(@RequestParam("postId") String postId,@RequestParam("userId") String userId,@RequestParam("comment") String comment)
    {

        Comment comm = commentService.添加评论(postId,userId,comment);

        keyService.通知同步队列(postId, TimeSyncType.POSTCOMMENT.getScene());
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",comm));
    }





    @RequestMapping(path="/delComment",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 删除评论(@RequestParam("userId") String userId,@RequestParam("commentId") String commentId) throws AppException {

        String postId = commentService.删除评论(userId,commentId);
        keyService.通知同步队列(postId, TimeSyncType.POSTCOMMENT.getScene());
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }



}
