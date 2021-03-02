package com.union.app.service.pk.service;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.PostImage;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.Restore;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.post.*;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.pk.IdGen.IdService;
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
import java.util.*;

@Service
public class PostService {


    @Autowired
    AppDaoService daoService;


    @Autowired
    UserService userService;

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
    IdService idService;

    @Autowired
    TextService textService;
    public String 卡片打卡(String pkId, String userId, String title, String backId) {
        PostEntity postEntity = 生成打卡消息(pkId,userId,title);

        postEntity.setPostType(PostType.卡片);

        TextBack textBack = textService.查询TextBackEntity(backId);
        postEntity.setBackColor(textBack.getBackColor());
        postEntity.setFontColor(textBack.getFontColor());
        postEntity.setBackUrl(textBack.getBackUrl());

        创建打卡消息(postEntity,null);
        return postEntity.getPostId();
    }
    public String 文字打卡(String pkId, String userId, String title) throws AppException {
        if(org.apache.commons.lang.StringUtils.isBlank(title)){throw AppException.buildException(PageAction.消息级别提示框(Level.正常消息,"内容为空"));}
        PostEntity postEntity = 生成打卡消息(pkId,userId,title);
        postEntity.setPostType(PostType.文字);
        创建打卡消息(postEntity,null);
        return postEntity.getPostId();
    }

    public String 视频打卡(String pkId, String userId, String title, String videoUrl,int width,int height) {

        PostEntity postEntity = 生成打卡消息(pkId,userId,title);
        postEntity.setPostType(PostType.视频);
        postEntity.setImgNum(1);
        //视频算一张图片
        keyService.pk图片总量递增(pkId,1);
        postEntity.setWidth(width);
        postEntity.setHeight(height);
        postEntity.setVideoUrl(videoUrl);
        创建打卡消息(postEntity,null);
        return postEntity.getPostId();
    }





    public String 图片打卡(String pkId,String userId,String title,List<String> images) throws IOException, AppException
    {
        PostEntity postEntity = 生成打卡消息(pkId,userId,title);
        postEntity.setPostType(PostType.图片);
        if(CollectionUtils.isEmpty(images)){throw AppException.buildException(PageAction.信息反馈框("请选择图片","请选择图片!"));}
        List<PostImageEntity> postImageEntities = getLegalImgUrl(images,pkId,postEntity.getPostId());
        postEntity.setImgNum(CollectionUtils.isEmpty(postImageEntities)?0:images.size());
        keyService.pk图片总量递增(pkId,postEntity.getImgNum());

        daoService.insertEntity(postEntity);
        postImageEntities.forEach(image->{
            daoService.insertEntity(image);
        });

        创建打卡消息(postEntity,postImageEntities);

        return postEntity.getPostId();
    }

    public PostEntity 生成打卡消息(String pkId,String userId,String title){

        PkEntity pkEntity = locationService.querySinglePkEntityWithoutCache(pkId);
        String postId = IdGenerator.getPostId();
        PostEntity postEntity = new PostEntity();
        postEntity.setPkId(pkId);
        postEntity.setSortId(idService.gennerateSortId());
        postEntity.setPkName(pkEntity.getName());
        postEntity.setPostId(postId);
        postEntity.setUserId(userId);
        postEntity.setTopic(noActiveTitle(title)?"":processText(title));
        postEntity.setLikes(0);
        postEntity.setDislikes(0);
        postEntity.setComplains(0);
        postEntity.setComments(0);
        postEntity.setStatu(PostStatu.显示);
        postEntity.setPostTimes(pkUserDynamicService.计算打卡次数(pkId,userId)+1);
        postEntity.setTime(System.currentTimeMillis());
        return postEntity;
    }

    public void 创建打卡消息(PostEntity postEntity,List<PostImageEntity> postImageEntities){
        daoService.insertEntity(postEntity);
        pkUserDynamicService.记录用户卡点打卡时间(postEntity.getPkId(),postEntity.getUserId());
        pkUserDynamicService.卡点用户打卡次数加一(postEntity.getPkId(),postEntity.getUserId());
        userDynamicService.用户总打榜次数加一(postEntity.getUserId());
        pkDynamicService.卡点打卡人数更新(postEntity.getPkId(),postEntity.getUserId());
        if(postEntity.getPostType() == PostType.视频){
            this.记录打卡条目(postEntity.getPkId(),postEntity.getPkName(),postEntity.getPostId(),postEntity.getPostType(),postEntity.getTopic(),postEntity.getVideoUrl());
        }
        else
        {
            this.记录打卡条目(postEntity.getPkId(),postEntity.getPkName(),postEntity.getPostId(),postEntity.getPostType(),postEntity.getTopic(),CollectionUtils.isEmpty(postImageEntities)?null:postImageEntities.get(0).getImgUrl());
        }

    }
























    private void 记录打卡条目(String pkId,String pkName, String postId,PostType postType, String title, String url) {
        PostColumEntity postColumEntity = new PostColumEntity();
        postColumEntity.setPostId(postId);
        postColumEntity.setPostType(postType.getType());
        postColumEntity.setPkId(pkId);
        postColumEntity.setPkName(pkName);
        postColumEntity.setText(title);
        postColumEntity.setContentUrl(url);
        daoService.insertEntity(postColumEntity);
    }

    public String processText(String str) {
//        String result = "";
//        if (str != null) {
//            Pattern p = Pattern.compile("(\r?\n(\\s*\r?\n)+)");
//            Matcher m = p.matcher(str);
//            result = m.replaceAll("\r\n");
//        }
//        return result;
        return str;
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
        if(CollectionUtils.isEmpty(images)){return postImageEntities;}
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




    public Post 查询帖子(String postId) {

        PostEntity postEntity = this.查询帖子ById(postId);
        if(ObjectUtils.isEmpty(postEntity)){return null;}
        Post post = translate(postEntity);
        return post;
    }


    public Post translate(PostEntity postEntity){
        Post post = new Post();
        post.setPkId(postEntity.getPkId());
        post.setPostId(postEntity.getPostId());
        post.setSortId(postEntity.getSortId());
        post.setType(postEntity.getPostType().getType());
        post.setVideoUrl(postEntity.getVideoUrl());
        if(postEntity.getPostType() == PostType.视频){
            post.setVideowidth(postEntity.getWidth());
            post.setVideoheight(postEntity.getHeight());
        }
        post.setPkTopic(postEntity.getPkName());
        post.setTime(TimeUtils.convertTime(postEntity.getTime()));
        post.setCreator(userService.queryUser(postEntity.getUserId()));
        post.setTopic(postEntity.getTopic());
        post.setBackColor(postEntity.getBackColor());
        post.setFontColor(postEntity.getFontColor());
        post.setBackUrl(postEntity.getBackUrl());
        post.setPostTimes(postEntity.getPostTimes());
        post.setComplains(postEntity.getComplains());
        post.setComments(postEntity.getComments());
        post.setLikes(postEntity.getLikes());
        post.setDislikes(postEntity.getDislikes());

        post.setPostImages(postEntity.getPostType()!=PostType.图片?new ArrayList<>():getPostImages(postEntity.getPostId(),postEntity.getPkId()));
        return post;
    }

    private float 计算高度(PostEntity postEntity) {
        if(postEntity.getWidth() > postEntity.getHeight())
        {


            return new Float(76.2 * postEntity.getHeight()/(postEntity.getWidth()*1f));
        }
        else
        {
            if(postEntity.getHeight()/(postEntity.getWidth()*1F) > 1.2f)
            {
                return new Float(76.2 *1.2f);

            }
            else
            {
                return new Float(76.2 * postEntity.getHeight()/(1f*postEntity.getWidth()));
            }

        }
    }

    private float 计算宽度(PostEntity postEntity) {
        if(postEntity.getWidth() > postEntity.getHeight())
        {
            return 76.2f;
        }
        else
        {
            return new Float(76.2 * postEntity.getWidth()/postEntity.getHeight());
        }
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
            map.put("topPostTimeLength",0L);
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
            if((pkDetail.getTopPostType() == PostType.图片.getType()) && org.apache.commons.lang.StringUtils.isNotBlank(pkDetail.getTopPostId()) && ((System.currentTimeMillis() - pkDetail.getTopPostSetTime()) < pkDetail.getTopPostTimeLength() * 60 * 1000L))
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

    public void 添加投诉(String postId, String userId, int type) {
        PostComplainEntity complainEntity = new PostComplainEntity();
        complainEntity.setComplainId(IdGenerator.getComplainId());
        complainEntity.setPostId(postId);
        complainEntity.setUserId(userId);
        complainEntity.setComplainType(ComplainType.valueOf(type));
        complainEntity.setTime(System.currentTimeMillis());
        daoService.insertEntity(complainEntity);
        keyService.Post投诉数量加一(postId);
    }


}
