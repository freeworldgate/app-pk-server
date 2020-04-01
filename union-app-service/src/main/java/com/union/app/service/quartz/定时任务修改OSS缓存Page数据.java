package com.union.app.service.quartz;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.id.KeyGetter;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class 定时任务修改OSS缓存Page数据 {



    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    AppDaoService appDaoService;

    @Autowired
    PostService postService;

    @Autowired
    OssStorage ossStorage;


    @Scheduled(cron = "*/60 * * * * ?") // 每分钟执行一次  刷新整个PKID的所有PAGE
    public void work() throws Exception {


        //需要设置开关：













//        (PK01-PAGE-CACHE)    -   {(PAGE-1):201909080232}
//        查询PKID的第PAGE页的刷新纪录


//        int currentPostIdSeq = cacheStorage.查询已发布帖子最大序列值Seq(pkId);


//        ossStorage.setList(pkPostPageKey,Post.class);

//        写入刷新纪录
//        redisStringUtil.setMapKey(KeyGetter.分页刷新标识Key(pkId),"PAGE-" + page,String.valueOf(System.currentTimeMillis()));











    }




    private List<Post> selectPost(String pkId, int page) throws IOException {

        int currentPostIdSeq = cacheStorage.查询已发布帖子最大序列值Seq(pkId);
        Random rd = new Random();
        List<Post> posts = new ArrayList<>();
        while(posts.size()<30) {
            int seq = rd.nextInt(currentPostIdSeq);
            //0是不存在的序列。
            seq = seq==0?1:seq;
            String postId = KeyGetter.拼接POSTID(pkId,seq);

            if(redisStringUtil.contains(KeyGetter.异常的POSTID列表(pkId),postId)){
                continue;
            }

            boolean isPostExist = postService.帖子是否存在(postId);
            if(!isPostExist){
                //把内容为空的帖子记录为被覆盖从用的POSTID；
                redisStringUtil.push(KeyGetter.异常的POSTID列表(pkId),postId);
            }
            else
            {
//                Post post = postService.查询封面帖子(postId);
//                if(!ObjectUtils.isEmpty(post)){
//                    posts.add(post);
//                }
            }

        }
        return posts;
    }













}
