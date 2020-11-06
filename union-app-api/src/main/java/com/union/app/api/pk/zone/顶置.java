package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkStatu;
import com.union.app.entity.pk.PkUnlockEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 顶置 {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    UserService userService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;



    @Autowired
    AppService appService;

    @RequestMapping(path="/topPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 顶置(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {



        if(pkService.isPkCreator(pkId,userId))
        {
            pkService.修改首页图册(pkId,postId);
            Post post = postService.查询帖子(pkId,postId,userId);

            return AppResponse.buildResponse(PageAction.前端数据更新("cpost",post));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非法操作","不具备权限"));
        }






    }


}
