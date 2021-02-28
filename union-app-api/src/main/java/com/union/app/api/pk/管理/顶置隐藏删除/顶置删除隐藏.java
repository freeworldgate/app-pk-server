package com.union.app.api.pk.管理.顶置隐藏删除;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 顶置删除隐藏 {


    @Autowired
    AppDaoService daoService;

    @Autowired
    LocationService locationService;

    @Autowired
    PostService postService;

    @Autowired
    AppService appService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @Autowired
    UserDynamicService userDynamicService;

    @RequestMapping(path="/topPostByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse topPostByManager(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {

                appService.checkManager(userId);

                return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }

    @RequestMapping(path="/setTopPostTimeByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setTopPostTimeByManager(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("value") int value) throws AppException, IOException {


            appService.checkManager(userId);

            Map<String,Object> map = new HashMap<>();
            map.put("topPostId",postId);
            map.put("topPostSetTime",System.currentTimeMillis());
            map.put("topPostTimeLength",value*1L);
            daoService.updateColumById(PkEntity.class,"pkId",pkId,map);

            Post post = postService.查询帖子(postId);

            return AppResponse.buildResponse(PageAction.执行处理器("success",post));


    }


    @RequestMapping(path="/removePostByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removePostByManager(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {
            appService.checkManager(userId);
            PostEntity postEntity = postService.查询帖子ById(postId);
            postService.删除打卡信息(postId);
            pkUserDynamicService.卡点用户打卡次数减一(pkId,postEntity.getUserId());
            userDynamicService.用户总打榜次数减一(postEntity.getUserId());

            return AppResponse.buildResponse(PageAction.执行处理器("success",""));


    }



    @RequestMapping(path="/hiddenPostByManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse hiddenPostByManager(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {

        appService.checkManager(userId);
        postService.隐藏打卡信息(postId);
        locationService.隐藏数量加1(pkId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }






}
