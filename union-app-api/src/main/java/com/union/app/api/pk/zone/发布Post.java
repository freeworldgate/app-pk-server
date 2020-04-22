package com.union.app.api.pk.zone;

import com.union.app.domain.pk.*;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.AppMode;
import com.union.app.entity.pk.EmptyPostEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.app.AppService;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 发布Post {


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
    RedisStringUtil redisStringUtil;

    @RequestMapping(path="/createPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 发布Post(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {


        Date cureentDate = new Date();


        String postId = null;

        PostEntity postEntity = postService.查询用户帖(pkId,userId);
        if(ObjectUtils.isEmpty(postEntity)){
            postId = postService.创建帖子(pkId,userId,title,images);


        }
        else
        {
            postId = postEntity.getPostId();
            postService.续传帖子(postEntity.getPostId(),title,userId,images);
        }



        Post post = postService.查询帖子(pkId,postId,userId,cureentDate);



        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));


    }



//
//    @RequestMapping(path="/uploadPostImgs",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 续传封面(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {
//
//
//
//        postService.续传帖子(postId,title,userId,images);
//
//        Post post = postService.查询帖子(pkId,postId,userId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
//
//    }
//



    @RequestMapping(path="/deletePostImg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 删除图片(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("imgId") String imgId,@RequestParam("userId") String userId) throws AppException, IOException {
        Date cureentDate = new Date();

        postService.删除帖子指定图片(postId,imgId,userId);

        Post post = postService.查询帖子(pkId,postId,userId,cureentDate);

        return AppResponse.buildResponse(PageAction.执行处理器("success",post));

    }












}
