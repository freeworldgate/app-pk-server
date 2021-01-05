package com.union.app.api.用户相册;

import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.卡点.UserFollowEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 指定用户图册 {

    @Autowired
    AppService appService;

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

    @Autowired
    LocationService locationService;


    @RequestMapping(path="/userPublishPosts",method = RequestMethod.GET)
    public AppResponse 用户图册(@RequestParam("userId") String userId,@RequestParam("targetId") String targetId) throws AppException, IOException {


        List<Post> posts = appService.查询用户发布图册(targetId,1);
        List<DataSet> dataSets = new ArrayList<>();
        UserFollowEntity userFollowEntity = locationService.查询关注(userId,targetId);

        dataSets.add(new DataSet("followStatu", !ObjectUtils.isEmpty(userFollowEntity)));

        if(CollectionUtils.isEmpty(posts))
        {
            dataSets.add(new DataSet("nomore",true));
        }
        else
        {
            dataSets.add(new DataSet("posts",posts));
            dataSets.add(new DataSet("nomore",false));
        }

        ;
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("creator",userService.queryUser(targetId)));
//        dataSets.add(new DataSet("imgBack",appService.查询背景(1)));








        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));



    }
    @RequestMapping(path="/nextUserPublishPosts",method = RequestMethod.GET)
    public AppResponse 用户图册(@RequestParam("targetId") String targetId,@RequestParam("page") int page) throws AppException, IOException {

        List<Post> posts = appService.查询用户发布图册(targetId,page+1);

        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));
        }
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }


}
