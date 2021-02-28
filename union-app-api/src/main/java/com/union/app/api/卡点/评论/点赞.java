package com.union.app.api.卡点.评论;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.SyncType;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LikeService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path="/pk")
public class 点赞 {


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
    LikeService likeService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    KeyService keyService;



    @RequestMapping(path="/greate",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 赞(@RequestParam("scene") int scene,@RequestParam("id") String id,@RequestParam("userId") String userId,@RequestParam("statu") int statu)
    {

        likeService.点赞或踩(id,userId,statu);
        keyService.通知同步队列(id,scene);
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }



















}
