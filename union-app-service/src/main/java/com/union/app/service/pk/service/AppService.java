package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.审核.AppMessage;
import com.union.app.domain.pk.审核.PkComment;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.task.PkTaskEntity;
import com.union.app.entity.pk.task.TaskStatu;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.support.UserPostStatu;
import com.union.app.entity.用户.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
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
import org.springframework.util.IdGenerator;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.*;
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

    @Autowired
    MediaService mediaService;


    public List<PkDetail> 查询预设相册(int page,int type) throws IOException {
        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity> pkEntities = queryPrePks(page,type);
        for(PkEntity pkPreEntity:pkEntities)
        {
            PkDetail pkDetail = this.translate(pkPreEntity);
            pkDetail.setApproveMessage(approveService.查询PK公告消息(pkPreEntity.getPkId()));
            pkDetail.setPriority(appService.查询优先级(pkPreEntity.getPkId()));
            pkDetails.add(pkDetail);
        }
        return pkDetails;
    }


    public List<PkDetail> 查询审核相册(int page) throws IOException {
        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity> pkEntities = queryPlateFormPks(page);
        for(PkEntity pkEntity:pkEntities)
        {
            PkDetail pkDetail = this.translate(pkEntity);
            pkDetails.add(pkDetail);
        }

        return pkDetails;
    }

    private List<PkEntity> queryPrePks(int page) {


        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkType",CompareTag.Equal,PkType.内置相册)
                .pageLimitFilter(page,20);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);



        return pkEntities;
    }

    private List<PkEntity> queryPrePks(int page,int type) {

        EntityFilterChain filter = null;
        if(type == 2)
        {
            filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                    .compareFilter("pkType",CompareTag.Equal,PkType.运营相册)
                    .orderByFilter("priority",OrderTag.DESC)
                    .pageLimitFilter(page,20);
        }
        else if(type == 3)
        {
            filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                    .compareFilter("pkType",CompareTag.Equal,PkType.内置相册)
                    .orderByFilter("priority",OrderTag.DESC)
                    .pageLimitFilter(page,20);
        }
        else if(type == 4)
        {
            filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                    .compareFilter("pkType",CompareTag.Equal,PkType.审核相册)
                    .orderByFilter("priority",OrderTag.DESC)
                    .pageLimitFilter(page,20);
        }
        else {
            filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                    .orderByFilter("priority",OrderTag.DESC)
                    .pageLimitFilter(page,20);
        }






        List<HomePagePk> pkEntities = daoService.queryEntities(HomePagePk.class,filter);

        List<PkEntity> pks = new ArrayList<>();
        for(HomePagePk pk:pkEntities)
        {
            pks.add(pkService.querySinglePkEntity(pk.getPkId()));
        }
        return pks;
    }


    public List<PkEntity> queryPlateFormPks(int page){

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkType",CompareTag.Equal,PkType.审核相册)
                .pageLimitFilter(page,20);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);
        return pkEntities;
    }

    private PkDetail translate(PkEntity pkEntity) throws UnsupportedEncodingException {
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pkEntity.getPkId());
        pkDetail.setPkType(pkEntity.getPkType().getDesc());
        pkDetail.setTopic(new String(pkEntity.getTopic(),"UTF-8"));
        pkDetail.setUser(userService.queryUser(pkEntity.getUserId()));
        pkDetail.setWatchWord(new String(pkEntity.getWatchWord(),"UTF-8"));
//        pkDetail.setTotalApprover(new KeyNameValue(1,dynamicService.查询今日审核员数量(pkEntity.getPkId())));
//        pkDetail.setTotalSort(new KeyNameValue(2,dynamicService.查询今日打榜用户数量(pkEntity.getPkId())));
        pkDetail.setTime(TimeUtils.translateTime(pkEntity.getCreateTime()));
        pkDetail.setInvite(new KeyNameValue(pkEntity.getIsInvite().getStatu(),pkEntity.getIsInvite().getStatuStr()));
        pkDetail.setPkStatu(new KeyNameValue(ObjectUtils.isEmpty(pkEntity.getAlbumStatu())?PkStatu.审核中.getStatu():pkEntity.getAlbumStatu().getStatu(),ObjectUtils.isEmpty(pkEntity.getAlbumStatu())?PkStatu.审核中.getStatuStr():pkEntity.getAlbumStatu().getStatuStr()));



        return pkDetail;
    }
    public List<PkDetail> 查询内置相册(String userId, int page) throws UnsupportedEncodingException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryPrePks(page);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = this.translate(pkEntity);
            pkDetail.setPriority(appService.查询优先级(pkEntity.getPkId()));
            pkDetails.add(pkDetail);
        }
        return pkDetails;

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

            for(PkDetail pkDetail:pks)
            {
                vip包装(pkDetail,userId,fromUser);

            }
    }



    public void vip包装(PkDetail pkDetail,String userId, String fromUser) throws IOException {
        if(ObjectUtils.isEmpty(pkDetail)){return;}
        Date current = new Date();

        if(userService.canUserView(userId,fromUser)) {
            pkDetail.setApproveMessage(approveService.查询PK公告消息(pkDetail.getPkId()));
        }



        if(StringUtils.equals(pkDetail.getPkType(),PkType.内置相册.getDesc()))
        {


            EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                    .compareFilter("pkId",CompareTag.Equal,pkDetail.getPkId());
            HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);

            if(!ObjectUtils.isEmpty(pk))
            {
                pkDetail.setApproved(pk.getApproved() + "已审核");
                pkDetail.setApproving(pk.getApproving() + "审核中");

                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setMode(1);
                groupInfo.setName("审核群组");
                if(pk.getGroupStatu() == 1)
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
                pkDetail.setApproved(0 + "已审核");
                pkDetail.setApproving(0 + "审核中");
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setMode(1);
                groupInfo.setName("审核群组");

                groupInfo.setHasGroup(true);
                groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
                pkDetail.setGroupInfo(groupInfo);
            }










        }
        else
        {
            pkDetail.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
            pkDetail.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + "审核中");
            if( userService.canUserView(userId,fromUser))
            {

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

                pkDetail.setGroupInfo(null);

            }

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
            PkEntity pkEntity = pkService.querySinglePkEntity(invitePkEntity.getPkId());
            if(!ObjectUtils.isEmpty(pkEntity)){pkEntities.add(pkEntity);}

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
            userService.邀请次数加一(userId);
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
//            pkDetail.setApproveMessage(approveService.查询PK公告消息(pkEntity.getPkId()));
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







//    public List<PkCashier> queryCashiers(String pkId)
//    {
//
//
//        List<PkCashier> pkCashiers = new ArrayList<>();
//
//        List<PkCashierEntity> cashiers = this.查询可用的Cashier();
//        PkCashierEntity selectCashier = this.查询已选Cashier(pkId);
//        cashiers = this.addFirst(cashiers,selectCashier);
//
//        for(PkCashierEntity pkCashierEntity:cashiers)
//        {
//            pkCashiers.add(this.translate(pkCashierEntity));
//        }
//
//        return pkCashiers;
//    }
//
//    private PkCashier translate(PkCashierEntity pkCashierEntity) {
//        PkCashier pkCashier = new PkCashier();
//        pkCashier.setCashierId(pkCashierEntity.getCashierId());
//        pkCashier.setCashierImg(pkCashierEntity.getCashierImg());
//        pkCashier.setCashierName(pkCashierEntity.getCashierName());
//        pkCashier.setImgUrl(pkCashierEntity.getImgUrl());
//        pkCashier.setText(pkCashierEntity.getText());
//        pkCashier.setConfirmTimes(pkCashierEntity.getConfirmTimes());
//        pkCashier.setSelectTimes(pkCashierEntity.getSelectTimes());
//
//        return pkCashier;
//    }

//    private List<PkCashierEntity> addFirst(List<PkCashierEntity> cashiers, PkCashierEntity selectCashier) {
//        List<PkCashierEntity> cashierEntities = new ArrayList<>();
//        if(ObjectUtils.isEmpty(selectCashier)){return cashiers;}
//        for(PkCashierEntity pkCashierEntity:cashiers)
//        {
//            if(!StringUtils.equals(pkCashierEntity.getCashierId(),selectCashier.getCashierId()))
//            {
//                cashierEntities.add(pkCashierEntity);
//            }
//        }
//        cashierEntities.add(0,selectCashier);
//        return cashierEntities;
//    }

//    public PkCashierEntity 查询已选Cashier(String pkId)
//    {
//        String cashierId = pkService.querySinglePkEntity(pkId).getSelectCashierId();
//        return this.查询Cashier(cashierId);
//
//    }
//
//    public String 已选CashierId(String pkId)
//    {
//       return pkService.querySinglePkEntity(pkId).getSelectCashierId();
//    }
//


//    private List<PkCashierEntity> 查询可用的Cashier() {
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
//                .compareFilter("statu",CompareTag.Equal,CashierStatu.启用);
//        List<PkCashierEntity> pkEntities = daoService.queryEntities(PkCashierEntity.class,filter);
//
//        return pkEntities;
//    }
//    public PkCashierEntity 查询Cashier(String cashierId) {
//        if(StringUtils.isBlank(cashierId)){return null;}
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
//                .compareFilter("cashierId",CompareTag.Equal,cashierId);
//        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
//
//        return pkCashierEntity;
//    }

//
//    public void 设置激活用户(String pkId, String cashierId) {
//        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
//        pkEntity.setSelectCashierId(cashierId);
//        daoService.updateEntity(pkEntity);
//
//
//
//    }



    public void 上传打赏截图(String pkId, String userId, String url) throws IOException, AppException {

        PkActiveEntity pkActiveEntity = 查询PK激活信息(pkId);
        if(ObjectUtils.isEmpty(pkActiveEntity))
        {

            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"未确认激活群组信息"));
        }
        else
        {
            pkActiveEntity.setScreenCutUrl(url);
            String mediaId = WeChatUtil.uploadImg2Wx(url);
            pkActiveEntity.setScreenCutMediaId(mediaId);
            pkActiveEntity.setScreenCutTime(System.currentTimeMillis());
            pkActiveEntity.setStatu(ActiveStatu.待处理);
            daoService.updateEntity(pkActiveEntity);
        }






    }
    private List<PkCashierEntity> queryPkCashiers(int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .pageLimitFilter(page,20);;
        List<PkCashierEntity> pkCashierEntities = daoService.queryEntities(PkCashierEntity.class,filter);

        return pkCashierEntities;
    }
    public List<PkCashier> 查询收款列表(int page) throws IOException {


        List<PkCashier> pkCashiers = new ArrayList<>();

        List<PkCashierEntity>  pkCashierEntities = queryPkCashiers(page);
        for(PkCashierEntity pkCashierEntity:pkCashierEntities)
        {
            PkCashier pkCashier = new PkCashier();
            pkCashier.setCashierId(pkCashierEntity.getCashierId());
            pkCashier.setCashierName(pkCashierEntity.getRealName());
            pkCashier.setSelectTimes(pkCashierEntity.getSelectTimes());
            pkCashier.setStatu(new KeyNameValue(pkCashierEntity.getStatu().getStatu(),pkCashierEntity.getStatu().getStatuStr()));
            pkCashiers.add(pkCashier);
        }

        return pkCashiers;

    }


    public synchronized void 新建收款用户(String name) {

        PkCashierEntity pkCashierEntity = new PkCashierEntity();
        pkCashierEntity.setCashierId(com.union.app.util.idGenerator.IdGenerator.收款用户ID());
        pkCashierEntity.setRealName(name);
        pkCashierEntity.setSelectTimes(0);
        pkCashierEntity.setStatu(CashierStatu.停用);
        daoService.insertEntity(pkCashierEntity);

    }
    private List<PkCashierGroupEntity> queryPkCashierGroups(String cashierId,int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .pageLimitFilter(page,20);
        List<PkCashierGroupEntity> pkCashierGroupEntities = daoService.queryEntities(PkCashierGroupEntity.class,filter);
        return pkCashierGroupEntities;
    }

    public List<CashierGroup> 查询用户群组(String cashierId, int page) {
        List<CashierGroup> pkCashiers = new ArrayList<>();
        List<PkCashierGroupEntity>  pkCashierGroupEntities = queryPkCashierGroups(cashierId,page);
        for(PkCashierGroupEntity pkCashierGroupEntity:pkCashierGroupEntities)
        {
            CashierGroup cashierGroup = new CashierGroup();
            cashierGroup.setGroupId(pkCashierGroupEntity.getGroupId());
            cashierGroup.setGroupUrl(pkCashierGroupEntity.getGroupUrl());
            cashierGroup.setTime("已上传:" + TimeUtils.距离上传的小时数(pkCashierGroupEntity.getCreateTime())  + "小时");
            cashierGroup.setMembers(dynamicService.查询群组分配的人数(pkCashierGroupEntity.getGroupId()));
            cashierGroup.setStatu(new KeyNameValue(pkCashierGroupEntity.getStatu().getStatu(),pkCashierGroupEntity.getStatu().getStatuStr()));
            pkCashiers.add(cashierGroup);
        }

        return pkCashiers;

    }

    public void 上传群组(String cashierId, String imgUrl) throws IOException {

        PkCashierGroupEntity pkCashierGroupEntity = new PkCashierGroupEntity();
        pkCashierGroupEntity.setGroupId(com.union.app.util.idGenerator.IdGenerator.收款群组ID());
        pkCashierGroupEntity.setCashierId(cashierId);
        String mediaId = WeChatUtil.uploadImg2Wx(imgUrl);
        pkCashierGroupEntity.setGroupMediaId(mediaId);
        pkCashierGroupEntity.setCreateTime(System.currentTimeMillis());
        pkCashierGroupEntity.setLastUpdateTime(System.currentTimeMillis());
        pkCashierGroupEntity.setGroupUrl(imgUrl);
        pkCashierGroupEntity.setStatu(GroupStatu.停用);
        daoService.insertEntity(pkCashierGroupEntity);


    }
    public void 更新群组(String cashierId, String groupId, String imgUrl) throws IOException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .andFilter()
                .compareFilter("groupId",CompareTag.Equal,groupId);
        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter);

        pkCashierGroupEntity.setGroupUrl(imgUrl);
        String mediaId = WeChatUtil.uploadImg2Wx(imgUrl);
        pkCashierGroupEntity.setGroupMediaId(mediaId);
        pkCashierGroupEntity.setLastUpdateTime(System.currentTimeMillis());
        pkCashierGroupEntity.setCreateTime(System.currentTimeMillis());

        daoService.updateEntity(pkCashierGroupEntity);


    }





    public void 删除群组(String cashierId, String groupId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .andFilter()
                .compareFilter("groupId",CompareTag.Equal,groupId);
        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter);
        if(!ObjectUtils.isEmpty(pkCashierGroupEntity)){
            daoService.deleteEntity(pkCashierGroupEntity);
        }





    }

    public List<CashierFeeCode> 查询用户收款码(String cashierId) {

        List<CashierFeeCode> cashierFeeCodes = new ArrayList<>();

        for(FeeNumber feeNumber:FeeNumber.values())
        {
            CashierFeeCode cashierFeeCode = new CashierFeeCode();


            cashierFeeCode.setFeeNumber(new KeyNameValue(feeNumber.getTag(),String.valueOf(feeNumber.getFee())));

            PkCashierFeeCodeEntity pkCashierFeeCodeEntity = queryPkCashierFeeCode(cashierId,feeNumber);
            if(!ObjectUtils.isEmpty(pkCashierFeeCodeEntity))
            {
                cashierFeeCode.setCashierId(pkCashierFeeCodeEntity.getCashierId());
                cashierFeeCode.setFeeCodeId(pkCashierFeeCodeEntity.getFeeCodeId());
                cashierFeeCode.setFeeCodeUrl(pkCashierFeeCodeEntity.getFeeCodeUrl());
                cashierFeeCode.setConfirmTime(pkCashierFeeCodeEntity.getConfirmTimes());
                cashierFeeCode.setSelectTime(dynamicService.查询收款码分配的人数(pkCashierFeeCodeEntity.getFeeCodeId()));
            }

            cashierFeeCodes.add(cashierFeeCode);



        }

        return cashierFeeCodes;



    }


    private PkCashierFeeCodeEntity queryPkCashierFeeCode(String cashierId,FeeNumber feeNumber) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .andFilter()
                .compareFilter("feeNumber",CompareTag.Equal,feeNumber);
        PkCashierFeeCodeEntity pkCashierFeeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter);
        return pkCashierFeeCodeEntity;

    }

    public void 上传收款码(String cashierId, int feeNumber, String imgUrl) throws IOException {

        FeeNumber feeNumber1 = FeeNumber.valueOfNumber(feeNumber);
        PkCashierFeeCodeEntity pkCashierFeeCodeEntity = new PkCashierFeeCodeEntity();
        pkCashierFeeCodeEntity.setFeeCodeId(com.union.app.util.idGenerator.IdGenerator.收款码ID());
        pkCashierFeeCodeEntity.setCashierId(cashierId);
        pkCashierFeeCodeEntity.setFeeCodeUrl(imgUrl);
        pkCashierFeeCodeEntity.setFeeNumber(feeNumber1);
        pkCashierFeeCodeEntity.setConfirmTimes(0);
        String mediaId = WeChatUtil.uploadImg2Wx(imgUrl);
        pkCashierFeeCodeEntity.setFeeCodeMediaId(mediaId);
        pkCashierFeeCodeEntity.setLastUpdateTime(System.currentTimeMillis());
        pkCashierFeeCodeEntity.setCreateTime(System.currentTimeMillis());

        daoService.insertEntity(pkCashierFeeCodeEntity);

    }







    public void 修改用户状态(String cashierId) throws AppException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);;
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
        CashierStatu cashierStatu = pkCashierEntity.getStatu();

        if(cashierStatu == CashierStatu.启用)
        {
            pkCashierEntity.setStatu(CashierStatu.停用);
            禁用用户所有群(cashierId);
        }
        else
        {
            全部收款码启用确认(cashierId);
            pkCashierEntity.setStatu(CashierStatu.启用);

        }
        daoService.updateEntity(pkCashierEntity);
    }

    private void 全部收款码启用确认(String cashierId) throws AppException {
        for(FeeNumber feeNumber:FeeNumber.values())
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                    .compareFilter("cashierId",CompareTag.Equal,cashierId)
                    .andFilter()
                    .compareFilter("feeNumber",CompareTag.Equal,feeNumber);
            PkCashierFeeCodeEntity pkCashierFeeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter);
            if(ObjectUtils.isEmpty(pkCashierFeeCodeEntity))
            {
                throw AppException.buildException(PageAction.执行处理器("init",""));
            }


        }


    }

//    private void 禁用用户所有收款码(String cashierId) {
//
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
//                .compareFilter("cashierId",CompareTag.Equal,cashierId);
//        List<PkCashierFeeCodeEntity> pkCashierFeeCodeEntities = daoService.queryEntities(PkCashierFeeCodeEntity.class,filter);
//        for(PkCashierFeeCodeEntity feeCode:pkCashierFeeCodeEntities)
//        {
//            feeCode.setStatu(FeeCodeStatu.停用);
//            daoService.updateEntity(feeCode);
//        }
//    }

    private void 禁用用户所有群(String cashierId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);
        List<PkCashierGroupEntity> pkCashierGroupEntities = daoService.queryEntities(PkCashierGroupEntity.class,filter);
        for(PkCashierGroupEntity group:pkCashierGroupEntities)
        {
            group.setStatu(GroupStatu.停用);
            daoService.updateEntity(group);
        }


    }


    public void 修改群组状态(String cashierId, String groupId) throws AppException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .andFilter()
                .compareFilter("groupId",CompareTag.Equal,groupId);
        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter);
        GroupStatu groupStatu = pkCashierGroupEntity.getStatu();

        if(groupStatu == GroupStatu.启用)
        {
            pkCashierGroupEntity.setStatu(GroupStatu.停用);
        }
        else
        {
            //判断用户状态

            EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                    .compareFilter("cashierId",CompareTag.Equal,cashierId);
            PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter1);
            if(pkCashierEntity.getStatu() == CashierStatu.启用)
            {
                pkCashierGroupEntity.setStatu(GroupStatu.启用);
            }
            else
            {
                throw AppException.buildException(PageAction.执行处理器("openCashier",""));
            }







        }
        daoService.updateEntity(pkCashierGroupEntity);


    }










    public synchronized PkCashierGroupEntity selectRandomGroup(List<String> cashiers)
    {
        List<Object> legalCashiers = new ArrayList<>();
        legalCashiers.addAll(cashiers);
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("statu",CompareTag.Equal,GroupStatu.启用)
                .andFilter()
                .inFilter("cashierId",legalCashiers)
                .andFilter()
                .compareFilter("createTime",CompareTag.Bigger,System.currentTimeMillis() - AppConfigService.getConfigAsLong(ConfigItem.群组最长上传时间间隔) * 3600 * 1000)
                .orderByRandomFilter();
        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter);
        //首先确定群组
        if(!ObjectUtils.isEmpty(pkCashierGroupEntity))
        {
            if(dynamicService.查询群组分配的人数(pkCashierGroupEntity.getGroupId()) >= AppConfigService.getConfigAsInteger(ConfigItem.单个群最大人数))
            {
                pkCashierGroupEntity.setStatu(GroupStatu.停用);
                daoService.updateEntity(pkCashierGroupEntity);
            }
        }
        return pkCashierGroupEntity;

    }


    public PkCashierFeeCodeEntity selectFeeCode(String cashierId)
    {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                .compareFilter("feeNumber",CompareTag.Equal,appService.当前系统激活收取费用())
                .andFilter()
                .compareFilter("cashierId",CompareTag.Equal,cashierId);
        PkCashierFeeCodeEntity pkCashierFeeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter1);

        return pkCashierFeeCodeEntity;
    }

    //确定用户可用群和收款码
    public PkActiveEntity selectGroupAndFeeCode(String pkId,String userId) throws AppException {

        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity pkActiveEntity = daoService.querySingleEntity(PkActiveEntity.class,filter2);
        if(!ObjectUtils.isEmpty(pkActiveEntity))
        {
            return pkActiveEntity;
        }
        //激活记录
        List<PkActiveEntity> pkActiveEntities = queryPkActiveHistory(userId);
        //获取可选的收款人
        List<String> leftCashiers = queryLeftCashiers(pkActiveEntities);
        if(CollectionUtils.isEmpty(leftCashiers)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"没找到可用激活用户,稍后再试!"));}
        PkCashierGroupEntity pkCashierGroupEntity = selectRandomGroup(leftCashiers);

        if(ObjectUtils.isEmpty(pkCashierGroupEntity))
        {
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"没找到可用激活用户,稍后再试!"));
        }

        PkCashierFeeCodeEntity pkCashierFeeCodeEntity = selectFeeCode(pkCashierGroupEntity.getCashierId());




        PkActiveEntity activeEntity = new PkActiveEntity();
        activeEntity.setPkId(pkId);
        activeEntity.setUserId(userId);
        activeEntity.setCashierId(pkCashierGroupEntity.getCashierId());
        activeEntity.setGroupId(pkCashierGroupEntity.getGroupId());
        activeEntity.setFeeCodeId(pkCashierFeeCodeEntity.getFeeCodeId());
        activeEntity.setStatu(ActiveStatu.初始化);
        daoService.insertEntity(activeEntity);
        dynamicService.群组分配的人数加一(activeEntity.getGroupId());
        dynamicService.收款码分配的人数加一(activeEntity.getFeeCodeId());

        return activeEntity;
    }

    private List<String> queryLeftCashiers(List<PkActiveEntity> pkActiveEntities) {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("statu",CompareTag.Equal,CashierStatu.启用);
        List<PkCashierEntity> pkCashierEntities = daoService.queryEntities(PkCashierEntity.class,filter2);
        Set<String> cashiers = new HashSet<>();
        for(PkCashierEntity pkCashierEntity:pkCashierEntities)
        {
            cashiers.add(pkCashierEntity.getCashierId());
        }

        for(PkActiveEntity pkActiveEntity:pkActiveEntities)
        {
            cashiers.remove(pkActiveEntity.getCashierId());
        }
        List<String> allCashiers = new ArrayList<>();
        allCashiers.addAll(cashiers);
        return allCashiers;
    }

    private List<PkActiveEntity> queryPkActiveHistory(String userId) {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        List<PkActiveEntity> pkActiveEntity = daoService.queryEntities(PkActiveEntity.class,filter2);
        return pkActiveEntity;
    }


    public PkCashierGroupEntity 查询可用群组(String pkId,String userId) throws AppException {

        PkActiveEntity activeEntity = selectGroupAndFeeCode(pkId,userId);

        //查询可用群组条件:  人数少于群最大值，启用状态 ，上传时间在6天以内(群组二维码七天过期)
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("groupId",CompareTag.Equal,activeEntity.getGroupId());

        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter);

        return pkCashierGroupEntity;

    }



    public PkCashierFeeCodeEntity 查询可用收款码(String pkId, String userId) throws AppException {
        PkActiveEntity activeEntity = selectGroupAndFeeCode(pkId,userId);
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                .compareFilter("feeCodeId",CompareTag.Equal,activeEntity.getFeeCodeId());
        PkCashierFeeCodeEntity feeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter);
        return feeCodeEntity;
    }






    private FeeNumber 当前系统激活收取费用() {
        int feeTag = AppConfigService.getConfigAsInteger(ConfigItem.当前系统收款金额);
        return FeeNumber.valueOfNumber(feeTag);
    }


    public void 修改收款码(String cashierId, String feeCodeId, String imgUrl) throws IOException {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                    .compareFilter("cashierId",CompareTag.Equal,cashierId)
                    .andFilter()
                    .compareFilter("feeCodeId",CompareTag.Equal,feeCodeId);
            PkCashierFeeCodeEntity pkCashierFeeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter);

            pkCashierFeeCodeEntity.setFeeCodeUrl(imgUrl);
            String mediaId = WeChatUtil.uploadImg2Wx(imgUrl);
            pkCashierFeeCodeEntity.setFeeCodeMediaId(mediaId);
            daoService.updateEntity(pkCashierFeeCodeEntity);
    }

    public void 设置参数(String type,String value) {
        ConfigEntity configEntity = 查询参数(type);

        configEntity.setConfigValue(value);

        daoService.updateEntity(configEntity);

    }
    public ConfigEntity 查询参数(String configTag){
        ConfigItem configItem = ConfigItem.valueOfConfigItem(configTag);
        if(ObjectUtils.isEmpty(configItem)){return null;}
        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(ConfigEntity.class).compareFilter("configName", CompareTag.Equal, configItem.getName());
        ConfigEntity configEntity = daoService.querySingleEntity(ConfigEntity.class, entityFilterChain);
        if(ObjectUtils.isEmpty(configEntity))
        {
            configEntity = new ConfigEntity();
            configEntity.setConfigName(configItem.getName());
            configEntity.setConfigDesc(configItem.getDesc());
            configEntity.setColumSwitch(ColumSwitch.ON);
            configEntity.setCreateTime(new Date());
            configEntity.setConfigValue(configItem.getDefaultValue());
            configEntity.setDefaultValue(configItem.getDefaultValue());
            daoService.insertEntity(configEntity);
        }
        return configEntity;


    }

    public List<DataSet> 查询当前设置页面() {
        List<DataSet> dataSets = new ArrayList<>();
        for(ConfigItem configItem:ConfigItem.values())
        {
            ConfigEntity configEntity = 查询参数(configItem.getTag());
            String value = configEntity.getConfigValue();
            dataSets.add(new DataSet(configItem.getTag(),value));

        }
        return dataSets;
    }

    public List<PkCashierGroupEntity> 查询过期的群组() {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("createTime",CompareTag.Small, System.currentTimeMillis() - (AppConfigService.getConfigAsInteger(ConfigItem.群组最长上传时间间隔)* 3600 * 1000))
                .andFilter()
                .compareFilter("statu",CompareTag.Equal,GroupStatu.启用)
                .pageLimitFilter(1,20);
        List<PkCashierGroupEntity> pkCashierGroupEntities = daoService.queryEntities(PkCashierGroupEntity.class,filter);
        return pkCashierGroupEntities;






    }

    public PkButton 显示按钮(String userId, String fromUser,PkButtonType pkButtonType) {
        if(pkButtonType == PkButtonType.榜帖){return new PkButton(PkButtonType.榜帖.getIcon(),PkButtonType.榜帖.getName(),PkButtonType.榜帖.getLinkMethod());}
        if(pkButtonType == PkButtonType.审核){return new PkButton(PkButtonType.审核.getIcon(),PkButtonType.审核.getName(),PkButtonType.审核.getLinkMethod());}



//        boolean showButton = AppConfigService.getConfigAsBoolean(ConfigItem.对所有用户展示审核系统);
//
//        if(showButton)
//        {
//            return new PkButton(pkButtonType.getIcon(),pkButtonType.getName(),pkButtonType.getLinkMethod());
//
//        }
//        else
//        {
            if(userService.canUserView(userId,fromUser))
            {
                return new PkButton(pkButtonType.getIcon(),pkButtonType.getName(),pkButtonType.getLinkMethod());
            }
            else
            {
                return null;
            }

        }

//    }

    public PkButton 显示审核中按钮(String userId,String pkId) {
        if(!userService.canUserView(userId) && pkService.isPkCreator(pkId,userId))
        {
            return new PkButton(PkButtonType.审核中.getIcon(),PkButtonType.审核中.getName(),PkButtonType.审核中.getLinkMethod(),redisSortSetService.size(CacheKeyName.榜主审核中列表(pkId)));
        }
        else
        {
            return null;
        }







    }

    public PkActiveEntity 查询PK激活信息(String pkId) {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity pkActiveEntity = daoService.querySingleEntity(PkActiveEntity.class,filter2);
        return pkActiveEntity;
    }

    public List<PkActiveEntity> 查询需要激活的PK() {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("statu",CompareTag.Equal,ActiveStatu.待处理)
                .pageLimitFilter(1,20)
                .orderByRandomFilter();
        List<PkActiveEntity> pkActiveEntities = daoService.queryEntities(PkActiveEntity.class,filter2);
        return pkActiveEntities;

    }



    public CashierGroup 查询激活的群组信息(String groupId) {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("groupId",CompareTag.Equal,groupId);
        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter2);

        CashierGroup cashierGroup = new CashierGroup();
        cashierGroup.setGroupId(pkCashierGroupEntity.getGroupId());
        cashierGroup.setGroupUrl(pkCashierGroupEntity.getGroupUrl());
        cashierGroup.setTime("已上传:" + TimeUtils.距离上传的小时数(pkCashierGroupEntity.getCreateTime())  + "小时");
        cashierGroup.setMembers(dynamicService.查询群组分配的人数(pkCashierGroupEntity.getGroupId()));
        cashierGroup.setStatu(new KeyNameValue(pkCashierGroupEntity.getStatu().getStatu(),pkCashierGroupEntity.getStatu().getStatuStr()));
        return cashierGroup;

    }



    public CashierFeeCode 查询激活的收款码信息(String feeCodeId) {

        CashierFeeCode cashierFeeCode = new CashierFeeCode();
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                .compareFilter("feeCodeId",CompareTag.Equal,feeCodeId);
        PkCashierFeeCodeEntity pkCashierFeeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter2);


        cashierFeeCode.setFeeNumber(new KeyNameValue(pkCashierFeeCodeEntity.getFeeNumber().getTag(),String.valueOf(pkCashierFeeCodeEntity.getFeeNumber().getFee())));
        cashierFeeCode.setCashierId(pkCashierFeeCodeEntity.getCashierId());
        cashierFeeCode.setFeeCodeId(pkCashierFeeCodeEntity.getFeeCodeId());
        cashierFeeCode.setFeeCodeUrl(pkCashierFeeCodeEntity.getFeeCodeUrl());
        return cashierFeeCode;
    }

    public void 修改激活处理的状态(String pkId) {

        PkActiveEntity pkActiveEntity = this.查询PK激活信息(pkId);
        if(!ObjectUtils.isEmpty(pkActiveEntity) && !StringUtils.isBlank(pkActiveEntity.getScreenCutUrl()))
        {
            pkActiveEntity.setStatu(ActiveStatu.待处理);
            daoService.updateEntity(pkActiveEntity);
        }




    }

    public PkCashier 查询收款人(String cashierId) {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter2);



        PkCashier pkCashier = new PkCashier();
        pkCashier.setCashierId(pkCashierEntity.getCashierId());
        pkCashier.setCashierName(pkCashierEntity.getRealName());
        pkCashier.setSelectTimes(pkCashierEntity.getSelectTimes());
        pkCashier.setStatu(new KeyNameValue(pkCashierEntity.getStatu().getStatu(),pkCashierEntity.getStatu().getStatuStr()));

        return pkCashier;
    }

    public synchronized void 收款码确认次数加一(String feeCodeId) {

        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkCashierFeeCodeEntity.class)
                .compareFilter("feeCodeId",CompareTag.Equal,feeCodeId);
        PkCashierFeeCodeEntity pkCashierFeeCodeEntity = daoService.querySingleEntity(PkCashierFeeCodeEntity.class,filter2);
        pkCashierFeeCodeEntity.setConfirmTimes(pkCashierFeeCodeEntity.getConfirmTimes() + 1);
        daoService.updateEntity(pkCashierFeeCodeEntity);

    }

    public List<PkDetail> 查询PK排名(int page) throws IOException {


        List<PkDetail> pks = new ArrayList<>();
        List<String> pageList = redisSortSetService.queryPage(CacheKeyName.PK排名(),page);
        for(String pkId:pageList)
        {
            PkDetail pkDetail =pkService.querySinglePk(pkId);
            if(ObjectUtils.isEmpty(pkDetail)){continue;}
            pkDetail.setPriority(appService.查询优先级(pkId));
            pks.add(pkDetail);

        }


        return pks;


    }


    public void 添加到主页预览(String pkId, int value) throws AppException {
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);

        if(org.apache.commons.lang.ObjectUtils.equals(pkEntity.getPkType(),PkType.审核相册))
        {
            throw AppException.buildException(PageAction.信息反馈框("错误","审核相册无法添加到主页，只有遗传相册和内置相册可以添加"));
        }




        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);
        if(ObjectUtils.isEmpty(pk))
        {

            pk = new HomePagePk();
            pk.setPkId(pkId);
            pk.setPkType(pkEntity.getPkType());
            pk.setPriority(value);
            pk.setGroupStatu(1);
            pk.setApproving(0);
            pk.setApproved(0);
            daoService.insertEntity(pk);
        }
        else
        {
            pk.setPriority(value);
            pk.setPkType(pkEntity.getPkType());
            daoService.updateEntity(pk);
        }









    }

    public String 查询优先级(String pkId) {


        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);

        if(ObjectUtils.isEmpty(pk))
        {
            return null;
        }
        return pk.getPriority() + "";

    }

    public void 移除主页预览(String pkId) {

        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);

        if(!ObjectUtils.isEmpty(pk))
        {
            daoService.deleteEntity(pk);
        }

    }

    public List<BackImgEntity> 查询内置背景图片(int page,int type) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                .compareFilter("type",CompareTag.Equal,type)
                .pageLimitFilter(page,20);


        List<BackImgEntity> pkEntities = daoService.queryEntities(BackImgEntity.class,filter);


        return pkEntities;




    }

    public BackImgEntity 设置内置背景图片(int type, String imgUrl) {
        BackImgEntity backImgEntity = new BackImgEntity();
        backImgEntity.setImgId(com.union.app.util.idGenerator.IdGenerator.getImageId());

        backImgEntity.setImgUrl(imgUrl);
        backImgEntity.setType(type);

        daoService.insertEntity(backImgEntity);
        return backImgEntity;


    }

    public String 查询背景(int type) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                .compareFilter("type",CompareTag.Equal,type)
                .orderByRandomFilter()
                ;


        BackImgEntity backImgEntity = daoService.querySingleEntity(BackImgEntity.class,filter);


        return ObjectUtils.isEmpty(backImgEntity)?"":backImgEntity.getImgUrl();

    }

    public void 删除内置背景图片(String id) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                .compareFilter("imgId",CompareTag.Equal,id);

        BackImgEntity backImgEntity = daoService.querySingleEntity(BackImgEntity.class,filter);
        daoService.deleteEntity(backImgEntity);


    }

    public PreUserEntity 新增内置用户(String name, String imgUrl) throws UnsupportedEncodingException {

        String userId = com.union.app.util.idGenerator.IdGenerator.生成用户ID();
        PreUserEntity preUserEntity = new PreUserEntity();
        preUserEntity.setUserId(userId);
        preUserEntity.setImgUrl(imgUrl);
        preUserEntity.setName(name.getBytes("UTF-8"));
        daoService.insertEntity(preUserEntity);
        UserEntity userEntity = new UserEntity();

        userEntity.setOpenId(userId);
        userEntity.setUserId(userId);
        userEntity.setAvatarUrl(imgUrl);
        userEntity.setPkTimes(0);
        userEntity.setPostTimes(0);
        userEntity.setUserType(UserType.重点用户);
        userEntity.setNickName(name.getBytes("UTF-8"));
        daoService.insertEntity(userEntity);

        return preUserEntity;

    }

    public List<PreUserEntity> 查询内置用户(int page) throws UnsupportedEncodingException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .pageLimitFilter(page,50)
                .orderByRandomFilter();
        List<PreUserEntity> pkEntities = daoService.queryEntities(PreUserEntity.class,filter);
        for(PreUserEntity userEntity:pkEntities)
        {
            userEntity.setUserName(new String(userEntity.getName(),"UTF-8"));
        }
        return pkEntities;




    }

    public PreUserEntity 修改内置用户名称(String userId, String name) throws UnsupportedEncodingException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        preUserEntity.setName(name.getBytes("UTF-8"));
        daoService.updateEntity(preUserEntity);
        return preUserEntity;

    }

    public PreUserEntity 修改内置用户头像(String userId, String imgUrl) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        preUserEntity.setImgUrl(imgUrl);
        daoService.updateEntity(preUserEntity);
        return preUserEntity;
    }

    public String 随机用户() {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .orderByRandomFilter();
        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        return preUserEntity.getUserId();

    }
}
