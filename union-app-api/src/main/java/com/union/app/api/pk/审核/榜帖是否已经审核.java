package com.union.app.api.pk.审核;

import com.union.app.entity.pk.ApproveStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
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

@RestController
@RequestMapping(path="/pk")
public class 榜帖是否已经审核 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;



    @Autowired
    ApproveService approveService;

    @RequestMapping(path="/isPostApproved",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询审核信息(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {


            PostEntity postEntity    = postService.查询帖子ById(postId);

            if(postEntity.getStatu() == PostStatu.审核中)
            {
                if(postEntity.getApproveStatu() == ApproveStatu.未处理)
                {
                    return AppResponse.buildResponse(PageAction.执行处理器("noApprove","/pages/pk/editApproveComment/editApproveComment?pkId=" + pkId + "&postId=" + postEntity.getPostId()));
                }


            }
            return AppResponse.buildResponse(PageAction.前端数据更新("verfiy",true));




    }

    @RequestMapping(path="/goApproving",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 请求审核(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {





            PostEntity postEntity    = postService.查询帖子ById(postId);
            if(  (postEntity.getStatu() == PostStatu.审核中))
            {
                return AppResponse.buildResponse(PageAction.执行处理器("noApprove","/pages/pk/editApproveComment/editApproveComment?pkId=" + pkId + "&postId=" + postEntity.getPostId()));

            }
            else
            {
                return AppResponse.buildResponse(PageAction.前端数据更新("verfiy",true));
            }

    }

}
