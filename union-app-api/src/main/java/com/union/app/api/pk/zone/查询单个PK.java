package com.union.app.api.pk.zone;

        import com.union.app.common.config.AppConfigService;
        import com.union.app.domain.pk.PkButtonType;
        import com.union.app.domain.pk.PkDetail;
        import com.union.app.domain.pk.Post;
        import com.union.app.domain.pk.UserCode;
        import com.union.app.domain.pk.apply.KeyNameValue;
        import com.union.app.domain.pk.integral.UserIntegral;
        import com.union.app.domain.pk.审核.ApproveMessage;
        import com.union.app.domain.pk.审核.ApproveUser;
        import com.union.app.domain.user.User;
        import com.union.app.domain.工具.RandomUtil;
        import com.union.app.entity.pk.PkEntity;
        import com.union.app.entity.pk.PostEntity;
        import com.union.app.entity.pk.PostStatu;
        import com.union.app.entity.用户.UserEntity;
        import com.union.app.entity.用户.support.UserType;
        import com.union.app.plateform.constant.ConfigItem;
        import com.union.app.plateform.data.resultcode.AppException;
        import com.union.app.plateform.data.resultcode.AppResponse;
        import com.union.app.plateform.data.resultcode.DataSet;
        import com.union.app.plateform.data.resultcode.PageAction;
        import com.union.app.plateform.storgae.redis.RedisStringUtil;
        import com.union.app.service.pk.click.ClickService;
        import com.union.app.service.pk.dynamic.DynamicService;
        import com.union.app.service.pk.service.*;
        import com.union.app.service.user.UserService;
        import com.union.app.util.time.TimeUtils;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.util.CollectionUtils;
        import org.springframework.util.ObjectUtils;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.RequestParam;
        import org.springframework.web.bind.annotation.RestController;

        import javax.transaction.Transactional;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Collection;
        import java.util.Date;
        import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询单个PK {


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
    AppService appService;


    @RequestMapping(path="/queryPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {

        List<DataSet> dataSets = new ArrayList<>();

        //榜单有可能被关闭
        AppResponse appResponse = pkService.checkPk(pkId,userId);
        if(!ObjectUtils.isEmpty(appResponse)){return appResponse;}
        //查询PK详情
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        UserEntity creator = userService.queryUserEntity(pkEntity.getUserId());
        PkDetail pkDetail = pkService.querySinglePk(pkEntity);


        Post post = postService.查询用户帖子(pkId,pkService.queryPkCreator(pkId).getUserId());
        boolean isCreatorPublish = false;
        if(!ObjectUtils.isEmpty(post) && post.getStatu().getKey() == PostStatu.上线.getStatu()) {
            isCreatorPublish = true;
            dataSets.add(new DataSet("cpost", post));
        }
        List<Post> posts = pkService.queryPkPost(pkId,0);

        dataSets.add(new DataSet("appImg",appService.查询背景(8)));
        if((creator.getUserType() == UserType.重点用户) || (AppConfigService.getConfigAsBoolean(ConfigItem.普通用户主题是否显示分享按钮和群组按钮)))
        {
            dataSets.add(new DataSet("group",appService.显示按钮(PkButtonType.群组)));
        }
        dataSets.add(new DataSet("post",appService.显示按钮(PkButtonType.榜帖)));
        dataSets.add(new DataSet("approve",appService.显示按钮(PkButtonType.审核)));
        if(pkService.isPkCreator(pkId,userId))
        {
            dataSets.add(new DataSet("approving",appService.显示按钮(PkButtonType.审核中)));
        }
        else
        {
            dataSets.add(new DataSet("complain",appService.显示按钮(PkButtonType.投诉)));
        }

        PostEntity postEntity = postService.查询用户帖(pkId,userId);
        if(ObjectUtils.isEmpty(postEntity))
        {
            dataSets.add(new DataSet("button",appService.显示按钮(PkButtonType.发布图册)));
        }
        else
        {
            if((creator.getUserType() == UserType.重点用户) || (AppConfigService.getConfigAsBoolean(ConfigItem.普通用户主题是否显示分享按钮和群组按钮)))
            {
                if(isCreatorPublish)
                {
                    dataSets.add(new DataSet("button",appService.显示按钮(PkButtonType.邀请图册)));
                }
            }
        }


        dataSets.add(new DataSet("pk",pkDetail));
        dataSets.add(new DataSet("imgBack",appService.查询背景(0)));
        dataSets.add(new DataSet("creatorImg",appService.查询背景(11)));
        dataSets.add(new DataSet("otherImg",appService.查询背景(12)));
        dataSets.add(new DataSet("topImg",appService.查询背景(13)));
//        dataSets.add(new DataSet("titleIcon",appService.查询背景(11)));
//        dataSets.add(new DataSet("cardBack",appService.查询背景(6)));
        dataSets.add(new DataSet("posts",posts));
        dataSets.add(new DataSet("page",0));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextPage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<Post> posts = pkService.queryPkPost(pkId,page+1);
        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("posts",posts));
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }

}
