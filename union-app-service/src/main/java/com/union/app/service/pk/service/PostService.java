package com.union.app.service.pk.service;

import com.mchange.v1.lang.BooleanUtils;
import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.common.id.KeyGetter;
import com.union.app.domain.pk.apply.ApproveCode;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.apply.PayOrderEntity;
import com.union.app.entity.pk.助力浏览评论分享.UserLikeEntity;
import com.union.app.entity.pk.审核.ApproveCommentEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Time;
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
    ApproveService approveService;



    public String 创建帖子(String pkId,String userId,String title,List<String> images) throws IOException, AppException
    {

        String postId = IdGenerator.getPostId();
        PostEntity postEntity = new PostEntity();

        postEntity.setPkId(pkId);
        postEntity.setPostId(postId);
        postEntity.setUserId(userId);
        postEntity.setTopic(noActiveTitle(title)?"...".getBytes("UTF-8"):title.getBytes(Charset.forName("UTF-8")));
        postEntity.setImgNum(images.size());
        postEntity.setCreateTime(TimeUtils.currentTime());
        postEntity.setLastModifyTime(TimeUtils.currentTime());
        postEntity.setStatu(PostStatu.审核中);
        postEntity.setRejectTimes(0);

        postEntity.setApproveStatu(ApproveStatu.未处理);
        StringBuffer stringBuffer = new StringBuffer();
        for(String img:images){
            stringBuffer.append(getLegalImgUrl(img));
            stringBuffer.append(";");

        }
        postEntity.setImgUrls(stringBuffer.toString());
        daoService.insertEntity(postEntity);
//        if(!userService.canUserView(userId)) {
//            dynamicService.设置帖子的审核用户(pkId, postId);
//        }
        return postId;
    }

    private boolean noActiveTitle(String title) {
        if(org.apache.commons.lang.StringUtils.isBlank(title)){return true;}

        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(title,"undefined")){return true;}
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(title,"Nan")){return true;}
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(title,"null")){return true;}

        return false;


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

        PostEntity postEntity = this.查询帖子ById(pkId,postId);
        if(ObjectUtils.isEmpty(postEntity)){return null;}
        Post post = translate(postEntity);

        return post;
    }


    public Post translate(PostEntity postEntity) throws UnsupportedEncodingException {
        Post post = new Post();
        post.setPkId(postEntity.getPkId());
        post.setPostId(postEntity.getPostId());
        post.setRejectTimes(postEntity.getRejectTimes());
        post.setMaxRejectTimes(AppConfigService.getConfigAsInteger(ConfigItem.Post最大修改次数));
        post.setRejectText(postEntity.getRejectTextBytes() == null?"":new String(postEntity.getRejectTextBytes(),"UTF-8"));
        post.setTime(TimeUtils.convertTime(postEntity.getCreateTime()));
        post.setCreator(userService.queryUser(postEntity.getUserId()));
        post.setTopic(new String(postEntity.getTopic(),"UTF-8"));
        post.setDynamic(getPostDynamic(postEntity.getPostId(),postEntity.getPkId()));
        post.setPostImages(getPostImages(postEntity.getImgUrls()));
        post.setStatu(new KeyNameValue(postEntity.getStatu().getStatu(),postEntity.getStatu().getStatuStr()));
//        post.setUserIntegral(dynamicService.查询用户打榜信息(postEntity.getPkId(),postEntity.getUserId()));
        post.setSelfComment(ArrayUtils.isEmpty(postEntity.getSelfComment())? org.apache.commons.lang.StringUtils.EMPTY :new String(postEntity.getSelfComment(),"UTF-8"));
        post.setSelfCommentTime(TimeUtils.convertTime(postEntity.getSelfCommentTime()));
        return post;
    }




    public List<PostImage> getPostImages(String urls) {
        List<PostImage> postImages = new ArrayList<>();
        if(StringUtils.isEmpty(urls)){return postImages;}
        String[] imgUrls = urls.split(";");

        for(String url:imgUrls){
            PostImage postImage = new PostImage();
            postImage.setImgUrl(url);
            postImage.setImageId("");
            postImage.setTime("");
            postImages.add(postImage);
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


    public Post 查询用户帖子(String pkId, String userId,Date date) throws UnsupportedEncodingException {

            PostEntity userPostEntity = 查询用户帖(pkId,userId);
            if(ObjectUtils.isEmpty(userPostEntity)){
                return null;
            }
            else {

                return this.translate(userPostEntity);
            }
    }




    public boolean 帖子是否存在(String postId) {
        String detail = KeyGetter.Post的Key(PostPart.POST信息,postId);
        if(!ossStorage.isKeyExist(detail)){return false;}
        return true;
    }


    public void 删除帖子指定图片(String postId, String imgId, String userId) throws AppException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter);
        if(!org.apache.commons.lang.StringUtils.equals(postEntity.getUserId(),userId)){
            throw AppException.buildException(PageAction.信息反馈框("操作异常","您无权删除用户图片"));
        }

        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostImageEntity.class)
                .compareFilter("imgId",CompareTag.Equal,imgId)
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postId);
        PostImageEntity postImageEntity = daoService.querySingleEntity(PostImageEntity.class,filter1);
        if(ObjectUtils.isEmpty(postImageEntity)){return;}
        postEntity.setImgNum(postEntity.getImgNum() - 1);

        daoService.deleteEntity(postImageEntity);
        daoService.updateEntity(postEntity);

    }

    public void 续传帖子(String postId, String title, String userId, List<String> images) throws AppException, UnsupportedEncodingException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter);
        if(!org.apache.commons.lang.StringUtils.equals(postEntity.getUserId(),userId)){
            throw AppException.buildException(PageAction.信息反馈框("操作异常","您无权续传用户图片"));
        }


        postEntity.setLastModifyTime(TimeUtils.currentTime());
//        String oldTitle = new String(postEntity.getTopic(),Charset.forName("UTF-8"));

        postEntity.setTopic(noActiveTitle(title) ? postEntity.getTopic() : title.getBytes("UTF-8"));

        for(String img:images){
            PostImageEntity postImageEntity = new PostImageEntity();
            postImageEntity.setPkId(postEntity.getPkId());
            postImageEntity.setPostId(postId);
            postImageEntity.setImgUrl(getLegalImgUrl(img));
            postImageEntity.setImgId(IdGenerator.getImageId());
            postImageEntity.setCreateTime(TimeUtils.currentTime());
            daoService.insertEntity(postImageEntity);
        }
        postEntity.setImgNum(postEntity.getImgNum() + images.size());
        daoService.updateEntity(postEntity);



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
            post.setTopic(new String(postEntity.getTopic(),"UTF-8"));
            post.setDynamic(getPostDynamic(postEntity.getPostId(),postEntity.getPkId()));
            post.setPostImages(getPostImages(new String(postEntity.getImgUrls())));
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

        PostEntity postEntity = this.查询帖子ById(pkId,postId);
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
    public void 上线帖子(String pkId, String postId) throws AppException {

        PostEntity postEntity = this.查询帖子ById(pkId,postId);
        if(postEntity.getStatu() == PostStatu.上线){throw AppException.buildException(PageAction.信息反馈框("榜帖已审核","榜帖已上线,不能重复审核"));}
        postEntity.setStatu(PostStatu.上线);
        daoService.updateEntity(postEntity);
        ApproveCommentEntity approveCommentEntity = approveService.查询留言(pkId,postId);
        if(!ObjectUtils.isEmpty(approveCommentEntity))
        {
            approveCommentEntity.setPostStatu(PostStatu.上线);
            daoService.updateEntity(approveCommentEntity);
        }
        if(!pkService.isPkCreator(pkId,postEntity.getUserId())){
            userService.用户已打榜(postEntity.getUserId());
        }




    }


    private boolean isUserCollectPost(String postId,String userId){
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(UserCollectionEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserCollectionEntity userCollectionEntity = daoService.querySingleEntity(UserCollectionEntity.class,cfilter);
        return !ObjectUtils.isEmpty(userCollectionEntity);
    }


    public PostEntity 查询帖子ById(String pkId, String postId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postId);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter1);
        return postEntity;
    }

    public void 替换指定图片(String pkId, String postId, String imgUrl, int index, String userId,Date date) throws AppException {

        PostEntity postEntity = 查询帖子ById(pkId,postId);
        if(!org.apache.commons.lang.StringUtils.equals(postEntity.getUserId(),userId)){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }
        String[] imgs = postEntity.getImgUrls().split(";");
        imgs[index] = imgUrl;
        StringBuffer stringBuffer = new StringBuffer();
        for(String img:imgs)
        {
            stringBuffer.append(img);
            stringBuffer.append(";");
        }
        postEntity.setImgUrls(stringBuffer.toString());
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

        dynamicService.榜帖恢复到审核中状态(pkId,postId);
    }

    public void 替换Topic(String pkId, String postId, String text, String userId) throws AppException, UnsupportedEncodingException {

        PostEntity postEntity = 查询帖子ById(pkId,postId);
        if(!org.apache.commons.lang.StringUtils.equals(userId,postEntity.getUserId())){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }

        postEntity.setTopic(text.getBytes("UTF-8"));

        daoService.updateEntity(postEntity);


    }

    public void 设置自评(String pkId, PostEntity postEntity, String text) {
        postEntity.setSelfComment(text.getBytes(Charset.forName("UTF-8")));

        postEntity.setSelfCommentTime(System.currentTimeMillis());
        daoService.updateEntity(postEntity);

    }

    public Post 查询需要审核的帖子() throws UnsupportedEncodingException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("statu",CompareTag.Equal,PostStatu.审核中)
                .andFilter()
                .compareFilter("approveStatu",CompareTag.Equal,ApproveStatu.请求审核)
                .andFilter()
                .compareFilter("shareTime",CompareTag.Small,System.currentTimeMillis() - AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间)*60*1000)
                .orderByRandomFilter();
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,filter);
        if(ObjectUtils.isEmpty(postEntity)){return null;}
        Post post = this.translate(postEntity);
        post.setApproveComment(approveService.获取留言信息(postEntity.getPkId(),postEntity.getPostId()));

        return post;

    }

    public void 用户转发审批(PostEntity postEntity) throws AppException {
        if(postEntity.getRejectTimes() > AppConfigService.getConfigAsInteger(ConfigItem.Post最大修改次数))
        {
            throw AppException.buildException(PageAction.信息反馈框("修改次数过多","榜帖修改次数过多..."));
        }
        postEntity.setShareTime(System.currentTimeMillis());
        postEntity.setApproveStatu(ApproveStatu.请求审核);
//        postEntity.setApproveUserId(postEntity.getPkId());
        daoService.updateEntity(postEntity);
    }
}
