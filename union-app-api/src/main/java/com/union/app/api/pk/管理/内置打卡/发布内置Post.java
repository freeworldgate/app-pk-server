package com.union.app.api.pk.管理.内置打卡;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
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
public class 发布内置Post {


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

    @RequestMapping(path="/createInnerUserPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 发布内置Post(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("backId") String backId,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {


        appService.checkManager(userId);

        userId = userService.随机选择内置用户();
        //添加时间限制

        String postId = postService.打卡(pkId,userId,title,images,backId);


        Post post = postService.查询帖子(postId);

        post.setLeftTime(AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔));
        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));


    }







}
