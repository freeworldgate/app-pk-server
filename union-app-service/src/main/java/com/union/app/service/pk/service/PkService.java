package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.common.dao.PkCacheService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisMapService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        List<Post> posts = new ArrayList<>();
        List<String> pageList = redisSortSetService.queryPage(CacheKeyName.榜主已审核列表(pkId),page);
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



    public List<Post> queryPkPost(String userId,String pkId,int page) throws IOException {

        List<Post> posts = new ArrayList<>();
        List<String> pageList = redisSortSetService.queryPage(CacheKeyName.榜主已审核列表(pkId),page);
        for(String postId:pageList)
        {
            Post post = postService.查询帖子(pkId,postId,"");
            if(!ObjectUtils.isEmpty(post)) {
                posts.add(post);
            }
        }

        Collections.shuffle(posts);


        return posts;
    }


    public PkDetail querySinglePk(String pkId) throws IOException {
        PkDetail pkDetail = new PkDetail();
        PkEntity pk = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pk)){return null;}
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setPkTypeValue(pk.getPkType().getType());
        pkDetail.setPkType(pk.getPkType().getDesc());
        pkDetail.setTopic(pk.getTopic());
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setWatchWord(pk.getWatchWord());
        pkDetail.setTime(TimeUtils.translateTime(pk.getCreateTime()));
        pkDetail.setComplainTimes(pk.getComplainTimes());
        pkDetail.setInvite(new KeyNameValue(pk.getIsInvite().getStatu(),pk.getIsInvite().getStatuStr()));
        pkDetail.setInviteType(pk.getIsInvite());
        if(!ObjectUtils.isEmpty(pk.getMessageType())){pkDetail.setCharge(new KeyNameValue(pk.getMessageType().getStatu(),pk.getMessageType().getStatuStr()));}
        pkDetail.setPkStatu(ObjectUtils.isEmpty(pk.getAlbumStatu())?new KeyNameValue(PkStatu.审核中.getStatu(),PkStatu.审核中.getStatuStr()):new KeyNameValue(pk.getAlbumStatu().getStatu(),pk.getAlbumStatu().getStatuStr()));
        pkDetail.setApproveMessage(approveService.查询PK公告消息(pkId));

        pkDetail.setApproved( "已发布(" + redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) +")");
        pkDetail.setApproving("发布中(" + redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + ")");

        GroupInfo groupInfo = new GroupInfo();
        boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId()));
        groupInfo.setName("群组");
        if(hasGroup)
        {
            groupInfo.setMode(1);
            groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
        }
        else
        {
            groupInfo.setMode(2);
            groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
        }
        pkDetail.setGroupInfo(groupInfo);



//        if(pk.getPkType()==PkType.内置相册 && pk.getIsInvite() == InviteType.邀请 )
//        {
////                pkDetail.setApproved(  "已发布(" + dynamicService.查看内置相册已审核榜帖(pkId) + ")");
////                pkDetail.setApproving( "发布中" + dynamicService.查看内置相册审核中榜帖(pkId) + ")");
////                GroupInfo groupInfo = new GroupInfo();
////                groupInfo.setName("群组");
////                if(dynamicService.查看内置相册群组状态(pkId))
////                {
////                    groupInfo.setMode(1);
////                    groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
////                }
////                else
////                {
////                    groupInfo.setMode(2);
////                    groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
////                }
////                pkDetail.setGroupInfo(groupInfo);
//
//        }
//        else if(pk.getPkType()==PkType.内置相册 && pk.getIsInvite() == InviteType.公开 )
//        {
////            pkDetail.setApproved( "已发布(" + redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + ")");
////            pkDetail.setApproving("发布中(" + redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + ")");
////
////            GroupInfo groupInfo = new GroupInfo();
////            boolean hasGroup = !StringUtils.isBlank(dynamicService.查询内置公开PK群组二维码MediaId(pkDetail.getPkId() ));
////            groupInfo.setName("群组");
////            if(hasGroup)
////            {
////                groupInfo.setMode(1);
////                groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
////            }
////            else
////            {
////                groupInfo.setMode(2);
////                groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
////            }
////            pkDetail.setGroupInfo(groupInfo);
//
//        }
//        else
//        {
////            pkDetail.setApproved( "已发布(" + redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) +")");
////            pkDetail.setApproving("发布中(" + redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + ")");
////
////                GroupInfo groupInfo = new GroupInfo();
////                boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId()));
////                groupInfo.setName("群组");
////                if(hasGroup)
////                {
////                    groupInfo.setMode(1);
////                    groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
////                }
////                else
////                {
////                    groupInfo.setMode(2);
////                    groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
////                }
////                pkDetail.setGroupInfo(groupInfo);
//
//        }
        pkDetail.setUserBack(appService.查询背景(0));
        return pkDetail;
    }


    public PkDetail querySinglePk(PkEntity pk) throws IOException {
        String pkId = pk.getPkId();
        PkDetail pkDetail = new PkDetail();
        if(ObjectUtils.isEmpty(pk)){return null;}
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setPkTypeValue(pk.getPkType().getType());
        pkDetail.setPkType(pk.getPkType().getDesc());
        pkDetail.setTopic(pk.getTopic());
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setWatchWord(pk.getWatchWord());
        pkDetail.setTime(TimeUtils.translateTime(pk.getCreateTime()));
        pkDetail.setInvite(new KeyNameValue(pk.getIsInvite().getStatu(),pk.getIsInvite().getStatuStr()));
        pkDetail.setInviteType(pk.getIsInvite());
        if(!ObjectUtils.isEmpty(pk.getMessageType())){pkDetail.setCharge(new KeyNameValue(pk.getMessageType().getStatu(),pk.getMessageType().getStatuStr()));}
        pkDetail.setPkStatu(ObjectUtils.isEmpty(pk.getAlbumStatu())?new KeyNameValue(PkStatu.审核中.getStatu(),PkStatu.审核中.getStatuStr()):new KeyNameValue(pk.getAlbumStatu().getStatu(),pk.getAlbumStatu().getStatuStr()));
        pkDetail.setApproveMessage(approveService.查询PK公告消息(pkId));

        pkDetail.setApproved( "已发布(" + redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + ")");
        pkDetail.setApproving("发布中(" + redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + ")");

        GroupInfo groupInfo = new GroupInfo();
        boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId()));
        groupInfo.setName("群组");
        if(hasGroup)
        {
            groupInfo.setMode(1);
            groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
        }
        else
        {
            groupInfo.setMode(2);
            groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
        }
        pkDetail.setGroupInfo(groupInfo);



//        if(pk.getPkType()==PkType.内置相册 && pk.getIsInvite() == InviteType.邀请 )
//        {
//            pkDetail.setApproved( "已发布(" + dynamicService.查看内置相册已审核榜帖(pkId) + ")");
//            pkDetail.setApproving("发布中(" + dynamicService.查看内置相册审核中榜帖(pkId)+ ")");
//            GroupInfo groupInfo = new GroupInfo();
//            groupInfo.setName("群组");
//            if(dynamicService.查看内置相册群组状态(pkId))
//            {
//                groupInfo.setMode(1);
//                groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
//            }
//            else
//            {
//                groupInfo.setMode(2);
//                groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
//            }
//            pkDetail.setGroupInfo(groupInfo);
//
//        }
//        else if(pk.getPkType()==PkType.内置相册 && pk.getIsInvite() == InviteType.公开 )
//        {
//
//            pkDetail.setApproved( "已发布(" + redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + ")");
//            pkDetail.setApproving("发布中(" + redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + ")");
//
//            GroupInfo groupInfo = new GroupInfo();
//            boolean hasGroup = !StringUtils.isBlank(dynamicService.查询内置公开PK群组二维码MediaId(pkDetail.getPkId() ));
//            groupInfo.setName("群组");
//            if(hasGroup)
//            {
//                groupInfo.setMode(1);
//                groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
//            }
//            else
//            {
//                groupInfo.setMode(2);
//                groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
//            }
//            pkDetail.setGroupInfo(groupInfo);
//
//        }
//        else
//        {
////            pkDetail.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
////            pkDetail.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + "审核中");
//
//            pkDetail.setApproved( "已发布(" + redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + ")");
//            pkDetail.setApproving("发布中(" + redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + ")");
//
//            GroupInfo groupInfo = new GroupInfo();
//            boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId()));
//            groupInfo.setName("群组");
//            if(hasGroup)
//            {
//                groupInfo.setMode(1);
//                groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
//            }
//            else
//            {
//                groupInfo.setMode(2);
//                groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
//            }
//            pkDetail.setGroupInfo(groupInfo);
//
//        }
        pkDetail.setUserBack(appService.查询背景(0));
        return pkDetail;
    }



    public User queryPkCreator(String pkId){
        PkEntity pkEntity =  querySinglePkEntity(pkId);

        User user = userService.queryUser(pkEntity.getUserId());
        return user;
    }







    public PkEntity querySinglePkEntity(String pkId)
    {
        PkEntity  pkEntity = pkCacheService.get(pkId,PkEntity.class);
        if(ObjectUtils.isEmpty(pkEntity))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId);
            pkEntity= daoService.querySingleEntity(PkEntity.class,filter);
        }
        return pkEntity;
    }








    public boolean isPkCreator(String pkId, String userId) {
        User creator = this.queryPkCreator(pkId);
        return org.apache.commons.lang.StringUtils.equals(userId,creator.getUserId())? Boolean.TRUE:Boolean.FALSE;
    }

    public String 创建PK(String userId, String topic, String watchWord,boolean invite) throws UnsupportedEncodingException {
        String pkId = UUID.randomUUID().toString();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(TimeUtils.currentDateTime());
        pkEntity.setPkType(PkType.运营相册);
        pkEntity.setMessageType(MessageType.不收费);
        pkEntity.setTopic(topic);
        pkEntity.setWatchWord(watchWord);
        pkEntity.setIsInvite(invite?InviteType.邀请:InviteType.公开);
        pkEntity.setUserId(userId);
        pkEntity.setAlbumStatu(PkStatu.审核中);
        pkEntity.setComplainTimes(0);
        daoService.insertEntity(pkEntity);
        userService.创建榜次数加1(userId);
        return pkId;
    }

    public PkEntity 创建预置PK(String topic, String watchWord,boolean isCharge,int type) throws UnsupportedEncodingException {
        String pkId = UUID.randomUUID().toString();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(TimeUtils.currentDateTime());
        pkEntity.setPkType(PkType.内置相册);
        pkEntity.setTopic(topic);
        pkEntity.setWatchWord(watchWord);
        pkEntity.setIsInvite(InviteType.公开);
        pkEntity.setMessageType(isCharge?MessageType.收费:MessageType.不收费);
        pkEntity.setUserId(appService.随机用户());
        pkEntity.setAlbumStatu(PkStatu.已审核);
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



    public void checkPk(String pkId) throws AppException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){throw AppException.buildException(PageAction.执行处理器("error","榜单不存在，是否返回主页?"));}
        PkStatu pkStatu = pkEntity.getAlbumStatu();
        if(pkStatu == PkStatu.已关闭){
            throw AppException.buildException(PageAction.执行处理器("error","榜单关闭，是否返回主页?"));
        }






    }

    public void 删除PK(String userId, String pkId) throws AppException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        //如果去掉此步骤，则可能要删除两次PK才能成功，因为JPA的原因
        pkCacheService.remove(pkEntity);
        pkEntity = this.querySinglePkEntity(pkId);

        if(pkEntity.getAlbumStatu() == PkStatu.审核中)
        {
            daoService.deleteEntity(pkEntity);
            this.删除激活表(pkEntity.getPkId());
            userService.删除一个未激活榜单(userId);
            return ;
        }
        if(pkEntity.getAlbumStatu() == PkStatu.已关闭)
        {
            daoService.deleteEntity(pkEntity);
            this.删除激活表(pkEntity.getPkId());
            return ;
        }
        throw AppException.buildException(PageAction.信息反馈框("无法删除","已流通主题无法删除"));


    }

    private void 删除激活表(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity pkActiveEntity = daoService.querySingleEntity(PkActiveEntity.class,filter);
        if(!ObjectUtils.isEmpty(pkActiveEntity)){daoService.deleteEntity(pkActiveEntity);}


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

        throw AppException.buildException(PageAction.信息反馈框("修改失败","榜单当前状态不支持修改榜帖内容..."));


    }

    public void 修改Creator(String pkId, String userId) {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.公开)
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCreatorEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId);
            PkCreatorEntity pkCreatorEntity = daoService.querySingleEntity(PkCreatorEntity.class,filter);
            if(ObjectUtils.isEmpty(pkCreatorEntity))
            {
                PkCreatorEntity pkCreatorEntity1 = new PkCreatorEntity();
                pkCreatorEntity1.setPkId(pkId);
                pkCreatorEntity1.setUserId(userId);
                pkCreatorEntity1.setSwitchBit(true);
                daoService.insertEntity(pkCreatorEntity1);
            }
            else
            {
                if(pkCreatorEntity.isSwitchBit())
                {
                    pkCreatorEntity.setUserId(userId);
                    daoService.updateEntity(pkCreatorEntity);
                }

            }


        }

    }

    public void 修改PKUser(String pkId, String userId) {

        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        pkEntity.setUserId(userId);
        daoService.updateEntity(pkEntity);



    }
}
