package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.*;
import com.union.app.common.id.KeyGetter;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.审核.ApproveCommentEntity;
import com.union.app.entity.用户.UserKvEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.DynamicService;
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
    ApproveService approveService;

    @Autowired
    ComplainService complainService;


    @Autowired
    PkCacheService pkCacheService;
    public String 预置帖子(String pkId,String title,List<String> images) throws IOException, AppException
    {
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        User creator = pkService.queryPkCreator(pkId);
        PostEntity creatorPostEntity = this.查询用户帖(pkId,creator.getUserId());

        String postId = IdGenerator.getPostId();
        String wxCode = appService.生成二维码(pkId,postId);
        PostEntity postEntity = new PostEntity();

        postEntity.setPkId(pkId);
        postEntity.setPkType(pkEntity.getPkType());
        postEntity.setWxCode(wxCode);
        postEntity.setPostId(postId);
        postEntity.setUserId(ObjectUtils.isEmpty(creatorPostEntity)?creator.getUserId():appService.随机用户());
        postEntity.setTopic(noActiveTitle(title)?"...":title);
        postEntity.setImgNum(images.size());
        postEntity.setTime(System.currentTimeMillis());
//        postEntity.setLastModifyTime(TimeUtils.currentTime());
        postEntity.setStatu(PostStatu.审核中);
        postEntity.setRejectTimes(0);


        postEntity.setApproveStatu(ApproveStatu.未处理);



//        StringBuffer stringBuffer = new StringBuffer();
//        for(String img:images){
//            stringBuffer.append(getLegalImgUrl(img));
//            stringBuffer.append(";");
//
//        }
//        postEntity.setImgUrls(stringBuffer.toString());
        List<PostImageEntity> postImageEntities = getLegalImgUrl(images,pkId,postId);
        daoService.insertEntity(postEntity);
        postImageEntities.forEach(image->{
            daoService.insertEntity(image);
        });



        this.用户转发审批(postEntity);
        dynamicService.设置帖子的审核用户(pkId,postId);
//          审核通过
        this.上线帖子(pkId,postId);
        dynamicService.已审核(pkId,postId);







        return postId;
    }

    public String 创建帖子(String pkId,String userId,String title,List<String> images) throws IOException, AppException
    {

        String postId = IdGenerator.getPostId();
        String wxCode = appService.生成二维码(pkId,postId);
        PostEntity postEntity = new PostEntity();
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        postEntity.setPkId(pkId);
        postEntity.setWxCode(wxCode);
        postEntity.setPkType(pkEntity.getPkType());
        postEntity.setPostId(postId);
        postEntity.setUserId(userId);
        postEntity.setTopic(noActiveTitle(title)?"...":title);
        postEntity.setImgNum(images.size());
        postEntity.setTime(System.currentTimeMillis());
//        postEntity.setLastModifyTime(TimeUtils.currentTime());
        postEntity.setStatu(PostStatu.审核中);
        postEntity.setRejectTimes(0);

        postEntity.setApproveStatu(ApproveStatu.未处理);
        List<PostImageEntity> postImageEntities = getLegalImgUrl(images,pkId,postId);
        daoService.insertEntity(postEntity);
        postImageEntities.forEach(image->{
            daoService.insertEntity(image);
        });
        daoService.insertEntity(postEntity);

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
            postImageEntity.setStatu(PostStatu.审核中);
            postImageEntities.add(postImageEntity);

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

//    public Post 查询帖子(String postId,String queryerId) throws UnsupportedEncodingException {
//
//        Post post = new Post();
//
//        PostEntity postEntity = getPostEntityById(postId);
//
//        if((!StringUtils.isEmpty(queryerId)) && (!org.apache.commons.lang.StringUtils.equals(postEntity.getUserId(),queryerId))){
//
//            post.setQueryerCollect(isUserCollectPost(postId,queryerId));
//
//        }
//
//        post.setPkId(postEntity.getPkId());
//        post.setPostId(postEntity.getPostId());
//        post.setCreator(userService.queryUser(postEntity.getUserId()));
//        post.setTopic(new String(postEntity.getTopic(),"UTF-8"));
//        post.setDynamic(getPostDynamic(postEntity.getPostId(),postEntity.getPkId()));
//        post.setPostImages(getPostImages(postEntity.getPostId(),postEntity.getPkId()));
//        post.setStatu(new KeyNameValue(postEntity.getStatu().getStatu(),postEntity.getStatu().getStatuStr()));
//        return post;
//    }

    public Post 查询帖子(String pkId,String postId,String queryerId) throws UnsupportedEncodingException {

        PostEntity postEntity = this.查询帖子ById(postId);
        if(ObjectUtils.isEmpty(postEntity)){return null;}
        Post post = translate(postEntity);
//        post.setBackUrl(appService.查询背景(4));
        return post;
    }


    public Post translate(PostEntity postEntity) throws UnsupportedEncodingException {
        Post post = new Post();
        post.setPkId(postEntity.getPkId());
        post.setPostId(postEntity.getPostId());
        post.setWxCode(StringUtils.isEmpty(postEntity.getWxCode())?appService.查询背景(9):postEntity.getWxCode());
        post.setRejectTimes(postEntity.getRejectTimes());
        post.setMaxRejectTimes(AppConfigService.getConfigAsInteger(ConfigItem.Post最大修改次数));
        post.setRejectText(postEntity.getRejectTextBytes() == null?"":postEntity.getRejectTextBytes());
        post.setTime(TimeUtils.convertTime(postEntity.getTime()));
        post.setCreator(userService.queryUser(postEntity.getUserId()));
        post.setTopic(postEntity.getTopic());
        post.setDynamic(getPostDynamic(postEntity.getPostId(),postEntity.getPkId()));
        List<PostImage> imgs =  getPostImages(postEntity.getPostId(),postEntity.getPkId());
        if(postEntity.getStatu() == PostStatu.上线) {
            Collections.shuffle(imgs);
        }
        post.setPostImages(imgs);
        post.setStatu(new KeyNameValue(postEntity.getStatu().getStatu(),postEntity.getStatu().getStatuStr()));
//        post.setSelfComment(org.apache.commons.lang.StringUtils.isBlank(postEntity.getSelfComment())? org.apache.commons.lang.StringUtils.EMPTY :postEntity.getSelfComment());
//        post.setSelfCommentTime(TimeUtils.convertTime(postEntity.getSelfCommentTime()));
//        post.setScan(dynamicService.查询扫码次数(postEntity.getPkId(),postEntity.getPostId()));
        return post;
    }




    public List<PostImage> getPostImages(String postId,String pkId) {
        List<PostImage> postImages = new ArrayList<>();
        if(StringUtils.isEmpty(postId)||StringUtils.isEmpty(pkId)) {return postImages; }

        postImages.addAll(EntityCacheService.查询榜帖图片(pkId,postId));

        if(CollectionUtils.isEmpty(postImages))
        {

            EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("postId",CompareTag.Equal,postId);
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
            if(!CollectionUtils.isEmpty(postImageEntity) && postImageEntity.get(0).getStatu() == PostStatu.上线)
            {
                EntityCacheService.保存图片(pkId,postId,postImages);
            }
        }



        return postImages;
    }


    private PostDynamic getPostDynamic(String postId, String pkId) {

        return new PostDynamic();
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


    public Post 查询用户帖子(String pkId, String userId) throws UnsupportedEncodingException {

            PostEntity userPostEntity = 查询用户帖(pkId,userId);
            if(ObjectUtils.isEmpty(userPostEntity)){
                return null;
            }
            else {

                return this.translate(userPostEntity);
            }
    }







    public Post 查询类型帖子(String pkId, String userId, int type, int page) throws AppException, UnsupportedEncodingException {

        String creator = pkService.querySinglePkEntity(pkId).getUserId();
        if(!org.apache.commons.lang.StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}


        Post post = new Post();


        PostEntity postEntity = this.查询下一个PostEntity(pkId,type,page);
        if(org.springframework.util.ObjectUtils.isEmpty(postEntity))
        {
            post.setStatu(new KeyNameValue(PostStatu.无内容.getStatu(), PostStatu.无内容.getStatuStr()));
        }
        else
        {
            post.setPkId(postEntity.getPkId());
            post.setPostId(postEntity.getPostId());
            post.setCreator(userService.queryUser(postEntity.getUserId()));
            post.setTopic(postEntity.getTopic());
            post.setDynamic(getPostDynamic(postEntity.getPostId(),postEntity.getPkId()));
            post.setPostImages(getPostImages(postEntity.getPostId(),postEntity.getPkId()));
            post.setStatu(new KeyNameValue(postEntity.getStatu().getStatu(), postEntity.getStatu().getStatuStr()));
        }


        return post;

    }

    private PostEntity 查询下一个PostEntity(String pkId, int type, int page) {
        EntityFilterChain filter = null;
        if(type == PostStatu.上线.getStatu()){
            filter = EntityFilterChain.newFilterChain(PostEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("statu",CompareTag.Equal,PostStatu.上线)
                    .pageLimitFilter(page,1);;
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PostEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("statu",CompareTag.Equal,  PostStatu.下线)
                    .pageLimitFilter(page,1);;
        }

        List<PostEntity> postEntities = daoService.queryEntities(PostEntity.class,filter);

        return CollectionUtils.isEmpty(postEntities)?null:postEntities.get(0);


    }

    public KeyNameValue 修改榜帖状态(String pkId, String postId, String userId) throws AppException {
        String creator = pkService.querySinglePkEntity(pkId).getUserId();
        if(!org.apache.commons.lang.StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}

        KeyNameValue keyNameValue = null;

        PostEntity postEntity = this.查询帖子ById(postId);
        if(PostStatu.上线 == postEntity.getStatu()){
            postEntity.setStatu(PostStatu.下线);
        }
        else {
            postEntity.setStatu(PostStatu.上线);
        }
        keyNameValue = new KeyNameValue(postEntity.getStatu().getStatu(),postEntity.getStatu().getStatuStr());
        daoService.updateEntity(postEntity);
        return keyNameValue;
    }
    public void 上线帖子(String pkId, String postId) throws AppException, IOException {

        PostEntity postEntity = this.查询帖子ById(postId);
        if(postEntity.getStatu() == PostStatu.上线){return;}
        postEntity.setStatu(PostStatu.上线);
        postEntity.setApproveStatu(ApproveStatu.已处理);
        daoService.updateEntity(postEntity);
        this.修改图片状态上线(postEntity);
        if(!pkService.isPkCreator(pkId,postEntity.getUserId())){
            userService.用户已打榜(postEntity.getUserId());
        }
        else
        {
            //激活该主题
            EntityCacheService.lockPkEntity(pkId);
            PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
            pkEntity.setActive(true);
            daoService.updateEntity(pkEntity);
            userService.用户激活PK加一(postEntity.getUserId());

            EntityCacheService.unlockPkEntity(pkId);
            //用户可激活次数减一
            if(appService.是否收费(pkEntity.getUserId())) {
                UserKvEntity userKvEntity = userService.queryUserKvEntity(pkEntity.getUserId());
                if(userKvEntity.getFeeTimes() > 0)
                {
                    userKvEntity.setFeeTimes(userKvEntity.getFeeTimes() - 1);
                    daoService.updateEntity(userKvEntity);
                }

            }

        }







    }

    private void 修改图片状态上线(PostEntity postEntity) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,postEntity.getPkId())
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postEntity.getPostId());
        List<PostImageEntity> posts = daoService.queryEntities(PostImageEntity.class,filter);
        posts.forEach(img->{
            img.setStatu(PostStatu.上线);
            daoService.updateEntity(img);
        });



    }


    private boolean isUserCollectPost(String postId,String userId){
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(UserCollectionEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserCollectionEntity userCollectionEntity = daoService.querySingleEntity(UserCollectionEntity.class,cfilter);
        return !ObjectUtils.isEmpty(userCollectionEntity);
    }


    public PostEntity 查询帖子ById(String postId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter1);

        return postEntity;
    }

    public void 替换指定图片(String pkId, String postId, String imgUrl, String imgId, String userId,Date date) throws AppException {





        PostEntity postEntity = 查询帖子ById(postId);
        if(!org.apache.commons.lang.StringUtils.equals(postEntity.getUserId(),userId)){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }
        if(postEntity.getStatu() == PostStatu.上线){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }

        ComplainEntity complainEntity = complainService.查询投诉信息(pkId,postEntity.getUserId());
        if(!ObjectUtils.isEmpty(complainEntity))
        {
            throw AppException.buildException(PageAction.信息反馈框("投诉信息处理中","不能修改图册内容..."));
        }




        替换图片(pkId,postId,imgId,imgUrl);

        postEntity.setStatu(PostStatu.审核中);
        postEntity.setApproveStatu(ApproveStatu.未处理);
        postEntity.setRejectTimes(0);
        postEntity.setRejectTextBytes(null);
        ApproveCommentEntity approveCommentEntity = approveService.查询留言(pkId,postId);
        if(!ObjectUtils.isEmpty(approveCommentEntity)) {
            approveCommentEntity.setPostStatu(PostStatu.审核中);
            daoService.updateEntity(approveCommentEntity);

        }

        daoService.updateEntity(postEntity);

//        dynamicService.榜帖恢复到审核中状态(pkId,postId);
    }

    private void 替换图片(String pkId, String postId, String imgId, String imgUrl) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostImageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postId)
                .andFilter()
                .compareFilter("imgId",CompareTag.Equal,imgId);
        PostImageEntity postImageEntity = daoService.querySingleEntity(PostImageEntity.class,filter);
        postImageEntity.setImgUrl(imgUrl);
        daoService.updateEntity(postImageEntity);

    }

    public void 替换Topic(String pkId, String postId, String text, String userId) throws AppException, UnsupportedEncodingException {

        PostEntity postEntity = 查询帖子ById(postId);
        if(!org.apache.commons.lang.StringUtils.equals(userId,postEntity.getUserId())){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }

        postEntity.setTopic(text);

        daoService.updateEntity(postEntity);


    }

    public void 设置自评(String pkId, PostEntity postEntity, String text) {
        postEntity.setSelfComment(text);

        postEntity.setSelfCommentTime(System.currentTimeMillis());
        daoService.updateEntity(postEntity);

    }

    public Post 查询需要审核的帖子(int type) throws UnsupportedEncodingException {
        EntityFilterChain filter = null;
        if(type == 1)
        {
            filter = EntityFilterChain.newFilterChain(PostEntity.class)
                    .compareFilter("statu",CompareTag.Equal,PostStatu.审核中)
                    .andFilter()
                    .compareFilter("pkType",CompareTag.Equal,PkType.运营相册)
                    .andFilter()
                    .compareFilter("approveStatu",CompareTag.Equal,ApproveStatu.请求审核)
                    .andFilter()
                    .compareFilter("shareTime",CompareTag.Small,System.currentTimeMillis() - AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间)*60*1000)
                    .orderByRandomFilter();
        }
        else if(type == 2)
        {
            filter = EntityFilterChain.newFilterChain(PostEntity.class)
                    .compareFilter("statu",CompareTag.Equal,PostStatu.审核中)
                    .andFilter()
                    .compareFilter("pkType",CompareTag.Equal,PkType.内置相册)
                    .andFilter()
                    .compareFilter("approveStatu",CompareTag.Equal,ApproveStatu.请求审核)
                    .andFilter()
                    .compareFilter("shareTime",CompareTag.Small,System.currentTimeMillis() - AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间)*60*1000)
                    .orderByRandomFilter();
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PostEntity.class)
                    .compareFilter("statu",CompareTag.Equal,PostStatu.审核中)
                    .andFilter()
                    .compareFilter("pkType",CompareTag.Equal,PkType.审核相册)
                    .andFilter()
                    .compareFilter("approveStatu",CompareTag.Equal,ApproveStatu.请求审核)
                    .andFilter()
                    .compareFilter("shareTime",CompareTag.Small,System.currentTimeMillis() - AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间)*60*1000)
                    .orderByRandomFilter();

        }





        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter);
        if(ObjectUtils.isEmpty(postEntity)){return null;}
        Post post = this.translate(postEntity);
        post.setApproveComment(approveService.获取留言信息(postEntity.getPkId(),postEntity.getPostId()));

        return post;

    }

    public void 用户转发审批(PostEntity postEntity) throws AppException {
        if(postEntity.getRejectTimes() > AppConfigService.getConfigAsInteger(ConfigItem.Post最大修改次数))
        {
            throw AppException.buildException(PageAction.信息反馈框("修改次数过多","图册修改次数过多..."));
        }
        postEntity.setShareTime(System.currentTimeMillis());
        postEntity.setApproveStatu(ApproveStatu.请求审核);
//        postEntity.setApproveUserId(postEntity.getPkId());
        daoService.updateEntity(postEntity);
    }

    public Post 查询预置帖子(String postId) throws UnsupportedEncodingException {
        PostEntity postEntity = this.查询帖子ById(postId);
        ApproveComment approveComment = approveService.获取留言信息(postEntity.getPkId(),postEntity.getPostId());
        Post post = this.translate(postEntity);
        post.setApproveComment(approveComment);

        return post;
    }

    public void 修改Post(PostEntity postEntity) {
        daoService.updateEntity(postEntity);
    }

    public List<String> 查询PK展示图片(String pkId,String userId) {
        List<String> imgs = new ArrayList<>();
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);

        String topUserId = pkEntity.getTopPostUserId();
        if(StringUtils.isEmpty(topUserId))
        {
            topUserId = pkEntity.getUserId();
        }
        PostEntity postEntity = this.查询用户帖(pkId,topUserId);
        if(ObjectUtils.isEmpty(postEntity)){return imgs;}
        List<PostImage> postImageEntity =  getPostImages(postEntity.getPostId(),pkId);
        if(!org.apache.commons.collections4.CollectionUtils.isEmpty(postImageEntity))
        {
            postImageEntity.forEach(image->{
                imgs.add(image.getImgUrl());
            });
        }

        return imgs;

    }
}
