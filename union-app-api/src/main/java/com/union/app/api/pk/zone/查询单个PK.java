package com.union.app.api.pk.zone;

        import com.union.app.domain.pk.PkDetail;
        import com.union.app.domain.pk.Post;
        import com.union.app.domain.pk.UserCode;
        import com.union.app.domain.pk.apply.KeyNameValue;
        import com.union.app.domain.pk.integral.UserIntegral;
        import com.union.app.domain.pk.审核.ApproveUser;
        import com.union.app.domain.user.User;
        import com.union.app.domain.工具.RandomUtil;
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
    UserInfoService userInfoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;


    @RequestMapping(path="/queryPk",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("fromUser") String fromUser) throws AppException, IOException {


        Date currentDate = new Date();
        String imgBack = RandomUtil.getRandomBackImg();
        //查询PK详情
        PkDetail pkDetail = pkService.querySinglePk(pkId,currentDate);
        List<Post> posts = pkService.queryPkPost(userId,pkId,1,currentDate);
        boolean isUserPublish = !ObjectUtils.isEmpty(postService.查询用户帖(pkId,userId));
        User creator = pkService.queryPkCreator(pkId);


//        List<UserIntegral> userIntegrals = dynamicService.查询今日审核用户列表(pkId,currentDate);
//        UserIntegral creatorIntegral = dynamicService.查询榜主审核信息(pkId);
//        userIntegrals.add(0,creatorIntegral);

        List<ApproveUser> userIntegrals = approveService.查询当前户审核用户列表(pkId,currentDate);


        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("isUserPublish",isUserPublish));
        dataSets.add(new DataSet("pkDetail",pkDetail));
        dataSets.add(new DataSet("date",TimeUtils.dateStr(currentDate)));
        dataSets.add(new DataSet("creator",creator));
        dataSets.add(new DataSet("userIntegrals",userIntegrals));
        dataSets.add(new DataSet("imgBack",imgBack));
//        dataSets.add(new DataSet("creatorIntegral",creatorIntegral));



        dataSets.add(new DataSet("posts",posts));
        dataSets.add(new DataSet("page",1));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextPage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {
        Date currentDate = new Date();
        //页数不断递增，但是只有一百页。
        List<Post> posts = pkService.queryPkPost(userId,pkId,page+1,currentDate);
        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("posts",posts));
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }

}
