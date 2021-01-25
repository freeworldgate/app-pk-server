package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.卡点.标签.ActiveTipEntity;
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
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class AppService {




    @Autowired
    PayService payService;

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


    @Autowired
    LocationService locationService;


    @Autowired
    UserDynamicService userDynamicService;

    public List<PkDetail> 查询预设相册(int page,int type) throws IOException {


//        if(type == 0)
//        {
//            return pkDataService.仅邀请的遗传主页列表(page);
//        }

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
                .pageLimitFilter(page,20)
                .orderByFilter("createTime",OrderTag.DESC);

        List<BuildInPkEntity> buildInPkEntities = daoService.queryEntities(BuildInPkEntity.class,filter);
        List<Object> buildInPks = new ArrayList<>();
        buildInPkEntities.forEach(pk->{buildInPks.add(pk.getPkId());});

        if(CollectionUtils.isEmpty(buildInPks)){return new ArrayList<>();}

        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PkEntity.class)
                .inFilter("pkId",buildInPks);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter1);

        return pkEntities;
    }




    public List<PkDetail> 查询内置相册( int page,int type) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryPrePks(page,type);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = pkService.querySinglePk(pkEntity.getPkId());
//            pkDetail.setGeneticPriority(appService.查询优先级(pkEntity.getPkId(),1));
//            pkDetail.setNonGeneticPriority(appService.查询优先级(pkEntity.getPkId(),2));
            pkDetails.add(pkDetail);
        }
        return pkDetails;

    }
    public List<PkDetail> 查询用户邀请(String userId, int page,double latitude,double longitude) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryUserInvitePks(userId,page);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = locationService.querySinglePk(pkEntity);
            int length = locationService.计算坐标间距离(latitude,longitude,pkEntity.getLatitude(),pkEntity.getLongitude());
            pkDetail.setUserLength(length);
            pkDetail.setUserLengthStr(locationService.距离转换成描述(length));
            pkDetails.add(pkDetail);
        }
        return pkDetails;

    }





    private List<PkEntity> queryUserInvitePks(String userId, int page) {
        List<PkEntity> pkEntities = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(InvitePkEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("createTime",OrderTag.DESC);
        List<InvitePkEntity> invites = daoService.queryEntities(InvitePkEntity.class,filter);

        List<Object> pkIds = new ArrayList<>();
        for(InvitePkEntity invitePkEntity:invites)
        {
            pkIds.add(invitePkEntity.getPkId());
        }
        //批量查询
        if(!org.apache.commons.collections4.CollectionUtils.isEmpty(pkIds)) {
            EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PkEntity.class)
                    .inFilter("pkId", pkIds);
            List<PkEntity> pks = daoService.queryEntities(PkEntity.class, filter1);
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(pks)) {
                pkEntities.addAll(pks);
            }
        }
        return pkEntities;
    }


    public void 添加邀请(String pkId, String userId) throws IOException {


        if(StringUtils.isBlank(pkId)){return;}


        InvitePkEntity invitePkEntity = queryInvitePk(pkId,userId);
        if(ObjectUtils.isEmpty(invitePkEntity))
        {
            invitePkEntity = new InvitePkEntity();
            invitePkEntity.setPkId(pkId);
            invitePkEntity.setUserId(userId);
            invitePkEntity.setCreateTime(System.currentTimeMillis());
            daoService.insertEntity(invitePkEntity);
            userDynamicService.邀请次数加一(userId);
            dynamicService.valueIncr(CacheKeyName.收藏,pkId);
        }
        else
        {
            daoService.deleteEntity(invitePkEntity);
            userDynamicService.邀请次数减一(userId);
            dynamicService.valueDecr(CacheKeyName.收藏,pkId);
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

    public List<PkDetail> 查询用户相册(String userId,double latitude, double longitude,int page) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  pkEntities = queryUserPks(userId,page);
        for(PkEntity pkEntity:pkEntities)
        {
            PkDetail pkDetail = locationService.querySinglePk(pkEntity);
            int length = locationService.计算坐标间距离(latitude,longitude,pkEntity.getLatitude(),pkEntity.getLongitude());
            pkDetail.setUserLength(length);
            pkDetail.setUserLengthStr(locationService.距离转换成描述(length));
            System.out.println(pkEntity.getName() + " : " + locationService.距离转换成描述(length));
            pkDetails.add(pkDetail);
        }

        return pkDetails;

    }

    private List<PkEntity> queryUserPks(String userId, int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("time",OrderTag.DESC);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);

        return pkEntities;
    }

    private List<PkEntity> queryUserPublishPks(String userId, int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("active",CompareTag.Equal,Boolean.TRUE)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("createTime",OrderTag.DESC);
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


    private List<PkCashierEntity> queryActivePkCashiers() {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("statu",CompareTag.Equal,CashierStatu.启用)
                .orderByFilter("time",OrderTag.DESC);
        List<PkCashierEntity> pkCashierEntities = daoService.queryEntities(PkCashierEntity.class,filter);
        return pkCashierEntities;
    }

    private List<PkCashierEntity> queryPkCashiers() {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .orderByFilter("time",OrderTag.DESC);
        List<PkCashierEntity> pkCashierEntities = daoService.queryEntities(PkCashierEntity.class,filter);

        return pkCashierEntities;
    }
    public List<PkCashier> 查询收款列表() throws IOException {


        List<PkCashier> pkCashiers = new ArrayList<>();

        List<PkCashierEntity>  pkCashierEntities = queryPkCashiers();
        for(PkCashierEntity pkCashierEntity:pkCashierEntities)
        {
            PkCashier pkCashier = new PkCashier();
            pkCashier.setCashierId(pkCashierEntity.getCashierId());
            pkCashier.setCashierName(pkCashierEntity.getRealName());
            pkCashier.setStatu(new KeyNameValue(pkCashierEntity.getStatu().getStatu(),pkCashierEntity.getStatu().getStatuStr()));
            pkCashier.setImg(pkCashierEntity.getImg());
            pkCashier.setBackPng(pkCashierEntity.getBackPng());
            pkCashier.setTime(TimeUtils.convertTime(pkCashierEntity.getTime()));
            pkCashiers.add(pkCashier);

        }

        return pkCashiers;

    }


    private  static volatile List<PkCashier> cashiers = new ArrayList<>();
    private  static volatile long cashiersUpdateTime;


    public List<PkCashier> 查询有效收款列表() throws IOException {

        if(CollectionUtils.isEmpty(cashiers) || cashiersUpdateTime <(System.currentTimeMillis()-AppConfigService.getConfigAsInteger(ConfigItem.缓存时间)*1000))
        {
            List<PkCashier> pkCashiers = new ArrayList<>();
            List<PkCashierEntity>  pkCashierEntities = queryActivePkCashiers();
            for(PkCashierEntity pkCashierEntity:pkCashierEntities)
            {
                PkCashier pkCashier = new PkCashier();
                pkCashier.setCashierId(pkCashierEntity.getCashierId());
                pkCashier.setCashierName(pkCashierEntity.getRealName());
                pkCashier.setStatu(new KeyNameValue(pkCashierEntity.getStatu().getStatu(),pkCashierEntity.getStatu().getStatuStr()));
                pkCashier.setImg(pkCashierEntity.getImg());
                pkCashier.setBackPng(pkCashierEntity.getBackPng());
                pkCashier.setTime(TimeUtils.convertTime(pkCashierEntity.getTime()));
                pkCashiers.add(pkCashier);

            }
            cashiers = pkCashiers;
            cashiersUpdateTime = System.currentTimeMillis();
        }
        return cashiers;




    }













    public synchronized void 新建收款用户(String name) {

        PkCashierEntity pkCashierEntity = new PkCashierEntity();
        pkCashierEntity.setCashierId(com.union.app.util.idGenerator.IdGenerator.收款用户ID());
        pkCashierEntity.setRealName(name);
        pkCashierEntity.setStatu(CashierStatu.停用);
        pkCashierEntity.setTime(System.currentTimeMillis());
        daoService.insertEntity(pkCashierEntity);


    }
    private List<PkCashierGroupEntity> queryPkCashierGroups(String cashierId,int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId)
                .pageLimitFilter(page,20);
        List<PkCashierGroupEntity> pkCashierGroupEntities = daoService.queryEntities(PkCashierGroupEntity.class,filter);
        return pkCashierGroupEntities;
    }

//    public List<CashierGroup> 查询用户群组(String cashierId, int page) {
//        List<CashierGroup> pkCashiers = new ArrayList<>();
//        List<PkCashierGroupEntity>  pkCashierGroupEntities = queryPkCashierGroups(cashierId,page);
//        for(PkCashierGroupEntity pkCashierGroupEntity:pkCashierGroupEntities)
//        {
//            CashierGroup cashierGroup = new CashierGroup();
//            cashierGroup.setGroupId(pkCashierGroupEntity.getGroupId());
//            cashierGroup.setGroupUrl(pkCashierGroupEntity.getGroupUrl());
//            cashierGroup.setTime("已上传:" + TimeUtils.距离上传的小时数(pkCashierGroupEntity.getCreateTime())  + "小时");
//            cashierGroup.setMembers(dynamicService.查询群组分配的人数(pkCashierGroupEntity.getGroupId()));
//            cashierGroup.setStatu(new KeyNameValue(pkCashierGroupEntity.getStatu().getStatu(),pkCashierGroupEntity.getStatu().getStatuStr()));
//            pkCashiers.add(cashierGroup);
//        }
//
//        return pkCashiers;
//
//    }


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
//                cashierFeeCode.setSelectTime(dynamicService.查询收款码分配的人数(pkCashierFeeCodeEntity.getFeeCodeId()));
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
            if(!StringUtils.isBlank(pkCashierEntity.getBackPng()) && !StringUtils.isBlank(pkCashierEntity.getImg())  ) {
                pkCashierEntity.setStatu(CashierStatu.启用);
            }
            else
            {
                throw AppException.buildException(PageAction.信息反馈框("图片和PNG背景不全","请上传图片和PNG背景"));
            }

        }
        daoService.updateEntity(pkCashierEntity);
    }










//
//
//    public synchronized PkCashierGroupEntity selectRandomGroup(List<String> cashiers)
//    {
//        List<Object> legalCashiers = new ArrayList<>();
//        legalCashiers.addAll(cashiers);
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierGroupEntity.class)
//                .compareFilter("statu",CompareTag.Equal,GroupStatu.启用)
//                .andFilter()
//                .inFilter("cashierId",legalCashiers)
//                .andFilter()
//                .compareFilter("createTime",CompareTag.Bigger,System.currentTimeMillis() - AppConfigService.getConfigAsLong(ConfigItem.群组最长上传时间间隔) * 3600 * 1000)
//                .orderByRandomFilter();
//        PkCashierGroupEntity pkCashierGroupEntity = daoService.querySingleEntity(PkCashierGroupEntity.class,filter);
//        //首先确定群组
//        if(!ObjectUtils.isEmpty(pkCashierGroupEntity))
//        {
//            if(dynamicService.查询群组分配的人数(pkCashierGroupEntity.getGroupId()) >= AppConfigService.getConfigAsInteger(ConfigItem.单个群最大人数))
//            {
//                pkCashierGroupEntity.setStatu(GroupStatu.停用);
//                daoService.updateEntity(pkCashierGroupEntity);
//            }
//        }
//        return pkCashierGroupEntity;
//
//    }









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


    public PkButton 显示按钮(PkButtonType pkButtonType) {
//        if(pkButtonType == PkButtonType.榜帖){return new PkButton(PkButtonType.榜帖.getIcon(),PkButtonType.榜帖.getName(),PkButtonType.榜帖.getLinkMethod());}
//        if(pkButtonType == PkButtonType.审核){return new PkButton(PkButtonType.审核.getIcon(),PkButtonType.审核.getName(),PkButtonType.审核.getLinkMethod());}
//        if(pkButtonType == PkButtonType.群组){return new PkButton(PkButtonType.群组.getIcon(),PkButtonType.群组.getName(),PkButtonType.群组.getLinkMethod());}
//        if(pkButtonType == PkButtonType.审核中){return new PkButton(PkButtonType.审核中.getIcon(),PkButtonType.审核中.getName(),PkButtonType.审核中.getLinkMethod());}
        return new PkButton(pkButtonType.getIcon(),pkButtonType.getName(),pkButtonType.getLinkMethod(),pkButtonType.getStyle(),pkButtonType.getType());

    }






    public List<PkDetail> 查询PK排名(int page) throws IOException {


        List<PkDetail> pks = new ArrayList<>();
        List<String> pageList = redisSortSetService.queryPage(CacheKeyName.PK排名,page);
        for(String pkId:pageList)
        {
            PkDetail pkDetail =pkService.querySinglePk(pkId);
            if(!ObjectUtils.isEmpty(pkDetail)){
//                pkDetail.setGeneticPriority(appService.查询优先级(pkId,1));
//                pkDetail.setNonGeneticPriority(appService.查询优先级(pkId,2));
                pks.add(pkDetail);
            }

        }


        return pks;


    }


    public void 添加到主页预览(String pkId, int value, int type) throws AppException, UnsupportedEncodingException {
//        if(!approveService.是否已发布审核消息(pkId)){throw AppException.buildException(PageAction.信息反馈框("添加错误","榜单未初始化审核公告"));}


//        if(pkEntity.getMessageType() == MessageType.收费 && type == 2)
//        {
//            throw AppException.buildException(PageAction.信息反馈框("添加错误","收费榜单不可以添加到非遗传用户首页"));
//        }


        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);
        if(ObjectUtils.isEmpty(pk))
        {
            PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
            pk = new HomePagePk();
            pk.setPkId(pkId);
//            pk.setPkType(pkEntity.getPkType());
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




    private static final Map<Integer,List<BackImgEntity>> imgs = new HashMap<>();
    private static final Map<Integer,Long> imgUpdates = new HashMap<>();


    public String 查询背景(int type) {

        BackImgEntity backImgEntity = 更新图片缓存(type);

        return ObjectUtils.isEmpty(backImgEntity)?"":backImgEntity.getImgUrl();

    }

    public BackImgEntity 更新图片缓存(int type)
    {
        List<BackImgEntity> typeImgs = imgs.get(type);
        Long updateTime = imgUpdates.get(type);
        if(CollectionUtils.isEmpty(typeImgs) || updateTime==null || updateTime <(System.currentTimeMillis()-AppConfigService.getConfigAsInteger(ConfigItem.缓存时间)*1000))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                    .compareFilter("type",CompareTag.Equal,type)
                    .orderByRandomFilter()
                    ;
            typeImgs = daoService.queryEntities(BackImgEntity.class,filter);
            imgs.put(type,typeImgs);
            imgUpdates.put(type,System.currentTimeMillis());
        }
        if(CollectionUtils.isEmpty(typeImgs)){return null;}
        Random random = new Random();
        int n = random.nextInt(typeImgs.size());
        return typeImgs.get(n);

    }












    public void 删除内置背景图片(String id) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                .compareFilter("imgId",CompareTag.Equal,id);

        BackImgEntity backImgEntity = daoService.querySingleEntity(BackImgEntity.class,filter);
        daoService.deleteEntity(backImgEntity);


    }
    private volatile boolean userType = true;
    public PreUserEntity 新增内置用户(String name, String imgUrl) throws UnsupportedEncodingException {

        String userId = com.union.app.util.idGenerator.IdGenerator.生成用户ID();
        PreUserEntity preUserEntity = new PreUserEntity();
        preUserEntity.setUserId(userId);
        preUserEntity.setImgUrl(imgUrl);
        preUserEntity.setUserName(name);


        UserEntity userEntity = new UserEntity();

        userEntity.setOpenId(userId);
        userEntity.setUserId(userId);
        userEntity.setAvatarUrl(imgUrl);





        userEntity.setNickName(name);

        if(userType)
        {
            preUserEntity.setUserType(UserType.重点用户);
            userEntity.setUserType(UserType.重点用户);

        }
        else
        {
            preUserEntity.setUserType(UserType.普通用户);
            userEntity.setUserType(UserType.普通用户);
        }
        userType = !userType;

        daoService.insertEntity(preUserEntity);
        daoService.insertEntity(userEntity);


//        UserDynamicEntity userDynamicEntity = new UserDynamicEntity();
//        userDynamicEntity.setPkTimes(0);
//        userDynamicEntity.setPostTimes(0);
//        userDynamicEntity.setPublishPkTimes(0);
//        userDynamicEntity.setActivePks(0);
//        userDynamicEntity.setPostTimes(0);
//        userDynamicEntity.setFeeTimes(1000000);
//        daoService.insertEntity(userDynamicEntity);



        return preUserEntity;

    }

    public List<PreUserEntity> 查询内置用户(int page) throws UnsupportedEncodingException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .pageLimitFilter(page,50)
                .orderByRandomFilter();
        List<PreUserEntity> pkEntities = daoService.queryEntities(PreUserEntity.class,filter);
        for(PreUserEntity userEntity:pkEntities)
        {
            userEntity.setUserName(userEntity.getUserName());
        }
        return pkEntities;




    }

    public PreUserEntity 修改内置用户名称(String userId, String name) throws UnsupportedEncodingException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        preUserEntity.setUserName(name);
        preUserEntity.setUserType(preUserEntity.getUserType() == null?UserType.重点用户:preUserEntity.getUserType());

        UserEntity userEntity = userService.queryUserEntity(userId);
        if(ObjectUtils.isEmpty(userEntity))
        {
            userEntity = new UserEntity();
            userEntity.setOpenId(preUserEntity.getUserId());
            userEntity.setUserId(preUserEntity.getUserId());
            userEntity.setAvatarUrl(preUserEntity.getImgUrl());
//            userEntity.setPkTimes(0);
//            userEntity.setPostTimes(0);
            userEntity.setNickName(name);
            userEntity.setUserType(preUserEntity.getUserType());
            daoService.insertEntity(userEntity);

        }
        else
        {
            userEntity.setNickName(name);
            userEntity.setUserType(preUserEntity.getUserType());
            daoService.updateEntity(userEntity);
        }

        daoService.updateEntity(preUserEntity);




        return preUserEntity;

    }

    public PreUserEntity 修改内置用户头像(String userId, String imgUrl) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        preUserEntity.setImgUrl(imgUrl);
        preUserEntity.setUserType(preUserEntity.getUserType() == null?UserType.重点用户:preUserEntity.getUserType());

        UserEntity userEntity = userService.queryUserEntity(userId);
        if(ObjectUtils.isEmpty(userEntity))
        {
            userEntity = new UserEntity();
            userEntity.setOpenId(preUserEntity.getUserId());
            userEntity.setUserId(preUserEntity.getUserId());
            userEntity.setAvatarUrl(imgUrl);
//            userEntity.setPkTimes(0);
//            userEntity.setPostTimes(0);
            userEntity.setNickName(preUserEntity.getUserName());
            userEntity.setUserType(preUserEntity.getUserType());
            daoService.insertEntity(userEntity);

        }
        else
        {
            userEntity.setAvatarUrl(imgUrl);
            userEntity.setUserType(preUserEntity.getUserType());
            daoService.updateEntity(userEntity);
        }

        daoService.updateEntity(preUserEntity);
        return preUserEntity;

    }
    public String 随机用户() {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                    .orderByRandomFilter();

        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        return preUserEntity.getUserId();

    }

    public String 随机用户(int type) {
        EntityFilterChain filter = null;
        if(type==2)
        {
            filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                    .compareFilter("userType",CompareTag.Equal,UserType.重点用户)
                    .orderByRandomFilter();
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PreUserEntity.class)
                    .compareFilter("userType",CompareTag.Equal,UserType.普通用户)
                    .orderByRandomFilter();
        }

        PreUserEntity preUserEntity = daoService.querySingleEntity(PreUserEntity.class,filter);
        return preUserEntity.getUserId();

    }

//    public List<PkDetail> 查询用户主页(String userId,int page,int type) throws IOException {
//        List<PkDetail> pkDetails = new ArrayList<>();
//
//        if(type == 1)
//        {
//            if(userService.是否已经打榜(userId))
//            {
//                pkDetails = pkDataService.遗传主页列表(page);
//            }
//            else {
//                pkDetails = pkDataService.仅邀请的遗传主页列表(page);
//            }
//        }
//        else
//        {
//            pkDetails = pkDataService.非遗传主页列表(page);
//        }
//
//
//
//        return pkDetails;
//    }
//
//
//    private List<PkEntity> 遗传和内置相册列表(int page) {
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(HomePagePk.class)
//                    .compareFilter("pkType",CompareTag.Equal,PkType.运营相册)
//                    .orFilter()
//                    .compareFilter("pkType",CompareTag.Equal,PkType.内置相册)
//                    .orderByFilter("priority",OrderTag.DESC)
//                    .pageLimitFilter(page,20);
//
//        List<HomePagePk> pkEntities = daoService.queryEntities(HomePagePk.class,filter);
//
//        List<PkEntity> pks = new ArrayList<>();
//        for(HomePagePk pk:pkEntities)
//        {
//            pks.add(pkService.querySinglePkEntity(pk.getPkId()));
//        }
//        return pks;
//    }
//

    public void 上传收款链接(String cashierId, int type, String linkImg) throws IOException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCashierEntity.class)
                .compareFilter("cashierId",CompareTag.Equal,cashierId);;
        PkCashierEntity pkCashierEntity = daoService.querySingleEntity(PkCashierEntity.class,filter);


        if(type == 1)
        {
            pkCashierEntity.setImg(linkImg);
        }
        if(type == 2)
        {
            pkCashierEntity.setBackPng(linkImg);
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



    public List<DataSet> 查询下一个审核榜帖(int type) throws IOException {
        Post post = postService.查询需要审核的帖子(type);
        List<DataSet> dataSets = new ArrayList<>();


        if(ObjectUtils.isEmpty(post))
        {
            dataSets.add(new DataSet("pk",null));
            dataSets.add(new DataSet("post",null));

        }
        else
        {
            PkDetail pkDetail = pkService.querySinglePk(post.getPkId());
//            pkDetail.setApproveMessage(approveService.获取审核人员消息(post.getPkId()));
            dataSets.add(new DataSet("pk",pkDetail));
            dataSets.add(new DataSet("post",post));

        }
        return dataSets;
    }



    public void 验证Password(String password) throws AppException {

        if(!StringUtils.equalsIgnoreCase(password,"fenghaoapp1"))
        {
            throw AppException.buildException(PageAction.信息反馈框("登录错误","登录错误"));

        }




    }




    public static String getPolicy()
    {

        String policy
                = "{\"expiration\": \"2120-01-01T12:00:00.000Z\",\"conditions\": [[\"content-length-range\", 0, 104857600]]}";
        String encodePolicy = new String(org.apache.commons.codec.binary.Base64.encodeBase64(policy.getBytes()));
        return encodePolicy;
    }


    public static String getSignature(String accessKeySecret)
    {

        String signaturecom = computeSignature(accessKeySecret, getPolicy());

        return signaturecom;
    }

    private static String computeSignature(String accessKeySecret, String encodePolicy)
             {
                 try {
                     // convert to UTF-8
                     byte[] key = accessKeySecret.getBytes("UTF-8");
                     byte[] data = encodePolicy.getBytes("UTF-8");

                     // hmac-sha1
                     Mac mac = Mac.getInstance("HmacSHA1");
                     mac.init(new SecretKeySpec(key, "HmacSHA1"));
                     byte[] sha = mac.doFinal(data);

                     // base64
                     return new String(Base64.encodeBase64(sha));

                 }catch (Exception e){
                    return "";
                 }
             }


    public String 生成二维码(String pkId, String postId)  {

        try {
            return WeChatUtil.生成二维码(pkId);
        } catch (IOException e) {
            return this.查询背景(9);
        }

    }

    public boolean 是否收费(String userId) {
        if(userService.是否是遗传用户(userId))
        {
            return AppConfigService.getConfigAsBoolean(ConfigItem.遗传用户是否收费);

        }
        else
        {
            return AppConfigService.getConfigAsBoolean(ConfigItem.普通用户是否收费);
        }


    }

    public JSONObject 获取支付信息(String userId,HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("single",payService.single支付方案(userId,request));
        jsonObject.put("all",payService.all支付方案(userId,request));
        return jsonObject;





    }
    public List<Post> 查询用户发布图册(String userId, int page) throws IOException {


        List<Post> posts = new ArrayList<>();
        List<PostEntity>  postEntities = queryUserPublishPosts(userId,page);
        if(CollectionUtils.isEmpty(postEntities)){return posts;}
        Map<String,PkEntity> allPks = queryPostPks(postEntities);
        for(PostEntity postEntity:postEntities)
        {
            PkEntity pkEntity = allPks.get(postEntity.getPkId());
            if(!ObjectUtils.isEmpty(pkEntity))
            {
                Post post = postService.translate(postEntity);
                post.setPkTopic(pkEntity.getName());
                posts.add(post);
            }

        }

        return posts;




    }


    public List<Post> 查询用户图册(String userId, int page) throws UnsupportedEncodingException {


        List<Post> posts = new ArrayList<>();
        List<PostEntity>  postEntities = queryUserPosts(userId,page);
        if(CollectionUtils.isEmpty(postEntities)){return posts;}
        Map<String,PkEntity> allPks = queryPostPks(postEntities);
        for(PostEntity postEntity:postEntities)
        {
            PkEntity pkEntity = allPks.get(postEntity.getPkId());
            if(!ObjectUtils.isEmpty(pkEntity))
            {
                Post post = postService.translate(postEntity);
//                post.setPkTopic(pkEntity.getTopic());
                posts.add(post);
            }

        }

        return posts;




    }

    private Map<String,PkEntity> queryPostPks(List<PostEntity> postEntities) {

        Map<String,PkEntity> pkMap = new HashMap<>();
        List<Object> pks = new ArrayList<>();
        postEntities.forEach(post->{
            pks.add(post.getPkId());
        });
        if(CollectionUtils.isEmpty(pks)){return pkMap;}
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .inFilter("pkId",pks);
        List<PkEntity> entities = daoService.queryEntities(PkEntity.class,filter);
        entities.forEach(pk->{
            pkMap.put(pk.getPkId(),pk);
        });
        return pkMap;

    }
    private List<PostEntity> queryUserPublishPosts(String userId, int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("time",OrderTag.DESC);

        List<PostEntity> entities = daoService.queryEntities(PostEntity.class,filter);

        return entities;

    }
    private List<PostEntity> queryUserPosts(String userId, int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("time",OrderTag.DESC);

        List<PostEntity> entities = daoService.queryEntities(PostEntity.class,filter);

        return entities;

    }

    public PkUnlockEntity 查询用户解锁(String pkId, String userId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkUnlockEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("pkId",CompareTag.Equal,pkId);

        PkUnlockEntity unlockEntity = daoService.querySingleEntity(PkUnlockEntity.class,filter);
        return unlockEntity;
    }

    public void 添加解锁Pk(String pkId, String userId) {

        PkUnlockEntity unlockEntity = new PkUnlockEntity();
        unlockEntity.setPkId(pkId);
        unlockEntity.setUserId(userId);
        unlockEntity.setCreateTime(System.currentTimeMillis());

        daoService.insertEntity(unlockEntity);

    }

    public PkLocationEntity 查询PK位置(String pkId) {


        //进缓存

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkLocationEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);

        PkLocationEntity pkLocationEntity = daoService.querySingleEntity(PkLocationEntity.class,filter);

        return pkLocationEntity;
    }

    public void 设置PK位置( String pkId, String name, String desc, String city, String cityCode, String latitude, String longitude) throws AppException, UnsupportedEncodingException {
        PkLocationEntity locationEntity = 查询PK位置(pkId);
        if(ObjectUtils.isEmpty(locationEntity))
        {
            locationEntity = new PkLocationEntity();

        }
        locationEntity.setPkId(pkId);
        locationEntity.setName(name);
        locationEntity.setDescription(desc);
        locationEntity.setCity(city);
        locationEntity.setCityCode(cityCode);
        locationEntity.setLatitude(Float.valueOf(Float.valueOf(latitude)*1000000.0F).longValue());
        locationEntity.setLongitude(Float.valueOf(Float.valueOf(longitude)*1000000.0F).longValue());
        if(ObjectUtils.isEmpty(locationEntity))
        {
            daoService.insertEntity(locationEntity);

        }
        else
        {
            daoService.updateEntity(locationEntity);
        }

    }

    public boolean 查询收藏状态(String pkId, String userId) {

        if(StringUtils.isBlank(userId) || StringUtils.equalsIgnoreCase("undefined",userId)|| StringUtils.equalsIgnoreCase("Nan",userId)|| StringUtils.equalsIgnoreCase("null",userId)){
            return false;
        }
        InvitePkEntity invitePkEntity = queryInvitePk(pkId,userId);
        return !ObjectUtils.isEmpty(invitePkEntity);


    }

    public List<PkDetail> 查询用户已发布主题(String userId, int page) throws IOException {

        Date current = new Date();
        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  pkEntities = queryUserPublishPks(userId,page);
        for(PkEntity pkEntity:pkEntities)
        {
            PkDetail pkDetail = pkService.querySinglePk(pkEntity);
//            String topUserId = pkEntity.getTopPostUserId();
//            pkDetail.setImgs(postService.查询PK展示图片(pkEntity.getPkId(),StringUtils.isEmpty(topUserId)?pkEntity.getUserId():topUserId));
            pkDetails.add(pkDetail);
        }




        if(!userService.是否是遗传用户(userId)  && !AppConfigService.getConfigAsBoolean(ConfigItem.普通用户主题是否显示分享按钮和群组按钮))
        {
            pkDetails.forEach(pk ->{
                PkButton pkButton = appService.显示按钮(PkButtonType.时间);
                pkButton.setName(pk.getTime());
//                pk.setGroupInfo(pkButton);
            });
        }
        return pkDetails;



    }


    public void 设置标签(String pkId, List<String> tips) {

        EntityFilterChain filter =  EntityFilterChain.newFilterChain(PkTipEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        List<PkTipEntity> entities = daoService.queryEntities(PkTipEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities))
        {
            entities.forEach(entity->{
                daoService.deleteEntity(entity);
            });
        }


        for(String tipId:tips)
        {
            EntityFilterChain filter1 =  EntityFilterChain.newFilterChain(PkTipEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("tipId",CompareTag.Equal,tipId);
            PkTipEntity activeTipEntity = daoService.querySingleEntity(PkTipEntity.class,filter1);
            if(ObjectUtils.isEmpty(activeTipEntity))
            {
                activeTipEntity = new PkTipEntity();
                activeTipEntity.setPkId(pkId);
                activeTipEntity.setTipId(clearTip(tipId));
                activeTipEntity.setTime(System.currentTimeMillis());
                daoService.insertEntity(activeTipEntity);
            }

        }

    }
    private String clearTip(String tip) {
        String reg1 = "\\[";
        String reg2 = "]";
        String reg3 = "\"";
//        String reg4 = AppConfigService.getConfigAsString(常量值.OSS基础地址,"https://oss.211shopper.com");
        tip = tip.replaceAll(reg3,"").replaceAll(reg1,"").replaceAll(reg2,"").trim();
        return tip;
    }
    private String 查询标签Style() {

        return "";

    }

    public List<ActiveTip> 查询所有标签信息() {
        EntityFilterChain filter =  EntityFilterChain.newFilterChain(ActiveTipEntity.class);
        List<ActiveTipEntity> activeTipEntities = daoService.queryEntities(ActiveTipEntity.class,filter);


        List<ActiveTip> activeTips = new ArrayList<>();
        activeTipEntities.forEach(tip ->{
            ActiveTip activeTip = new ActiveTip();
            activeTip.setId(tip.getId());
            activeTip.setStyle(查询标签Style());
            activeTip.setTip(tip.getTip());
            activeTips.add(activeTip);
        });

        return activeTips;
    }
    public void 添加Tip(String tip) throws UnsupportedEncodingException {
        ActiveTipEntity activeTipEntity = new ActiveTipEntity();
        activeTipEntity.setId(com.union.app.util.idGenerator.IdGenerator.getActiveTipId());
        activeTipEntity.setTip(tip);
        daoService.insertEntity(activeTipEntity);

    }
    private List<ActiveTip> 查询Tip(List<Object> tips){
        List<ActiveTip> activeTips = new ArrayList<>();
        if(CollectionUtils.isEmpty(tips)){return activeTips;}
        EntityFilterChain filter =  EntityFilterChain.newFilterChain(ActiveTipEntity.class)
                .inFilter("id",tips);
        List<ActiveTipEntity> activeTipEntities = daoService.queryEntities(ActiveTipEntity.class,filter);

        if(!CollectionUtils.isEmpty(activeTipEntities))
        {
            activeTipEntities.forEach(activeTipEntity -> {
                ActiveTip activeTip = new ActiveTip();
                activeTip.setId(activeTipEntity.getId());
                activeTip.setStyle(查询标签Style());
                activeTip.setTip(activeTipEntity.getTip());
                activeTips.add(activeTip);
            });
        }
        return activeTips;


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
}
