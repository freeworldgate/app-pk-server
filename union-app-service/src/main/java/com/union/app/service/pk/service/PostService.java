package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.PostImage;
import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.entity.pk.*;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.pkuser.PkDynamicService;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.pk.service.文字背景.TextService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class PostService {


    @Autowired
    AppDaoService daoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    UserService userService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;


    @Autowired
    PkService pkService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    PkDynamicService pkDynamicService;

    @Autowired
    KeyService keyService;

    @Autowired
    TextService textService;

    @Autowired
    PkCacheService pkCacheService;

    public String 打卡(String pkId,String userId,String title,List<String> images,String backId) throws IOException, AppException
    {

        PkEntity pkEntity = locationService.querySinglePkEntityWithoutCache(pkId);
        String postId = IdGenerator.getPostId();
        PostEntity postEntity = new PostEntity();
        postEntity.setPkId(pkId);
        postEntity.setPkName(pkEntity.getName());
        postEntity.setPostId(postId);
        postEntity.setUserId(userId);
        postEntity.setTopic(noActiveTitle(title)?"...":title);
        TextBack textBack = textService.查询TextBackEntity(backId);
        postEntity.setBackColor(textBack.getBackColor());
        postEntity.setFontColor(textBack.getFontColor());
        postEntity.setBackUrl(textBack.getBackUrl());


        postEntity.setStatu(PostStatu.显示);
        postEntity.setPostTimes(pkUserDynamicService.计算打卡次数(pkId,userId)+1);
        postEntity.setTime(System.currentTimeMillis());
        List<PostImageEntity> postImageEntities = getLegalImgUrl(images,pkId,postId);
        postEntity.setImgNum(CollectionUtils.isEmpty(postImageEntities)?0:images.size());
        keyService.pk图片总量递增(pkId,postEntity.getImgNum());
        daoService.insertEntity(postEntity);
        postImageEntities.forEach(image->{
            daoService.insertEntity(image);
        });
        daoService.insertEntity(postEntity);
        pkUserDynamicService.记录用户卡点打卡时间(pkId,userId);
        pkUserDynamicService.卡点用户打卡次数加一(pkId,userId);
        userDynamicService.用户总打榜次数加一(userId);
        pkDynamicService.卡点打卡人数更新(pkId,userId);


        return postId;
    }


    private boolean noActiveTitle(String title) {
        if(org.apache.commons.lang.StringUtils.isBlank(title)){return true;}

        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(title,"undefined")){return true;}
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(title,"Nan")){return true;}
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(title,"null")){return true;}

        return false;


    }

    private List<PostImageEntity> getLegalImgUrl(List<String> images,String pkId,String postId) {
        List<PostImageEntity> postImageEntities = new ArrayList<>();
        images.forEach(img->{
            PostImageEntity postImageEntity = new PostImageEntity();
            postImageEntity.setImgId(IdGenerator.getImageId());
            postImageEntity.setImgUrl(getLegalImgUrl(img));
            postImageEntity.setPkId(pkId);
            postImageEntity.setPostId(postId);
            postImageEntity.setTime(System.currentTimeMillis());
            if(!StringUtils.isEmpty(postImageEntity.getImgUrl()))
            {
                postImageEntities.add(postImageEntity);
            }

        });
        return postImageEntities;








    }



    private String getLegalImgUrl(String image) {
        String reg1 = "\\[";
        String reg2 = "]";
        String reg3 = "\"";
//        String reg4 = AppConfigService.getConfigAsString(常量值.OSS基础地址,"https://oss.211shopper.com");
        image = image.replaceAll(reg3,"").replaceAll(reg1,"").replaceAll(reg2,"").trim();
        return image;
    }



    public Post 查询顶置帖子(PkEntity pkEntity) {


        if(!org.apache.commons.lang.StringUtils.isBlank(pkEntity.getTopPostId()))
        {
            PostEntity postEntity = this.查询帖子ById(pkEntity.getTopPostId());
            if(ObjectUtils.isEmpty(postEntity)){return null;}
            Post post = translate(postEntity);
            return post;
        }
        return null;
    }




    public Post 查询帖子(String pkId,String postId,String queryUserId) {

        PostEntity postEntity = this.查询帖子ById(postId);
        if(ObjectUtils.isEmpty(postEntity)){return null;}
        Post post = translate(postEntity);
        return post;
    }


    public Post translate(PostEntity postEntity){
        Post post = new Post();
        post.setPkId(postEntity.getPkId());
        post.setPkTopic(postEntity.getPkName());
        post.setPostId(postEntity.getPostId());
        post.setTime(TimeUtils.convertTime(postEntity.getTime()));
        post.setCreator(userService.queryUser(postEntity.getUserId()));
        post.setTopic(postEntity.getTopic());
        post.setBackColor(postEntity.getBackColor());
        post.setFontColor(postEntity.getFontColor());
        post.setBackUrl(postEntity.getBackUrl());
        post.setPostTimes(postEntity.getPostTimes());
        post.setPtime(TimeUtils.convertPostTime(postEntity.getTime()));
        post.setPostImages(postEntity.getImgNum()<1?new ArrayList<>():getPostImages(postEntity.getPostId(),postEntity.getPkId()));
        return post;
    }




    public List<PostImage> getPostImages(String postId,String pkId) {
        List<PostImage> postImages = new ArrayList<>();
        if(StringUtils.isEmpty(postId)||StringUtils.isEmpty(pkId)) {return postImages; }

        postImages.addAll(keyService.查询榜帖图片(pkId,postId));

        return postImages;
    }





    public PostEntity 查询用户帖(String pkId, String userId) {
        if(StringUtils.isEmpty(userId)){return null;}


        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter);
        return postEntity;
    }











    public PostEntity 查询帖子ById(String postId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter1);

        return postEntity;
    }


    public void 删除打卡信息(String postId) {
        //删除缓存数据：PKEntity   PostEntity  PostImageEntity 三个缓存

        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,cfilter);
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId);
        List<PostImageEntity> postImageEntities = daoService.queryEntities(PostImageEntity.class,filter);


        daoService.deleteEntity(postEntity);
        if(!CollectionUtils.isEmpty(postImageEntities)){
            postImageEntities.forEach(postImageEntity -> {
                daoService.deleteEntity(postImageEntity);
            });
        }
        //如果用户删除的卡册是顶置卡册，要把顶置清除掉
        PkEntity pkEntity = locationService.querySinglePkEntity(postEntity.getPkId());
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(pkEntity.getTopPostId(),postEntity.getPostId()))
        {
            Map<String,Object> map = new HashMap<>();
            map.put("topPostId","");
            map.put("topPostSetTime",0L);
            daoService.updateColumById(pkEntity.getClass(),"pkId",pkEntity.getPkId(),map);

        }
        keyService.pk图片总量递减(pkEntity.getPkId(),postEntity.getImgNum());

    }



    public void 隐藏打卡信息(String postId) {



        Map<String,Object> map = new HashMap<>();
        map.put("statu",PostStatu.隐藏);
        daoService.updateColumById(PostEntity.class,"postId",postId,map);



    }


    public void 移除隐藏打卡信息(String postId) {
        //删除缓存数据：PKEntity   PostEntity  PostImageEntity 三个缓存


        Map<String,Object> map = new HashMap<>();
        map.put("statu",PostStatu.显示);
        daoService.updateColumById(PostEntity.class,"postId",postId,map);

    }


    public Post 查询最新的图片列表(PkEntity pk) {


        Post post = new Post();
        List<PostImage> postImages = new ArrayList<>();
        if(CollectionUtils.isEmpty(postImages))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pk.getPkId())
                    .pageLimitFilter(1,9)
                    .orderByFilter("time",OrderTag.DESC);
            List<PostImageEntity> postImageEntity = daoService.queryEntities(PostImageEntity.class,filter);
            postImageEntity.forEach(img->{
                PostImage postImage = new PostImage();
                postImage.setImgUrl(img.getImgUrl());
                postImage.setImageId(img.getImgId());
                postImage.setPkId(img.getPkId());
                postImage.setPostId(img.getPostId());
                postImage.setTime(TimeUtils.convertTime(img.getTime()));
                postImages.add(postImage);
            });
        }
        post.setPostImages(postImages);
        return CollectionUtils.isEmpty(postImages)?null:post;

    }

    public List<Post> 查询用户发帖列表(String userId, String pkId, int page) {
        List<Post> posts = new ArrayList<>();
        List<PostEntity> postEntities = this.查询用户帖列表(userId,pkId,page);
        postEntities.forEach(postEntity -> {
            posts.add(this.translate(postEntity));
        });
        return posts;

    }

    private List<PostEntity> 查询用户帖列表(String userId, String pkId, int page) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,10)
                .orderByFilter("time",OrderTag.DESC);
        List<PostEntity> postEntities = daoService.queryEntities(PostEntity.class,cfilter);
        return postEntities;
    }


    public void 批量查询POST(List<PkDetail> pkDetails) {

        pkDetails.forEach(pkDetail -> {
            if(org.apache.commons.lang.StringUtils.isNotBlank(pkDetail.getTopPostId()) && ((System.currentTimeMillis() - pkDetail.getTopPostSetTime()) < pkDetail.getTopPostTimeLength() * 60 * 1000L))
            {
                Collection<PostImage> postImages =  keyService.查询榜帖图片(pkDetail.getPkId(),pkDetail.getTopPostId());
                List<PostImage> postImagesList = new ArrayList<>();
                postImagesList.addAll(postImages);
                Post post = new Post();
                post.setPostImages(postImagesList);
                pkDetail.setTopPost(post);
            }
        });

    }
}
