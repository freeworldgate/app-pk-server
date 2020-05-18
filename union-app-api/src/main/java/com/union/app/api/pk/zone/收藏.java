package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.id.KeyGetter;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.Post;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.UserCollectionEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.app.AppService;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicItem;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostCacheService;
import com.union.app.service.pk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path="/pk")
public class 收藏 {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    PostService postService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    AppService appService;

    @Autowired
    PostCacheService postCacheService;

    @Autowired
    DynamicService dynamicService;

    @RequestMapping(path="/likeOrDisLike",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 收藏(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("postId") String postId,@RequestParam("isQueryerCollect") boolean isQueryerCollect) throws AppException, IOException {

        Date date = new Date();
        PostEntity postEntity = postService.查询帖子ById(pkId,postId);

        if(isQueryerCollect){

            // 修改成消息队列形式
            postCacheService.取消助力POST(pkId,userId,postId);
            dynamicService.收藏积分减1(pkId,postEntity.getUserId());

        }
        else
        {
            postCacheService.助力POST(pkId,userId,postId);
            dynamicService.收藏积分加1(pkId,postEntity.getUserId());

        }
        dynamicService.更新今日用户排名(pkId,postEntity.getUserId(),date);

        int score = AppConfigService.getConfigAsInteger(常量值.收藏一次的积分,100);

//        Post post = postService.查询帖子(pkId,postId,userId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",score));

    }
}
