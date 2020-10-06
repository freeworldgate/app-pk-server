package com.union.app.api.pk.管理;

import com.union.app.domain.pk.PkButtonType;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.PostStatu;
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
public class 查询单个预置PK {


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


    @RequestMapping(path="/queryPreInnerPk",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("password") String password,@RequestParam("pkId") String pkId) throws AppException, IOException, InterruptedException {
        appService.验证Password(password);
        List<DataSet> dataSets = new ArrayList<>();

        //榜单有可能被关闭
        pkService.checkPk(pkId,"");

        //查询PK详情
        PkDetail pkDetail = pkService.querySinglePk(pkId);
        pkDetail.setUserBack(appService.查询背景(8));
        List<Post> posts = pkService.queryPrePkPost(pkId,0);




        Post post = postService.查询用户帖子(pkId,pkService.queryPkCreator(pkId).getUserId());
        dataSets.add(new DataSet("cpost", post));

        dataSets.add(new DataSet("group",appService.显示按钮(PkButtonType.群组)));
        dataSets.add(new DataSet("post",appService.显示按钮(PkButtonType.榜帖)));
        dataSets.add(new DataSet("approve",appService.显示按钮(PkButtonType.审核)));
        dataSets.add(new DataSet("approving",appService.显示按钮(PkButtonType.审核中)));

        dataSets.add(new DataSet("pk",pkDetail));
        dataSets.add(new DataSet("imgBack",appService.查询背景(0)));
        dataSets.add(new DataSet("posts",posts));
        dataSets.add(new DataSet("page",0));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextPreInnerPage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("password") String password,@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {
        appService.验证Password(password);
        //页数不断递增，但是只有一百页。
        List<Post> posts = pkService.queryPrePkPost(pkId,page+1);
        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("posts",posts));
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }

}
