package com.union.app.api.pk.管理;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.ApprovePost;
import com.union.app.domain.pk.CashierGroup;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.ApproveStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 审核榜帖 {

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
    UserInfoService userInfoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();

    @RequestMapping(path="/manageApprovingPosts",method = RequestMethod.GET)
    public AppResponse 查询审核榜帖(@RequestParam("userId") String userId) throws AppException, IOException {
        List<ApprovePost> approvePosts = new ArrayList<>();

        List<Post> posts = postService.查询需要审核的帖子();

        for(Post post:posts)
        {
            ApprovePost approvePost = new ApprovePost();
            approvePost.setPost(post);
            PkDetail pkDetail = pkDetailMap.get(post.getPkId());
            if(ObjectUtils.isEmpty(pkDetail))
            {
                pkDetail = pkService.querySinglePk(post.getPkId());
                pkDetail.setApproveMessage(approveService.获取审核人员消息(post.getPkId()));
                approvePost.setPk(pkDetail);
                pkDetailMap.put(post.getPkId(),pkDetail);
            }
            else
            {
                approvePost.setPk(pkDetail);
            }

            approvePosts.add(approvePost);
        }






        return AppResponse.buildResponse(PageAction.前端数据更新("posts",approvePosts));

    }

    @RequestMapping(path="/approvePost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse approvePost(@RequestParam("userId") String userId,@RequestParam("postId") String postId,@RequestParam("pkId") String pkId) throws AppException, IOException {
        //User认证


        postService.上线帖子(pkId,postId);
        dynamicService.已审核(pkId,postId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));


    }

    @RequestMapping(path="/hiddenPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse hiddenPost(@RequestParam("userId") String userId,@RequestParam("postId") String postId,@RequestParam("pkId") String pkId) throws AppException, IOException {



        PostEntity postEntity = postService.查询帖子ById(pkId,postId);
        postEntity.setApproveStatu(ApproveStatu.处理过);
        daoService.updateEntity(postEntity);



        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }



}
