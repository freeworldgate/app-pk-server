package com.union.app.api.pk.zone.post;

import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.OrderService;
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
public class Post审批 {


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



    @RequestMapping(path="/postApprove",method = RequestMethod.GET)
    public AppResponse getApplyOrder(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("type") int type,@RequestParam("page") int page) throws AppException, IOException {


        Post post = null;


        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
    }


    @RequestMapping(path="/postConfirm",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse cashierOrderConfirm1(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {


        KeyNameValue statu = null;

        return AppResponse.buildResponse(PageAction.执行处理器("success",statu));
    }



}
