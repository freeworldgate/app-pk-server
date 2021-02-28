package com.union.app.api.卡点.评论;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.comment.PubType;
import com.union.app.domain.pk.comment.Restore;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.RedisService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.评论.CommentService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path="/pk")
public class 回复 {


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
    AppService appService;

    @Autowired
    CommentService commentService;

    @Autowired
    RedisService redisService;



    @RequestMapping(path="/publishRestore",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 添加回复(@RequestParam("commentId") String commentId,@RequestParam("userId") String userId,@RequestParam("targetUserId") String targetUserId,@RequestParam("comment") String comment)
    {

        Restore restore = commentService.添加回复(commentId,userId,targetUserId,comment);

        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",restore));
    }





    @RequestMapping(path="/delRestore",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 删除回复(@RequestParam("userId") String userId,@RequestParam("restoreId") String restoreId) throws AppException {

        commentService.删除回复(userId,restoreId);

        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }








}
