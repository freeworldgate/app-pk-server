package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.common.dao.PkCacheService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PkService {


    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    RedisMapService redisMapService;


    @Autowired
    AppDaoService daoService;

    @Autowired
    PkService pkService;

    @Autowired
    AppService appService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    PostCacheService postCacheService;

    @Autowired
    ApproveService approveService;

    @Autowired
    PkCacheService pkCacheService;


    public static Map<String,List<FeeTask>> taskCache = new ConcurrentHashMap<>();

    public static Map<String,List<FactualInfo>> factualCache = new ConcurrentHashMap<>();



    public List<Post> queryPrePkPost(String pkId,int page) throws IOException {

        List<Post> posts = new LinkedList<>();
//        List<String> pageList = redisSortSetService.queryPage(CacheKeyName.榜主已审核列表(pkId),page);
        List<String> pageList =  pkService.查询已审核页(pkId,page);
        for(String postId:pageList)
        {
            Post post = postService.查询帖子(pkId,postId,"");
            if(!ObjectUtils.isEmpty(post)) {
                ApproveComment approveComment = approveService.获取留言信息(pkId,postId);

                post.setApproveComment(approveComment);

                posts.add(post);
            }
        }

        Collections.shuffle(posts);


        return posts;
    }



    public List<Post> queryPkPost(String pkId,int page) throws IOException {

        List<Post> posts = 查询Post(pkId,page);

        Collections.shuffle(posts);

        return posts;
    }

    private List<Post> 查询Post( String pkId, int page) throws UnsupportedEncodingException {

        List<Post> posts = pkCacheService.查询Post缓存(pkId,page);
        if(CollectionUtils.isEmpty(posts))
        {
            posts = queryPosts(pkId,page);
            pkCacheService.添加Post缓存(pkId,page,posts);
        }
        return posts;

    }



    public List<Post> queryPosts(String pkId,int page) throws UnsupportedEncodingException {
        List<Post> posts = new LinkedList<>();
        List<String> pageList =  pkService.查询已审核页(pkId,page);
        for(String postId:pageList)
        {
            Post post = postService.查询帖子(pkId,postId,"");
            if(!ObjectUtils.isEmpty(post)) {
                posts.add(post);
            }
        }
        return posts;

    }







    public PkDetail querySinglePk(String pkId) throws IOException {
        PkEntity pk = this.querySinglePkEntity(pkId);
        return this.querySinglePk(pk);
    }


    public PkButton 查询群组(String pkId)
    {

        boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkId));

        if(hasGroup)
        {
            return appService.显示按钮(PkButtonType.群组已更新);
        }
        else
        {
            return appService.显示按钮(PkButtonType.群组未更新);
        }

    }



    public PkDetail querySinglePk(PkEntity pk) throws IOException {
        if(ObjectUtils.isEmpty(pk)){return null;}
        String pkId = pk.getPkId();
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setLocation(appService.查询PK位置(pkId));
        pkDetail.setPkTypeValue(pk.getPkType().getType());
        pkDetail.setPkType(pk.getPkType().getDesc());
        pkDetail.setTopic(pk.getTopic());
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setWatchWord(pk.getWatchWord());
        pkDetail.setTime(TimeUtils.convertTime(pk.getCreateTime()));
        pkDetail.setComplainTimes(pk.getComplainTimes());
        pkDetail.setPkStatu(ObjectUtils.isEmpty(pk.getAlbumStatu())?new KeyNameValue(PkStatu.审核中.getStatu(),PkStatu.审核中.getStatuStr()):new KeyNameValue(pk.getAlbumStatu().getStatu(),pk.getAlbumStatu().getStatuStr()));
        pkDetail.setBackUrl(StringUtils.isBlank(pk.getBackUrl())?appService.查询背景(10):pk.getBackUrl());
        pkDetail.setApproved(dynamicService.getKeyValue(CacheKeyName.已审核数量,pkId));
        pkDetail.setApproving(dynamicService.getKeyValue(CacheKeyName.审核中数量,pkId));
        pkDetail.setGroupInfo(this.查询群组(pkDetail.getPkId()));
        pkDetail.setUserBack(appService.查询背景(0));
        pkDetail.setGreate(dynamicService.getKeyValue(CacheKeyName.点赞,pkId));
        pkDetail.setInvite(dynamicService.getKeyValue(CacheKeyName.收藏,pkId));
        pkDetail.setComment(dynamicService.getKeyValue(CacheKeyName.评论,pkId));
        return pkDetail;
    }




    public User queryPkCreator(String pkId){
        PkEntity pkEntity =  querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){return null;}
        User user = userService.queryUser(pkEntity.getUserId());
        return user;
    }







    public PkEntity querySinglePkEntity(String pkId)
    {
        PkEntity pkEntity = EntityCacheService.getPkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId);
            pkEntity = daoService.querySingleEntity(PkEntity.class,filter);
            EntityCacheService.savePk(pkEntity);
        }

        return pkEntity;
    }








    public boolean isPkCreator(String pkId, String userId) throws IOException {
        PkEntity pk = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pk)){return false;}
        return org.apache.commons.lang.StringUtils.equals(userId,pk.getUserId())? Boolean.TRUE:Boolean.FALSE;
    }

    public String 创建PK(String userId, String topic, String watchWord,boolean invite) throws UnsupportedEncodingException {
        String pkId = IdGenerator.getPkId();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(System.currentTimeMillis());
        if(userService.是否是遗传用户(userId))
        {
            pkEntity.setPkType(PkType.运营相册);
        }
        else
        {
            pkEntity.setPkType(PkType.审核相册);
        }

        pkEntity.setActive(false);
        pkEntity.setTopic(topic);
        pkEntity.setWatchWord(watchWord);

        pkEntity.setUserId(userId);
        pkEntity.setTopPostUserId(userId);
        pkEntity.setAlbumStatu(PkStatu.审核中);
        pkEntity.setComplainTimes(0);
        daoService.insertEntity(pkEntity);
        userService.创建榜次数加1(userId);
        return pkId;
    }

    public PkEntity 创建预置PK(String topic, String watchWord,boolean isCharge,int type) throws UnsupportedEncodingException {
        String pkId = IdGenerator.getPkId();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(System.currentTimeMillis());
        pkEntity.setPkType(PkType.内置相册);
        pkEntity.setTopic(topic);
        pkEntity.setWatchWord(watchWord);
        String userId = appService.随机用户(type);
        pkEntity.setUserId(userId);
        pkEntity.setTopPostUserId(userId);
        pkEntity.setAlbumStatu(PkStatu.已审核);
        pkEntity.setActive(true);
        daoService.insertEntity(pkEntity);

        BuildInPkEntity buildInPkEntity = new BuildInPkEntity();
        buildInPkEntity.setPkId(pkId);
        buildInPkEntity.setIsInvite(type != 2 ?InviteType.邀请:InviteType.公开);
        buildInPkEntity.setMessageType(isCharge?MessageType.收费:MessageType.不收费);
        buildInPkEntity.setCreateTime(System.currentTimeMillis());
        daoService.insertEntity(buildInPkEntity);






        return pkEntity;
    }







    public boolean 是否更新今日审核群(PkEntity pkEntity) {
//        if(pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.公开)
//        {
//            return !StringUtils.isBlank(dynamicService.查询内置公开PK群组二维码MediaId(pkEntity.getPkId()));
//        }
        return !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkEntity.getPkId()));
    }

    public boolean 是否更新今日公告(String pkId) throws UnsupportedEncodingException {
//        return !StringUtils.isBlank(dynamicService.查询PK公告消息Id(pkId));
        return !ObjectUtils.isEmpty(approveService.获取审核人员消息Entity(pkId));

    }


    public boolean isVipView(String userId,String pkId)
    {

        if(userService.是否是遗传用户(userId)){return true;}
        if(!userService.isUserExist(userId))
        {
            return pkService.isCreatedByVip(pkId);
        }
        return false;


    }




    public AppResponse checkPk(String pkId,String userId) throws AppException, IOException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){throw AppException.buildException(PageAction.执行处理器("error","榜单不存在，是否返回主页?"));}
        PkStatu pkStatu = pkEntity.getAlbumStatu();
        if(pkStatu == PkStatu.已关闭){

            PostEntity postEntity = postService.查询用户帖(pkId,userId);
            //关闭榜单所有未审核的榜帖全变为上线
            if(!ObjectUtils.isEmpty(postEntity) && postEntity.getStatu() != PostStatu.上线)
            {
                String approver = dynamicService.查询审核用户(pkId,postEntity.getPostId());
                if(!StringUtils.isBlank(approver)){
                    postService.上线帖子(postEntity.getPkId(),postEntity.getPostId());
                    dynamicService.已审核(postEntity.getPkId(),postEntity.getPostId());
                }





            }
            return AppResponse.buildResponse(PageAction.执行处理器("error","榜单关闭，是否返回主页?"));
        }
        return null;






    }

    public void 删除PK(String userId, String pkId) throws AppException {
        EntityCacheService.lockPkEntity(pkId);
        PkEntity pkEntity = this.querySinglePkEntity(pkId);

        if(pkEntity.getAlbumStatu() == PkStatu.审核中)
        {
            daoService.deleteEntity(pkEntity);
            userService.删除一个未发布榜单(userId);
            EntityCacheService.unlockPkEntity(pkId);
            return ;
        }
        EntityCacheService.unlockPkEntity(pkId);
//        if(pkEntity.getAlbumStatu() == PkStatu.已关闭)
//        {
//            daoService.deleteEntity(pkEntity);
//            this.删除激活表(pkEntity.getPkId());
//            return ;
//        }
        throw AppException.buildException(PageAction.信息反馈框("无法删除","已发布主题无法删除"));


    }


    public void 修改PrePK(String pkId,String topic, String watchWord) throws AppException, UnsupportedEncodingException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);

            pkEntity.setTopic(topic);
            pkEntity.setWatchWord(watchWord);
            daoService.updateEntity(pkEntity);
            return ;

    }

    public void 修改PK(String pkId,String topic, String watchWord) throws AppException, UnsupportedEncodingException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(pkEntity.getAlbumStatu() == PkStatu.审核中)
        {
            pkEntity.setTopic(topic);
            pkEntity.setWatchWord(watchWord);
            daoService.updateEntity(pkEntity);
            return ;
        }

        throw AppException.buildException(PageAction.信息反馈框("修改失败","榜单当前状态不支持修改图册内容..."));


    }
    public void 修改PkCreator(String userId,String value) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCodeEntity.class)
                .compareFilter("code",CompareTag.Equal,value);
        PkCodeEntity entity = daoService.querySingleEntity(PkCodeEntity.class,filter);
        if(!ObjectUtils.isEmpty(entity))
        {

            PkEntity pkEntity = this.querySinglePkEntity( entity.getPkId());
            PostEntity postEntity = postService.查询用户帖(pkEntity.getPkId(),pkEntity.getUserId());

            if(!ObjectUtils.isEmpty(postEntity))
            {
                postEntity.setUserId(userId);
                daoService.updateEntity(postEntity);
            }
            pkEntity.setUserId(userId);
            daoService.updateEntity(pkEntity);
            daoService.deleteEntity(entity);
        }






















    }

    
    public void 更新群组时间(String pkId) {
        EntityCacheService.lockPkEntity(pkId);
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        pkEntity.setUpdateTime(System.currentTimeMillis());
        daoService.updateEntity(pkEntity);
        EntityCacheService.unlockPkEntity(pkId);
    }

    public void 修改封面(String pkId, String imgUrl) {
        EntityCacheService.lockPkEntity(pkId);
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        pkEntity.setBackUrl(imgUrl);
        daoService.updateEntity(pkEntity);
        EntityCacheService.unlockPkEntity(pkId);

    }
    public PkPostListEntity 查询图册排列(String pkId, String postId)
    {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkPostListEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId)
                .andFilter()
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkPostListEntity entity = daoService.querySingleEntity(PkPostListEntity.class,filter);
        return entity;
    }
    public void 更新图册状态列表(String pkId, String postId) {
        PkPostListEntity pkPostListEntity = 查询图册排列(pkId,postId);
        if(!ObjectUtils.isEmpty(pkPostListEntity))
        {
            pkPostListEntity.setStatu(PostStatu.上线);
            daoService.updateEntity(pkPostListEntity);
        }

        this.更新PK审核数量(pkId);


    }

    private void 更新PK审核数量(String pkId) {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        dynamicService.valueIncr(CacheKeyName.已审核数量,pkId);
        dynamicService.valueDecr(CacheKeyName.审核中数量,pkId);
//        pkEntity.setApproved(pkEntity.getApproved() + 1);
//        pkEntity.setApproving(pkEntity.getApproving() - 1);
        daoService.updateEntity(pkEntity);

    }

    public void 删除审核中Post(String pkId, String postId) {

        PkPostListEntity pkPostListEntity = 查询图册排列(pkId,postId);
        if(!ObjectUtils.isEmpty(pkPostListEntity))
        {
            daoService.deleteEntity(pkPostListEntity);
            this.减少一个审核中(pkId);
        }


    }

    public void 添加一个审核中(String pkId) {
//        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        dynamicService.valueIncr(CacheKeyName.审核中数量,pkId);

////        pkEntity.setApproving(pkEntity.getApproving() + 1);
//        daoService.updateEntity(pkEntity);
    }

    public void 减少一个审核中(String pkId) {
//        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        dynamicService.valueDecr(CacheKeyName.审核中数量,pkId);
//        pkEntity.setApproving(pkEntity.getApproving() - 1);
//        daoService.updateEntity(pkEntity);
    }


    public long 查询POST审核时间(String pkId, String postId) {

        PkPostListEntity pkPostListEntity = 查询图册排列(pkId,postId);

        return pkPostListEntity.getTime();
    }

    public String 获取一个审核中Post(String pkId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkPostListEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("statu",CompareTag.Equal,PostStatu.审核中)
                .orderByFilter("time",OrderTag.DESC);
        PkPostListEntity entity = daoService.querySingleEntity(PkPostListEntity.class,filter);
        return ObjectUtils.isEmpty(entity)?"":entity.getPostId();



    }

    public List<String> 查询已审核页(String pkId, int page) {

        List<String> ids = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkPostListEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("statu",CompareTag.Equal,PostStatu.上线)
                .pageLimitFilter(page+1,30)
                .orderByFilter("time",OrderTag.DESC);

        List<PkPostListEntity> entities = daoService.queryEntities(PkPostListEntity.class,filter);
        entities.forEach(t->{
            ids.add(t.getPostId());
        });
        return ids;

    }

    public boolean 是否审核中(String pkId, String postId) {
        PkPostListEntity pkPostListEntity = this.查询图册排列(pkId,postId);
        if(!ObjectUtils.isEmpty(pkPostListEntity) && pkPostListEntity.getStatu() == PostStatu.审核中 ){return true;}else{return false;}


    }

    public boolean isCreatedByVip(String pkId) {
        if(StringUtils.isBlank(pkId)){return false;}
        if(StringUtils.equalsIgnoreCase("undefined",pkId) || StringUtils.equalsIgnoreCase("null",pkId) || StringUtils.equalsIgnoreCase("Nan",pkId)) {return false;}
        PkEntity pkEntity =  querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){return false;}
        UserEntity creator = userService.queryUserEntity(pkEntity.getUserId());
        if(ObjectUtils.isEmpty(creator)){return false;}
        return org.apache.commons.lang.ObjectUtils.equals(creator.getUserType(),UserType.重点用户);




    }

    public GreatePkEntity 查询用户点赞(String pkId,String userId)
    {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(GreatePkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        GreatePkEntity entity = daoService.querySingleEntity(GreatePkEntity.class,filter);
        return entity;
    }


    public void greate(String pkId, String userId) {
        GreatePkEntity entity = this.查询用户点赞(pkId,userId);
        if(ObjectUtils.isEmpty(entity))
        {
            entity = new GreatePkEntity();
            entity.setPkId(pkId);
            entity.setUserId(userId);
            entity.setCreateTime(System.currentTimeMillis());
            daoService.insertEntity(entity);
            dynamicService.valueIncr(CacheKeyName.点赞,pkId);
        }
        else
        {
            daoService.deleteEntity(entity);
            dynamicService.valueDecr(CacheKeyName.点赞,pkId);
        }

    }

    public DislikePkEntity 查询用户踩一脚(String pkId,String userId)
    {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(DislikePkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        DislikePkEntity entity = daoService.querySingleEntity(DislikePkEntity.class,filter);
        return entity;
    }

    public void dislike(String pkId, String userId) {

        DislikePkEntity entity = this.查询用户踩一脚(pkId,userId);
        if(ObjectUtils.isEmpty(entity))
        {
            entity = new DislikePkEntity();
            entity.setPkId(pkId);
            entity.setUserId(userId);
            entity.setCreateTime(System.currentTimeMillis());
            daoService.insertEntity(entity);
            dynamicService.valueIncr(CacheKeyName.踩一脚,pkId);
        }
        else
        {
            daoService.deleteEntity(entity);
            dynamicService.valueDecr(CacheKeyName.踩一脚,pkId);
        }
    }

    public void 修改首页图册(String pkId, String postId) {
        PostEntity postEntity = postService.查询帖子ById(postId);
        EntityCacheService.lockPkEntity(pkId);
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        pkEntity.setTopPostUserId(postEntity.getUserId());
        daoService.updateEntity(pkEntity);
        EntityCacheService.unlockPkEntity(pkId);
    }
}
