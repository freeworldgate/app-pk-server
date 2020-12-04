package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path="/pk")
public class 发布图贴 {

    @Autowired
    AppService appService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();


    @RequestMapping(path="/preCreatePost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 预置Post(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("password") String password,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {

        appService.验证Password(password);

        String postId = postService.预置帖子(pkId,title,images);

        Post post = postService.查询预置帖子(postId);




        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));


    }



    @RequestMapping(path="/preSetComment",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 预置Comment(@RequestParam("postId") String postId,  @RequestParam("password") String password,@RequestParam("text") String text,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {
        appService.验证Password(password);


        Date currentDay = new Date();

        List<DataSet> dataSets = new ArrayList<>();
        PostEntity postEntity = postService.查询帖子ById(postId);


//        approveService.设置审核留言(postEntity.getPkId(),postEntity.getPostId(),postEntity.getUserId(),text,imgUrl);

        Post post = postService.查询预置帖子(postId);

        return AppResponse.buildResponse(PageAction.执行处理器("message",post));





    }

}
