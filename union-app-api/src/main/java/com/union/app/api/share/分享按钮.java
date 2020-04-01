package com.union.app.api.share;

import com.union.app.domain.pk.share.ShareWindow;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.share.ShareService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 分享按钮 {


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
    ShareService shareService;

    @RequestMapping(path="/shareMenu",method = RequestMethod.GET)
    public AppResponse 分享界面(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("fromUser") String fromUser) throws AppException, IOException {

        //用户点击分享按钮
        System.out.println("--pkId--" + pkId);
        System.out.println("--userId--" + userId);
        System.out.println("--fromUser--" + fromUser);



        ShareWindow shareWindow = shareService.获取分享窗口数据(pkId,userId);




        //对于不在传播链上的用户来说，我不需要你分享。直接返回服务器异常

        return AppResponse.buildResponse(PageAction.执行处理器("success",shareWindow));

    }


    @RequestMapping(path="/showShareMenu",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("fromUser") String fromUser) throws AppException, IOException {

        //要做校验，用户是否在传播链上面。













        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        //查询PK详情
//        PkDetail pkDetail = pkService.querySinglePk(pkId);
//        List<Post> posts = pkService.queryPkPost(userId,pkId,1);
//        boolean isUserPublish = !ObjectUtils.isEmpty(postService.查询用户帖(pkId,userId));

        List<DataSet> dataSets = new ArrayList<>();
//        dataSets.add(new DataSet("isUserPublish",isUserPublish));
//        dataSets.add(nshareMenuew DataSet("pkDetail",pkDetail));
//        dataSets.add(new DataSet("posts",posts));
//        dataSets.add(new DataSet("page",1));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

}
