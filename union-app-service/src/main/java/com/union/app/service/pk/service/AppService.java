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
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.entity.pk.*;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
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

    @Autowired
    PkDataService pkDataService;

    public List<PkDetail> 查询预设相册(int page,int type) throws IOException {


        if(type == 0)
        {
            return pkDataService.仅邀请的遗传主页列表(page);
        }

        if(type == 1)
        {
            return pkDataService.遗传主页列表(page);
        }

        if(type == 2)
        {
            return pkDataService.非遗传主页列表(page);
        }



        List<PkDetail> pkDetails = new ArrayList<>();

        return pkDetails;
    }


    private List<PkEntity> queryPrePks(int page,int type) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(BuildInPkEntity.class)
                .compareFilter("isInvite",CompareTag.Equal,type != 2 ? InviteType.邀请:InviteType.公开)
                .pageLimitFilter(page,20);
        List<BuildInPkEntity> buildInPkEntities = daoService.queryEntities(BuildInPkEntity.class,filter);
        List<Object> buildInPks = new ArrayList<>();
        buildInPkEntities.forEach(pk->{buildInPks.add(pk.getPkId());});

        if(CollectionUtils.isEmpty(buildInPks)){return new ArrayList<>();}

        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PkEntity.class)
                .inFilter("pkId",buildInPks);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter1);

        return pkEntities;
    }



    public PkDetail translate(PkEntity pkEntity) throws UnsupportedEncodingException {
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pkEntity.getPkId());
        pkDetail.setPkType(pkEntity.getPkType().getDesc());
        pkDetail.setTopic(pkEntity.getTopic());
        pkDetail.setUser(userService.queryUser(pkEntity.getUserId()));
        pkDetail.setWatchWord(pkEntity.getWatchWord());
//        pkDetail.setTotalApprover(new KeyNameValue(1,dynamicService.查询今日审核员数量(pkEntity.getPkId())));
//        pkDetail.setTotalSort(new KeyNameValue(2,dynamicService.查询今日打榜用户数量(pkEntity.getPkId())));
        pkDetail.setTime(TimeUtils.translateTime(pkEntity.getCreateTime()));
        pkDetail.setInvite(new KeyNameValue(pkEntity.getIsInvite().getStatu(),pkEntity.getIsInvite().getStatuStr()));
        pkDetail.setPkStatu(new KeyNameValue(ObjectUtils.isEmpty(pkEntity.getAlbumStatu())?PkStatu.审核中.getStatu():pkEntity.getAlbumStatu().getStatu(),ObjectUtils.isEmpty(pkEntity.getAlbumStatu())?PkStatu.审核中.getStatuStr():pkEntity.getAlbumStatu().getStatuStr()));



        return pkDetail;
    }
    public List<PkDetail> 查询内置相册(String userId, int page,int type) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryPrePks(page,type);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = pkService.querySinglePk(pkEntity.getPkId());
            pkDetail.setGeneticPriority(appService.查询优先级(pkEntity.getPkId(),1));
            pkDetail.setNonGeneticPriority(appService.查询优先级(pkEntity.getPkId(),2));
            pkDetails.add(pkDetail);
        }
        return pkDetails;

    }
    public List<PkDetail> 查询用户邀请(String userId, int page) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryUserInvitePks(userId,page);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = pkService.querySinglePk(pkEntity.getPkId());
            pkDetails.add(pkDetail);
        }
        return pkDetails;

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

        if(pkService.isPkCreator(pkId,userId)){return;}



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
            PkDetail pkDetail = pkService.querySinglePk(pkEntity.getPkId());
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
            pkCashier.setActiveCodes(pkCashierEntity.getActiveCodes());
            pkCashier.setNoActiveCodes(pkCashierEntity.getNoActiveCode());
            pkCashier.setUsedActiveCodes(pkCashierEntity.getUsedActiveCode());
            pkCashier.setStatu(new KeyNameValue(pkCashierEntity.getStatu().getStatu(),pkCashierEntity.getStatu().getStatuStr()));
            if(!ObjectUtils.isEmpty(pkCashierEntity.getLinkType()))
            {
                pkCashier.setType(new KeyNameValue(pkCashierEntity.getLinkType().getType(),pkCashierEntity.getLinkType().getDesc()));
                pkCashier.setLinkImg(pkCashierEntity.getLinkUrl());
            }
            pkCashier.setTime(TimeUtils.convertTime(pkCashierEntity.getTime()));
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

        if(cashierStatu == CashierStatu.启用 )
        {
            pkCashierEntity.setStatu(CashierStatu.停用);
        }
        else
        {
            if(!StringUtils.isBlank(pkCashierEntity.getLinkUrl())) {
                pkCashierEntity.setStatu(CashierStatu.启用);
            }
            else
            {
                throw AppException.buildException(PageAction.执行处理器("init",""));
            }

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

    public PkButton 显示按钮(PkButtonType pkButtonType) {
        if(pkButtonType == PkButtonType.榜帖){return new PkButton(PkButtonType.榜帖.getIcon(),PkButtonType.榜帖.getName(),PkButtonType.榜帖.getLinkMethod());}
        if(pkButtonType == PkButtonType.审核){return new PkButton(PkButtonType.审核.getIcon(),PkButtonType.审核.getName(),PkButtonType.审核.getLinkMethod());}
        if(pkButtonType == PkButtonType.群组){return new PkButton(PkButtonType.群组.getIcon(),PkButtonType.群组.getName(),PkButtonType.群组.getLinkMethod());}
        if(pkButtonType == PkButtonType.审核中){return new PkButton(PkButtonType.审核中.getIcon(),PkButtonType.审核中.getName(),PkButtonType.审核中.getLinkMethod());}
        return null;

    }



    public PkActiveEntity 查询PK激活信息(String pkId) {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity pkActiveEntity = daoService.querySingleEntity(PkActiveEntity.class,filter2);
        return pkActiveEntity;
    }

    public ActivePk 查询需要激活的PK() throws IOException {
        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("statu",CompareTag.Equal,ActiveStatu.待处理)
                .orderByRandomFilter();
        PkActiveEntity pkActiveEntity = daoService.querySingleEntity(PkActiveEntity.class,filter2);
        if(ObjectUtils.isEmpty(pkActiveEntity))
        {
            return null;
        }
        else
        {
            ActivePk activePk = new ActivePk();
            activePk.setPk(pkService.querySinglePk(pkActiveEntity.getPkId()));
            activePk.setApproveMessage(approveService.获取审核人员消息(pkActiveEntity.getPkId()));
            activePk.setRejectTimes(pkActiveEntity.rejectTime);
            activePk.setTipId(pkActiveEntity.tipId);
            return activePk;
        }


    }






//    public void 修改激活处理的状态(String pkId) {
//
//        PkActiveEntity pkActiveEntity = this.查询PK激活信息(pkId);
//        if(!ObjectUtils.isEmpty(pkActiveEntity) && !StringUtils.isBlank(pkActiveEntity.getScreenCutUrl()))
//        {
//            pkActiveEntity.setStatu(ActiveStatu.待处理);
//            daoService.updateEntity(pkActiveEntity);
//        }
//
//
//
//
//    }



    public List<PkDetail> 查询PK排名(int page) throws IOException {


        List<PkDetail> pks = new ArrayList<>();
        List<String> pageList = redisSortSetService.queryPage(CacheKeyName.PK排名(),page);
        for(String pkId:pageList)
        {
            PkDetail pkDetail =pkService.querySinglePk(pkId);
            if(!ObjectUtils.isEmpty(pkDetail)){
                pkDetail.setGeneticPriority(appService.查询优先级(pkId,1));
                pkDetail.setNonGeneticPriority(appService.查询优先级(pkId,2));
                pks.add(pkDetail);
            }

        }


        return pks;


    }


    public void 添加到主页预览(String pkId, int value, int type) throws AppException, UnsupportedEncodingException {
        if(!approveService.是否已发布审核消息(pkId)){throw AppException.buildException(PageAction.信息反馈框("添加错误","榜单未初始化审核公告"));}
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);

        if(pkEntity.getMessageType() == MessageType.收费 && type == 2)
        {
            throw AppException.buildException(PageAction.信息反馈框("添加错误","收费榜单不可以添加到非遗传用户首页"));
        }


        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);
        if(ObjectUtils.isEmpty(pk))
        {

            pk = new HomePagePk();
            pk.setPkId(pkId);
            pk.setPkType(pkEntity.getPkType());
            if(type == 1){pk.setGeneticPriority(value);}
            if(type == 2){pk.setNonGeneticPriority(value);}

            daoService.insertEntity(pk);
        }
        else
        {
            if(type == 1){pk.setGeneticPriority(value);}
            if(type == 2){pk.setNonGeneticPriority(value);}

            daoService.updateEntity(pk);
        }

    }

    public long 查询优先级(String pkId,int type) {


        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);

        if(ObjectUtils.isEmpty(pk))
        {
            return -1;
        }
        if(type == 1) {return pk.getGeneticPriority();}
        if(type == 2) {return pk.getNonGeneticPriority();}
        return -1;

    }

    public void 移除主页预览(String pkId,int type) {

        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);

        if(!ObjectUtils.isEmpty(pk))
        {
            if(type == 1){pk.setGeneticPriority(-1L);}
            if(type == 2){pk.setNonGeneticPriority(-1L);}
            if(pk.getGeneticPriority() < 0L && pk.getNonGeneticPriority() < 0)
            {
                daoService.deleteEntity(pk);
            }
            else
            {
                daoService.updateEntity(pk);
            }



        }

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
        userEntity.setNickName(name);
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

    public List<PkDetail> 查询用户主页(String userId,int page,int type) throws IOException {
        List<PkDetail> pkDetails = new ArrayList<>();

        if(type == 1)
        {
            if(userService.是否已经打榜(userId))
            {
                pkDetails = pkDataService.遗传主页列表(page);
            }
            else {
                pkDetails = pkDataService.仅邀请的遗传主页列表(page);
            }
        }
        else
        {
            pkDetails = pkDataService.非遗传主页列表(page);
        }



        return pkDetails;
    }


    private List<PkEntity> 遗传和内置相册列表(int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                    .compareFilter("pkType",CompareTag.Equal,PkType.运营相册)
                    .orFilter()
                    .compareFilter("pkType",CompareTag.Equal,PkType.内置相册)
                    .orderByFilter("priority",OrderTag.DESC)
                    .pageLimitFilter(page,20);

        List<HomePagePk> pkEntities = daoService.queryEntities(HomePagePk.class,filter);

        List<PkEntity> pks = new ArrayList<>();
        for(HomePagePk pk:pkEntities)
        {
            pks.add(pkService.querySinglePkEntity(pk.getPkId()));
        }
        return pks;
    }


    public void 上传收款链接(String cashierId, int type, String linkImg) throws IOException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);;
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
        String mediaId = WeChatUtil.uploadImg2Wx(linkImg);
        pkCashierEntity.setMediaId(mediaId);
        pkCashierEntity.setLastUpdateTime(System.currentTimeMillis());
        if(type == 1)
        {
            pkCashierEntity.setLinkType(LinkType.微店);
            pkCashierEntity.setLinkUrl(linkImg);
        }
        if(type == 2)
        {
            pkCashierEntity.setLinkType(LinkType.淘宝);
            pkCashierEntity.setLinkUrl(linkImg);
        }

        daoService.updateEntity(pkCashierEntity);

    }

    public void 删除收款人(String cashierId) throws AppException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);;
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
        if(!ObjectUtils.isEmpty(pkCashierEntity))
        {
            if(pkCashierEntity.getStatu() == CashierStatu.启用)
            {
                throw AppException.buildException(PageAction.执行处理器("closeCashier",""));
            }
            else
            {
                daoService.deleteEntity(pkCashierEntity);
                return;

            }

        }
        throw AppException.buildException(PageAction.执行处理器("noCashier",""));






    }

    public PkCashierEntity 获取微店(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("linkType",CompareTag.Equal,LinkType.微店)
                .andFilter()
                .compareFilter("statu",CompareTag.Equal,CashierStatu.启用)
                .orderByRandomFilter();
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
        return pkCashierEntity;
    }

    public PkCashierEntity 获取淘宝(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("linkType",CompareTag.Equal,LinkType.淘宝)
                .andFilter()
                .compareFilter("statu",CompareTag.Equal,CashierStatu.启用)
                .orderByRandomFilter();
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
        return pkCashierEntity;



    }

    public PkActive 查询激活信息(String pkId) throws UnsupportedEncodingException {
        PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
        if(ObjectUtils.isEmpty(pkActiveEntity))
        {
            return null;
        }
        else
        {
            PkActive pkActive = this.translatePkActive(pkActiveEntity);
            return pkActive;
        }

    }

    private PkActive translatePkActive(PkActiveEntity pkActiveEntity) throws UnsupportedEncodingException {
        PkActive pkActive = new PkActive();
        pkActive.setActiveCode(pkActiveEntity.getActiveCode());
        pkActive.setPkId(pkActiveEntity.getPkId());
        pkActive.setRejectTimes(pkActiveEntity.getRejectTime());
        pkActive.setMaxModifyTimes(AppConfigService.getConfigAsInteger(ConfigItem.PK最大修改次数));
        pkActive.setTip(StringUtils.isBlank(pkActiveEntity.getTipId())?"":this.查询Tip(pkActiveEntity.getTipId()));
        pkActive.setStatu(new KeyNameValue(pkActiveEntity.getStatu().getStatu(),pkActiveEntity.getStatu().getStatuStr()));

        return pkActive;
    }


    public PkActive 提交激活码(String pkId, String userId, String activeCode) throws AppException, UnsupportedEncodingException {
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        if(!StringUtils.equals(userId,pkEntity.getUserId())){throw AppException.buildException(PageAction.信息反馈框("非法操作","只有榜主才可以激活榜单..."));}
        if(!appService.validActiveCode(userId,activeCode)){throw AppException.buildException(PageAction.信息反馈框("无效激活码","请获取有效激活码..."));}

        EntityFilterChain filter =  EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity activeEntity = daoService.querySingleEntity(PkActiveEntity.class,filter);

        if(!ObjectUtils.isEmpty(activeEntity))
        {
//            PkEntity activePk = pkService.querySinglePkEntity(activeEntity.getPkId());
//            if(pkEntity.getAlbumStatu() == PkStatu.已审核 || pkEntity.getAlbumStatu() == PkStatu.已关闭)
//            {
            throw AppException.buildException(PageAction.信息反馈框("审核中","榜单审核中。。。"));
//            }
//            else
//            {
//                activeEntity.setPkId(pkId);
//                activeEntity.setTipId("");
//                activeEntity.setStatu(ActiveStatu.待处理);
//                daoService.updateEntity(activeEntity);
//            }
        }
        else
        {

            activeEntity = new PkActiveEntity();
            activeEntity.setPkId(pkId);
            activeEntity.setActiveCode(activeCode);
            activeEntity.setStatu(ActiveStatu.待处理);
            activeEntity.setRejectTime(0);
            activeEntity.setTipId("");
            daoService.insertEntity(activeEntity);
        }







        return translatePkActive(activeEntity);
    }

    private boolean validActiveCode(String userId,String activeCode) throws AppException {

//        激活码系统
        ActiveCodeEntity activeCodeEntity = this.查询激活码ById(activeCode);
        if(ObjectUtils.isEmpty(activeCodeEntity)){throw AppException.buildException(PageAction.信息反馈框("无效激活码","请获取有效激活码..."));}




        if(StringUtils.isBlank(activeCodeEntity.getUserId()))
        {
            activeCodeEntity.setUserId(userId);
            activeCodeEntity.setActiveTimes(activeCodeEntity.getActiveTimes() + 1);
            daoService.updateEntity(activeCodeEntity);
            PkCashierEntity cashierEntity = this.查询收款人(activeCodeEntity.getCashierId());
            cashierEntity.setUsedActiveCode(cashierEntity.getUsedActiveCode() + 1);
            daoService.updateEntity(cashierEntity);
            return true;
        }
        else
        {
            if(!StringUtils.equals(userId,activeCodeEntity.getUserId()))
            {
                throw AppException.buildException(PageAction.信息反馈框("无效激活码","该激活码已在其他用户名下..."));
            }
            else
            {
                int maxActiveTimes = AppConfigService.getConfigAsInteger(ConfigItem.激活码使用次数);
                if(activeCodeEntity.getActiveTimes() >= maxActiveTimes)
                {
                    throw AppException.buildException(PageAction.信息反馈框("无效激活码","激活码已使用" + maxActiveTimes+",该激活码已失效，请获取新的激活码..."));
                }
                else
                {
                    activeCodeEntity.setUserId(userId);
                    activeCodeEntity.setActiveTimes(activeCodeEntity.getActiveTimes() + 1);
                    daoService.updateEntity(activeCodeEntity);
                    PkCashierEntity cashierEntity = this.查询收款人(activeCodeEntity.getCashierId());

                    daoService.updateEntity(cashierEntity);
                    return true;
                }
            }


        }




    }

    public void 重新激活PK(String pkId, String userId) throws AppException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        if(!StringUtils.equals(userId,pkEntity.getUserId())){throw AppException.buildException(PageAction.信息反馈框("非法操作","只有榜主才可以激活榜单..."));}
        PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
        if(pkActiveEntity.getRejectTime() >= AppConfigService.getConfigAsInteger(ConfigItem.PK最大修改次数) ){throw AppException.buildException(PageAction.信息反馈框("提交失败","榜单已经超过最大修改次数..."));}
        pkActiveEntity.setStatu(ActiveStatu.待处理);
        pkActiveEntity.setRejectTime(pkActiveEntity.getRejectTime() + 1);
        daoService.updateEntity(pkActiveEntity);

    }

    public List<ActiveTip> 查询所有提示信息() {
        EntityFilterChain filter =  EntityFilterChain.newFilterChain(ActiveTipEntity.class);
        List<ActiveTipEntity> activeTipEntities = daoService.queryEntities(ActiveTipEntity.class,filter);


        List<ActiveTip> activeTips = new ArrayList<>();
        activeTipEntities.forEach(tip ->{
            ActiveTip activeTip = new ActiveTip();
            activeTip.setId(tip.getId());
            try {
                activeTip.setTip(new String(tip.getTipStr(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            activeTips.add(activeTip);
        });



        return activeTips;
    }

    public void 添加Tip(String tip) throws UnsupportedEncodingException {
        ActiveTipEntity activeTipEntity = new ActiveTipEntity();
        activeTipEntity.setId(com.union.app.util.idGenerator.IdGenerator.getActiveTipId());
        activeTipEntity.setTipStr(tip.getBytes("UTF-8"));
        daoService.insertEntity(activeTipEntity);

    }
    private String 查询Tip(String tipId) throws UnsupportedEncodingException {
        EntityFilterChain filter =  EntityFilterChain.newFilterChain(ActiveTipEntity.class)
                .compareFilter("id",CompareTag.Equal,tipId);
        ActiveTipEntity activeTipEntity = daoService.querySingleEntity(ActiveTipEntity.class,filter);
        if(!ObjectUtils.isEmpty(activeTipEntity))
        {
            return new String(activeTipEntity.getTipStr(),"UTF-8");
        }
        else
        {
            return "";
        }

    }

    public void 删除Tip(String id) {
        EntityFilterChain filter =  EntityFilterChain.newFilterChain(ActiveTipEntity.class)
                .compareFilter("id",CompareTag.Equal,id);
        ActiveTipEntity activeTipEntity = daoService.querySingleEntity(ActiveTipEntity.class,filter);
        if(!ObjectUtils.isEmpty(activeTipEntity))
        {
            daoService.deleteEntity(activeTipEntity);
        }



    }

    public List<DataSet> 查询下一个审核榜帖() throws IOException {
        Post post = postService.查询需要审核的帖子();
        List<DataSet> dataSets = new ArrayList<>();


        if(ObjectUtils.isEmpty(post))
        {
            dataSets.add(new DataSet("pk",null));
            dataSets.add(new DataSet("post",null));

        }
        else
        {
            PkDetail pkDetail = pkService.querySinglePk(post.getPkId());
            pkDetail.setApproveMessage(approveService.获取审核人员消息(post.getPkId()));
            dataSets.add(new DataSet("pk",pkDetail));
            dataSets.add(new DataSet("post",post));

        }
        return dataSets;
    }

    public void 移除预置表(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(BuildInPkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        BuildInPkEntity pkEntity = daoService.querySingleEntity(BuildInPkEntity.class,filter);
        daoService.deleteEntity(pkEntity);
    }


    private ActiveCodeEntity 查询激活码(String cashierId)
    {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(ActiveCodeEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .andFilter()
                .compareFilter("codeStatu",CompareTag.Equal,CodeStatu.未启用)
                .orderByFilter("time",OrderTag.ASC);
        ActiveCodeEntity activeCodeEntity = daoService.querySingleEntity(ActiveCodeEntity.class,filter1);

        return activeCodeEntity;

    }
    private ActiveCodeEntity 查询激活码ById(String activeCode)
    {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(ActiveCodeEntity.class)
                .compareFilter("activeCode",CompareTag.Equal,activeCode);
        ActiveCodeEntity activeCodeEntity = daoService.querySingleEntity(ActiveCodeEntity.class,filter1);
        return activeCodeEntity;

    }
    public PkCashierEntity 查询收款人(String cashierId)
    {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);;
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);
        return pkCashierEntity;
    }
    public String 获取收款码(String applyCode) throws AppException {
        PkCashierEntity pkCashierEntity = this.查询收款人(applyCode);
        if(ObjectUtils.isEmpty(pkCashierEntity)){
            throw AppException.buildException(PageAction.信息反馈框("无效口令","无效口令"));
        }
        if(pkCashierEntity.getStatu() != CashierStatu.启用)
        {
            throw AppException.buildException(PageAction.信息反馈框("用户未启用","用户未启用，启用后可以获取激活码"));
        }
        ActiveCodeEntity activeCodeEntity = this.查询激活码(applyCode);
        if(ObjectUtils.isEmpty(activeCodeEntity))
        {
            throw AppException.buildException(PageAction.信息反馈框("没有可用激活码","没有可用激活码，联系管理员储备更多激活码"));
        }
        activeCodeEntity.setCodeStatu(CodeStatu.已启用);
        pkCashierEntity.setNoActiveCode(pkCashierEntity.getNoActiveCode() + 1);
        daoService.updateEntity(activeCodeEntity);

        return activeCodeEntity.getActiveCode();


    }

    public void 生成激活码(String cashierId) {
        int i=0;

        while(i<100)
        {
            String code = IdGenerator.getActiveCode();
            ActiveCodeEntity activeCodeEntity = 查询激活码ById(code);
            if(ObjectUtils.isEmpty(activeCodeEntity))
            {
                activeCodeEntity = new ActiveCodeEntity();
                activeCodeEntity.setCashierId(cashierId);
                activeCodeEntity.setCodeStatu(CodeStatu.未启用);
                activeCodeEntity.setActiveCode(code);
                activeCodeEntity.setActiveTimes(0);
                activeCodeEntity.setTime(System.currentTimeMillis());
                daoService.insertEntity(activeCodeEntity);
                i++;
            }
        }












    }

    public void 新增储备激活码(String cashierId) {
        PkCashierEntity pkCashierEntity = 查询收款人(cashierId);
        if(!ObjectUtils.isEmpty(pkCashierEntity))
        {
            pkCashierEntity.setActiveCodes(pkCashierEntity.getActiveCodes() + 100);
            daoService.updateEntity(pkCashierEntity);
        }



    }

    public void 提交非遗传用户激活码(String pkId, String userId) throws AppException {
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        if(!StringUtils.equals(userId,pkEntity.getUserId())){throw AppException.buildException(PageAction.信息反馈框("非法操作","只有榜主才可以激活榜单..."));}
        if(userService.是否是遗传用户(userId)){throw AppException.buildException(PageAction.信息反馈框("系统错误","服务器出现错误，请您稍后重试..."));}
        EntityFilterChain filter =  EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity activeEntity = daoService.querySingleEntity(PkActiveEntity.class,filter);

        if(ObjectUtils.isEmpty(activeEntity))
        {
            activeEntity = new PkActiveEntity();
            activeEntity.setPkId(pkId);
            activeEntity.setActiveCode(this.获取系统默认激活码());
            activeEntity.setStatu(ActiveStatu.待处理);
            activeEntity.setRejectTime(0);
            activeEntity.setTipId("");
            daoService.insertEntity(activeEntity);
        }




    }

    private String 获取系统默认激活码() {
        return UUID.randomUUID().toString();
    }
}
