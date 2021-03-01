package com.union.app.api.卡点.打卡;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 发布Post {


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
    RedisStringUtil redisStringUtil;

    @Autowired
    KeyService keyService;

    @RequestMapping(path="/createImgPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 发布Post(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {
        String postId = postService.图片打卡(pkId,userId,title,images);
        Post post = postService.查询帖子(postId);
        post.setLeftTime(AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔));
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
    }

    @RequestMapping(path="/createVideoPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 视频打卡(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("videoUrl") String videoUrl,@RequestParam("width") int width,@RequestParam("height") int height) throws AppException, IOException {
        String postId = postService.视频打卡(pkId,userId,title,videoUrl,width,height);
        Post post = postService.查询帖子(postId);
        post.setLeftTime(AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔));
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
    }

    @RequestMapping(path="/createTextPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 文字打卡(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId) throws AppException, IOException {
        String postId = postService.文字打卡(pkId,userId,title);
        Post post = postService.查询帖子(postId);
        post.setLeftTime(AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔));
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
    }

    @RequestMapping(path="/createCardPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 卡片打卡(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("backId") String backId) throws AppException, IOException {
        String postId = postService.卡片打卡(pkId,userId,title,backId);
        Post post = postService.查询帖子(postId);
        post.setLeftTime(AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔));
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
    }




}
