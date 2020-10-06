package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.PkPageCacheEntity;
import com.union.app.entity.pk.PkPageRefreshTimeEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.UserCollectionEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PostCacheService {

    //pkid:List<Post>
    private static Map<String,Map<String,String>> postCacheTime = new ConcurrentHashMap<>();
    private static Map<String,Map<String,List<Post>>> postCache = new ConcurrentHashMap<>();

    private static String nodeSeq = UUID.randomUUID().toString();

    private static Map<String,HashMap<String,String>> refreshSeqs = new ConcurrentHashMap<>();

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    DynamicService dynamicService;

    /**
     * 查询指定页的POST数据
     * @param pkId
     * @param page
     * @return
     */
    public List<Post> getPosts(String pkId,int page) throws IOException {

        List<Post> pagePosts = new ArrayList<>();





        return pagePosts;
    }




    /**
     * 查询指定页的POST数据
     * @param pkId
     * @param page
     * @return
     */
    public List<Post> getPostPage(String userId,String pkId,int page) throws IOException {
//        return getRandomPage(pkId);
//
        List<Post> pagePosts = new ArrayList<>();



        String pkPage = "PAGE-" + String.valueOf(page);
        Map<String,String> pageRefreshTime = postCacheTime.get(pkId);
        if(MapUtils.isEmpty(pageRefreshTime)){
            pageRefreshTime = new HashMap<>();
            postCacheTime.put(pkId,pageRefreshTime);
        }
        Map<String,List<Post>> pkAllPages = postCache.get(pkId);
        if(MapUtils.isEmpty(pkAllPages)){
            pkAllPages = new HashMap<>();
            postCache.put(pkId,pkAllPages);
        }

        String localLastRefreshTime = pageRefreshTime.get(pkPage);
        String lastRefreshTime = queryPageLastModifyTime(pkId,pkPage);

        if(org.apache.commons.lang.StringUtils.equals(lastRefreshTime,localLastRefreshTime)){


            List<Post> posts = pkAllPages.get(pkPage);
            if(CollectionUtils.isEmpty(posts)){
                posts = getRandomPage(pkId);
                pkAllPages.put(pkPage,posts);
            }
            pagePosts.addAll(posts);
        }
        else
        {
            //更新时间不一致，查询数据库更新页面
            List<Post> posts = syncRefreshCachePost(pkId,pkPage);
            pagePosts.addAll(posts);
            pkAllPages.put(pkPage,posts);
            Map<String,String> refreshTime = postCacheTime.get(pkId);
            refreshTime.put(pkPage,lastRefreshTime);
        }




        return pagePosts;
    }

    private List<Post> syncRefreshCachePost(String pkId, String pkPage) throws UnsupportedEncodingException {

        List<Post> posts = new ArrayList<>();

        EntityFilterChain filterChain = EntityFilterChain.newFilterChain(PkPageCacheEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("page",CompareTag.Equal,pkPage)
                .orderByFilter("priority",OrderTag.ASC);
        List<PkPageCacheEntity> pageCache = daoService.queryEntities(PkPageCacheEntity.class,filterChain);

        if(!CollectionUtils.isEmpty(pageCache)) {
            for (PkPageCacheEntity pkPageCacheEntity : pageCache) {
                String postId = pkPageCacheEntity.getPostId();
                Post post = postService.查询帖子(pkId,postId, org.apache.commons.lang.StringUtils.EMPTY);
                posts.add(post);
            }
        }
        else
        {
            posts.addAll(getRandomPage(pkId));
        }
        return posts;
    }

    private String queryPageLastModifyTime(String pkId, String pkPage) {
        EntityFilterChain filterChain = EntityFilterChain.newFilterChain(PkPageRefreshTimeEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("page",CompareTag.Equal,pkPage);

        PkPageRefreshTimeEntity pkPageRefreshTimeEntity = daoService.querySingleEntity(PkPageRefreshTimeEntity.class,filterChain);

        if(!ObjectUtils.isEmpty(pkPageRefreshTimeEntity)){
            return pkPageRefreshTimeEntity.getRefreshTime();
        }

        return org.apache.commons.lang.StringUtils.EMPTY;




    }

    private List<Post> getRandomPage(String pkId) throws UnsupportedEncodingException {

        List<Post> posts = new ArrayList<>();
        EntityFilterChain filterChain = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .pageLimitFilter(1,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByRandomFilter();
        List<PostEntity> postEntities = daoService.queryEntities(PostEntity.class,filterChain);
        for(PostEntity postEntity:postEntities){
            Post post = postService.查询帖子(pkId,postEntity.getPostId(), org.apache.commons.lang.StringUtils.EMPTY);
            posts.add(post);
        }
        return posts;
    }



    //助力记录系统
    private void genUserLike(String pkId, String userId, List<Post> posts) {

        List<Object> postIds = new ArrayList<>();
        if(posts.size()  ==  0){return;}
        for(Post post:posts)
        {
            postIds.add(post.getPostId());
        }

        EntityFilterChain filterChain = EntityFilterChain.newFilterChain(UserCollectionEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .inFilter("postId",postIds);

        List<UserCollectionEntity> userCollectionEntities = daoService.queryEntities(UserCollectionEntity.class,filterChain);
        Map<String,UserCollectionEntity> userCollectionEntityMap = new HashMap<>();
        for(UserCollectionEntity userCollectionEntity:userCollectionEntities){
            userCollectionEntityMap.put(userCollectionEntity.getPostId(),userCollectionEntity);
        }
        for(Post post:posts){
            UserCollectionEntity userCollectionEntity = userCollectionEntityMap.get(post.getPostId());
            if(!ObjectUtils.isEmpty(userCollectionEntity)){
                post.setQueryerCollect(true);
            }
            else
            {
                post.setQueryerCollect(false);
            }
    }
}

    public void 助力POST(String pkId, String userId, String postId) throws IOException {

        UserCollectionEntity userCollectionEntity = new UserCollectionEntity();
        userCollectionEntity.setPkId(pkId);
        userCollectionEntity.setUserId(userId);
        userCollectionEntity.setPostId(postId);
        daoService.insertEntity(userCollectionEntity);
    }

    public void 取消助力POST(String pkId, String userId, String postId) throws UnsupportedEncodingException {

        EntityFilterChain filterChain = EntityFilterChain.newFilterChain(UserCollectionEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postId);
        UserCollectionEntity userCollectionEntity = daoService.querySingleEntity(UserCollectionEntity.class,filterChain);
        if(!ObjectUtils.isEmpty(userCollectionEntity)){
            daoService.deleteEntity(userCollectionEntity);
        }

    }

}
