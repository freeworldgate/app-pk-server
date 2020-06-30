package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.GroupInfo;
import com.union.app.domain.pk.IconUrl;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.AppMessage;
import com.union.app.domain.pk.审核.PkComment;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.task.PkTaskEntity;
import com.union.app.entity.pk.task.TaskStatu;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AppService {





    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    AppService appService;

    @Autowired
    PkService pkService;

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
    RedisSortSetService redisSortSetService;

    public List<PkDetail> 查询预设相册(int page) throws IOException {
        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity> pkEntities = queryPrePks(page);
        for(PkEntity pkPreEntity:pkEntities)
        {
            PkDetail pkDetail = this.translate(pkPreEntity);
            pkDetail.setApproveMessage(approveService.查询PK公告消息(pkPreEntity.getPkId()));
            pkDetails.add(pkDetail);
        }
        return pkDetails;
    }


    public List<PkDetail> 查询平台相册(int page) throws IOException {
        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity> pkEntities = queryPlateFormPks(page);
        for(PkEntity pkEntity:pkEntities)
        {
            PkDetail pkDetail = this.translate(pkEntity);
            pkDetail.setApproveMessage(approveService.查询PK公告消息(pkEntity.getPkId()));
            pkDetails.add(pkDetail);
        }



        return pkDetails;
    }

    private List<PkEntity> queryPrePks(int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkType",CompareTag.Equal,PkType.预设相册)
                .pageLimitFilter(page,20);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);
        return pkEntities;
    }


    public List<PkEntity> queryPlateFormPks(int page){

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkType",CompareTag.Equal,PkType.平台相册)
                .pageLimitFilter(page,20);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);
        return pkEntities;
    }

    private PkDetail translate(PkEntity pkEntity) throws UnsupportedEncodingException {
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pkEntity.getPkId());
        pkDetail.setPkType(pkEntity.getPkType());
        pkDetail.setTopic(new String(pkEntity.getTopic(),"UTF-8"));
        pkDetail.setUser(userService.queryUser(pkEntity.getUserId()));
        pkDetail.setWatchWord(new String(pkEntity.getWatchWord(),"UTF-8"));
        pkDetail.setTotalApprover(new KeyNameValue(1,dynamicService.查询今日审核员数量(pkEntity.getPkId())));
        pkDetail.setTotalSort(new KeyNameValue(2,dynamicService.查询今日打榜用户数量(pkEntity.getPkId())));
        pkDetail.setTime(TimeUtils.translateTime(pkEntity.getCreateTime()));
        pkDetail.setInvite(pkEntity.isInvite()?"仅邀请用户":"公开");



        return pkDetail;
    }

    public List<PkDetail> 查询用户邀请(String userId, int page) throws UnsupportedEncodingException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryUserInvitePks(userId,page);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = this.translate(pkEntity);

            pkDetails.add(pkDetail);
        }
        return pkDetails;

    }
    public void vip包装(List<PkDetail> pks,String userId, String fromUser) throws IOException {
        Date current = new Date();
        if(userService.isUserVip(userId) || (!userService.isUserExist(userId) && userService.isUserVip(fromUser)))
        {
            for(PkDetail pkDetail:pks)
            {

                pkDetail.setApproveMessage(approveService.查询PK公告消息(pkDetail.getPkId()));
                pkDetail.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
                pkDetail.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + "审核中");
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setMode(1);
                boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId(),current ));
                groupInfo.setName("审核群组");
                if(hasGroup)
                {
                    groupInfo.setHasGroup(true);
                    groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
                }
                else
                {
                    groupInfo.setHasGroup(true);
                    groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
                }

                pkDetail.setGroupInfo(groupInfo);


            }
        }
        else
        {
            for(PkDetail pkDetail:pks)
            {

                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setMode(0);
                groupInfo.setName(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");

                pkDetail.setGroupInfo(groupInfo);

            }




        }


    }
    public void vip包装(PkDetail pkDetail,String userId, String fromUser) throws IOException {
        Date current = new Date();
        if(userService.isUserVip(userId) || (!userService.isUserExist(userId) && userService.isUserVip(fromUser)))
        {

                pkDetail.setApproveMessage(approveService.查询PK公告消息(pkDetail.getPkId()));
                pkDetail.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
                pkDetail.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + "审核中");
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setMode(1);
                boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId(),current ));
                groupInfo.setName("审核群组");
                if(hasGroup)
                {
                    groupInfo.setHasGroup(true);
                    groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
                }
                else
                {
                    groupInfo.setHasGroup(true);
                    groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
                }
                pkDetail.setGroupInfo(groupInfo);
        }
        else
        {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setMode(0);
                groupInfo.setName(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
                pkDetail.setGroupInfo(groupInfo);

        }


    }

    private List<PkEntity> queryUserInvitePks(String userId, int page) {
        List<PkEntity> pkEntities = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(InvitePkEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,20);
        List<InvitePkEntity> invites = daoService.queryEntities(InvitePkEntity.class,filter);

        for(InvitePkEntity invitePkEntity:invites)
        {
            pkEntities.add(pkService.querySinglePkEntity(invitePkEntity.getPkId()));
        }
        return pkEntities;
    }


    public void 添加邀请(String pkId, String userId) {

        InvitePkEntity invitePkEntity = queryInvitePk(pkId,userId);
        if(ObjectUtils.isEmpty(invitePkEntity))
        {
            invitePkEntity = new InvitePkEntity();
            invitePkEntity.setPkId(pkId);
            invitePkEntity.setUserId(userId);
            invitePkEntity.setTime(TimeUtils.currentTime());
            daoService.insertEntity(invitePkEntity);
        }

    }
    public InvitePkEntity queryInvitePk(String pkId,String userId){

        EntityFilterChain filter = EntityFilterChain.newFilterChain(InvitePkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);

        InvitePkEntity invitePkEntity = daoService.querySingleEntity(InvitePkEntity.class,filter);
        return invitePkEntity;
    }

    public List<PkDetail> 查询用户相册(String userId,int page) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  pkEntities = queryUserPks(userId,page);
        for(PkEntity pkEntity:pkEntities)
        {
            PkDetail pkDetail = this.translate(pkEntity);
            pkDetail.setApproveMessage(approveService.查询PK公告消息(pkEntity.getPkId()));
            pkDetails.add(pkDetail);
        }

        return pkDetails;

    }

    private List<PkEntity> queryUserPks(String userId, int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,20);;
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);

        return pkEntities;
    }





    public boolean canView(String pkId,String userId,String fromUserId)
    {
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        PkType pkType = pkEntity.getPkType();




        return false;


    }


    public PkComment 查询激活消息留言(String pkId) {





        return null;
    }


    public AppMessage 查询激活消息(String pkId,String userId) {






        return null;
    }


    private PkCashierEntity queryPkCashierEntity() {

//        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
//                .compareFilter("userId",CompareTag.Equal,userId)
//                .pageLimitFilter(page,20);;
//        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);
//
//        return pkEntities;

        return null;
    }




}
