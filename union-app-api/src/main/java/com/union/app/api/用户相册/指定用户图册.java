package com.union.app.api.用户相册;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.PkDynamic.UserDynamic;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.名片.UserCard;
import com.union.app.entity.pk.kadian.UserFollowEntity;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.MessageService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
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
    MessageService messageService;

    @Autowired
    LikeService likeService;

    @Autowired
    LocationService locationService;


    @Autowired
    UserDynamicService userDynamicService;

    @RequestMapping(path="/userPublishPosts",method = RequestMethod.GET)
    public AppResponse 用户图册(@RequestParam("userId") String userId,@RequestParam("targetId") String targetId) throws AppException, IOException {

        UserEntity userEntity = userService.queryUserEntity(userId);
        if(userEntity.getUserType() == UserType.消息通知)
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("消息通知不可見","消息通知内容不可见!"));
        }

        List<Post> posts = appService.查询用户发布图册(targetId,1);
        List<DataSet> dataSets = new ArrayList<>();
        UserFollowEntity userFollowEntity = locationService.查询关注(userId,targetId);
        UserDynamic userDynamic = userDynamicService.queryUserDynamic(targetId);

        UserCard userCard = userService.查询UserCard(targetId);


        dataSets.add(new DataSet("hasNewMessages",messageService.hasNewMessages(targetId)));
        dataSets.add(new DataSet("userDynamic",userDynamic));
        dataSets.add(new DataSet("userCard",userCard));

        dataSets.add(new DataSet("followStatu", !ObjectUtils.isEmpty(userFollowEntity)));
        likeService.查询Post点赞记录(posts,userId);
        dataSets.add(new DataSet("posts",posts));
        dataSets.add(new DataSet("nomore",false));

        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("creator",userService.queryUser(targetId)));
        dataSets.add(new DataSet("postBorderRadius", AppConfigService.getConfigAsInteger(ConfigItem.小图片圆角)));
        dataSets.add(new DataSet("post1BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post2或4张图圆角)));
        dataSets.add(new DataSet("videoBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.视频圆角)));
        dataSets.add(new DataSet("post2BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post1张图圆角)));
        dataSets.add(new DataSet("post3BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.文字背景圆角)));


        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));



    }
    @RequestMapping(path="/nextUserPublishPosts",method = RequestMethod.GET)
    public AppResponse 用户图册(@RequestParam("userId") String userId,@RequestParam("targetId") String targetId,@RequestParam("page") int page) throws AppException, IOException {

        List<Post> posts = appService.查询用户发布图册(targetId,page+1);

        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));
        }
        likeService.查询Post点赞记录(posts,userId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }


}
