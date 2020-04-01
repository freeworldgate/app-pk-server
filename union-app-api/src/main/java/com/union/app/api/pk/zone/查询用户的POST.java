package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 查询用户的POST {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    PostService postService;

    @Autowired
    OrderService orderService;


    @RequestMapping(path="/queryUserPost",method = RequestMethod.GET)
    public AppResponse 查询用户的POST(@RequestParam("pkId") String pkId, @RequestParam("userId") String userId) throws AppException, IOException {






            Post post = postService.查询用户帖子(pkId,userId);
            if(!ObjectUtils.isEmpty(post)){

                return AppResponse.buildResponse(PageAction.执行处理器("userPost",post));
            }
            else
            {
                return AppResponse.buildResponse(PageAction.执行处理器("createPost",""));
            }













    }


}
