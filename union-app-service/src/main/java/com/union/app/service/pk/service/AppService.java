package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.捞人.ScaleRange;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.kadian.label.ActiveTipEntity;
import com.union.app.entity.pk.kadian.label.RangeEntity;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
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
import com.union.app.service.pk.service.pkuser.PkDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
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
    PkDynamicService pkDynamicService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    KeyService keyService;

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



    public List<PkDetail> 查询用户邀请(String userId, int page,double latitude,double longitude) throws IOException {


        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  invites = queryUserInvitePks(userId,page);
        for(PkEntity pkEntity:invites)
        {
            PkDetail pkDetail = locationService.querySinglePkWidthList(pkEntity);
            int length = locationService.计算坐标间距离(latitude,longitude,pkEntity.getLatitude(),pkEntity.getLongitude());
            pkDetail.setUserLength(length);
            pkDetail.setUserLengthStr(locationService.距离转换成描述(length));
            pkDetails.add(pkDetail);
        }
        批量查询Pk动态表和顶置(pkDetails);
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


        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkEntity>  pkEntities = queryUserPks(userId,page);

        for(PkEntity pkEntity:pkEntities)
        {
            PkDetail pkDetail = locationService.querySinglePkWidthList(pkEntity);
            int length = locationService.计算坐标间距离(latitude,longitude,pkEntity.getLatitude(),pkEntity.getLongitude());
            pkDetail.setUserLength(length);
            pkDetail.setUserLengthStr(locationService.距离转换成描述(length));
            System.out.println(pkEntity.getName() + " : " + locationService.距离转换成描述(length));
            pkDetails.add(pkDetail);
        }
        批量查询Pk动态表和顶置(pkDetails);

        return pkDetails;

    }

    public void 批量查询Pk动态表和顶置(List<PkDetail> pkDetails) {

        List<Object> pkIds = collectIds(pkDetails);
        if(!CollectionUtils.isEmpty(pkIds)){pkDynamicService.批量查询动态表(pkIds,pkDetails);}
        postService.批量查询POST(pkDetails);
        locationService.批量查询Pk顶置内容图片(pkDetails);

    }

    private List<Object> collectNoTopPostIds(List<PkDetail> pkDetails) {
        List<Object> ids = new ArrayList<>();
        pkDetails.forEach(pkDetail -> {
            if(StringUtils.isBlank(pkDetail.getTopPostId())){
                ids.add(pkDetail.getPkId());
            }
        });
        return ids;
    }

    private List<Object> collectTopPostIds(List<PkDetail> pkDetails) {
        List<Object> ids = new ArrayList<>();
        pkDetails.forEach(pkDetail -> {
            if(!StringUtils.isBlank(pkDetail.getTopPostId())){
                ids.add(pkDetail.getTopPostId());
            }
        });
        return ids;
    }


    private List<Object> collectIds(List<PkDetail> pkDetails) {
        List<Object> ids = new ArrayList<>();
        pkDetails.forEach(pkDetail -> {
            ids.add(pkDetail.getPkId());
        });
        return ids;

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
        AppConfigService.refreshConfig(configEntity.getConfigName());
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
        keyService.刷新图片缓存(type);
        return backImgEntity;


    }




    private static final Map<Integer,List<BackImgEntity>> imgs = new HashMap<>();


    public String 查询背景(int type) {

        BackImgEntity backImgEntity = keyService.查询图片缓存(type);

        return ObjectUtils.isEmpty(backImgEntity)?"":backImgEntity.getImgUrl();

    }










    public void 删除内置背景图片(String id) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(BackImgEntity.class)
                .compareFilter("imgId",CompareTag.Equal,id);

        BackImgEntity backImgEntity = daoService.querySingleEntity(BackImgEntity.class,filter);
        daoService.deleteEntity(backImgEntity);
        keyService.刷新图片缓存(backImgEntity.getType());

    }
    private volatile boolean userType = true;
    public User 新增内置用户(String name, String imgUrl) throws UnsupportedEncodingException {

        String userId = com.union.app.util.idGenerator.IdGenerator.生成用户ID();
        UserEntity userEntity = new UserEntity();
        userEntity.setOpenId(userId);
        userEntity.setUserId(userId);
        userEntity.setAvatarUrl(imgUrl);
        userEntity.setNickName(name);
        userEntity.setUserType(UserType.重点用户);

        userDynamicService.创建Dynamic表(userEntity.getUserId());
        userService.创建UserCardEntity(userEntity.getUserId());
        daoService.insertEntity(userEntity);

        return userService.queryUser(userId);

    }

    public List<User> 查询内置用户(int page) throws UnsupportedEncodingException {
        List<User> users = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                .compareFilter("userType",CompareTag.Equal,UserType.重点用户)
                .pageLimitFilter(page,50)
                .orderByRandomFilter();
        List<UserEntity> pkEntities = daoService.queryEntities(UserEntity.class,filter);
        for(UserEntity userEntity:pkEntities)
        {
            User user = new User();
            user.setUserName(new String(userEntity.getNickName()));
            user.setUserId(userEntity.getUserId());
            user.setUserType(ObjectUtils.isEmpty(userEntity.getUserType())?UserType.普通用户.getType():userEntity.getUserType().getType());
            user.setImgUrl(userEntity.getAvatarUrl());

            users.add(user);
        }
        return users;




    }

    public User 修改内置用户名称(String userId, String name) throws UnsupportedEncodingException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                .nullFilter("appName",false);
        List<UserEntity> results = daoService.queryEntities(UserEntity.class,filter);
        results.forEach(result->{
            result.setAppName(null);
            daoService.updateEntity(result);
        });

        UserEntity userEntity = userService.queryUserEntity(userId);
        userEntity.setNickName(name);
        userEntity.setAppName("CHOSSEN");
        daoService.updateEntity(userEntity);

        return userService.queryUser(userId);

    }

    public User 修改内置用户头像(String userId, String imgUrl) {

        UserEntity userEntity = userService.queryUserEntity(userId);
        userEntity.setAvatarUrl(imgUrl);
        daoService.updateEntity(userEntity);
        return userService.queryUser(userId);

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
    public List<Post> 查询用户发布图册(String userId, int page)  {


        List<Post> posts = new ArrayList<>();
        List<PostEntity>  postEntities = queryUserPublishPosts(userId,page);
        if(CollectionUtils.isEmpty(postEntities)){return posts;}
        for(PostEntity postEntity:postEntities)
        {
            Post post = postService.translate(postEntity);
            posts.add(post);
        }

        return posts;




    }


//    public List<Post> 查询用户图册(String userId, int page)  {
//
//
//        List<Post> posts = new ArrayList<>();
//        List<PostEntity>  postEntities = queryUserPosts(userId,page);
//        if(CollectionUtils.isEmpty(postEntities)){return posts;}
//        Map<String,PkEntity> allPks = queryPostPks(postEntities);
//        for(PostEntity postEntity:postEntities)
//        {
//            PkEntity pkEntity = allPks.get(postEntity.getPkId());
//            if(!ObjectUtils.isEmpty(pkEntity))
//            {
//                Post post = postService.translate(postEntity);
////                post.setPkTopic(pkEntity.getTopic());
//                posts.add(post);
//            }
//
//        }
//
//        return posts;
//
//
//
//
//    }

//    private Map<String,PkEntity> queryPostPks(List<PostEntity> postEntities) {
//
//        Map<String,PkEntity> pkMap = new HashMap<>();
//        List<Object> pks = new ArrayList<>();
//        postEntities.forEach(post->{
//            pks.add(post.getPkId());
//        });
//        if(CollectionUtils.isEmpty(pks)){return pkMap;}
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
//                .inFilter("pkId",pks);
//        List<PkEntity> entities = daoService.queryEntities(PkEntity.class,filter);
//        entities.forEach(pk->{
//            pkMap.put(pk.getPkId(),pk);
//        });
//        return pkMap;
//
//    }
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


    public boolean 查询收藏状态(String pkId, String userId) {

        if(StringUtils.isBlank(userId) || StringUtils.equalsIgnoreCase("undefined",userId)|| StringUtils.equalsIgnoreCase("Nan",userId)|| StringUtils.equalsIgnoreCase("null",userId)){
            return false;
        }
        InvitePkEntity invitePkEntity = queryInvitePk(pkId,userId);
        return !ObjectUtils.isEmpty(invitePkEntity);


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
    public List<RangeEntity> 查询全部范围偏移表() {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(RangeEntity.class);
        List<RangeEntity> rangeEntities = daoService.queryEntities(RangeEntity.class,cfilter);
        return rangeEntities;
    }
    public List<ScaleRange> 查询全部ScaleRange() {
        List<ScaleRange> scaleRanges = new ArrayList<>();

        List<RangeEntity> rangeEntities = 查询全部范围偏移表();



        Map<Integer,RangeEntity> rangeEntityHashMap = new HashMap<>();

        rangeEntities.forEach(rangeEntity -> {
            rangeEntityHashMap.put(rangeEntity.getPkRange(),rangeEntity);
        });


        for(int i=0;i<200;i++)
        {
            ScaleRange scaleRange = new ScaleRange();
            scaleRange.setRange((i+1)*10);
            scaleRange.setScale(ObjectUtils.isEmpty(rangeEntityHashMap.get(scaleRange.getRange()))?16:rangeEntityHashMap.get(scaleRange.getRange()).getScale());
            scaleRange.setOffset(ObjectUtils.isEmpty(rangeEntityHashMap.get(scaleRange.getRange()))?0.0F:rangeEntityHashMap.get(scaleRange.getRange()).getOffset());
            scaleRanges.add(scaleRange);
        }
        return scaleRanges;
    }
    public RangeEntity 查询指定范围缩放偏移(int range) {
        RangeEntity rangeEntity = keyService.查询缩放偏移缓存(range);
        if(ObjectUtils.isEmpty(rangeEntity)){
            EntityFilterChain cfilter = EntityFilterChain.newFilterChain(RangeEntity.class)
                    .compareFilter("pkRange",CompareTag.Equal,range);
            rangeEntity = daoService.querySingleEntity(RangeEntity.class,cfilter);
            if(ObjectUtils.isEmpty(rangeEntity))
            {
                rangeEntity = new RangeEntity();
                rangeEntity.setOffset(0);
                rangeEntity.setScale(16);
                rangeEntity.setPkRange(range);

            }
            keyService.保存缩放偏移缓存(rangeEntity);
        }
        return rangeEntity;
    }



    public RangeEntity 查询指定范围偏移表(int range) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(RangeEntity.class)
                .compareFilter("pkRange",CompareTag.Equal,range);
        RangeEntity rangeEntity = daoService.querySingleEntity(RangeEntity.class,cfilter);
        return rangeEntity;
    }
    public void 设置缩放(int range, int scale,double offset) {
        RangeEntity rangeEntity = 查询指定范围偏移表(range);
        if(ObjectUtils.isEmpty(rangeEntity))
        {
            rangeEntity = new RangeEntity();
            rangeEntity.setScale(scale);
            rangeEntity.setPkRange(range);
            rangeEntity.setOffset(offset);
            daoService.insertEntity(rangeEntity);
        }
        else
        {
            rangeEntity.setScale(scale);
            rangeEntity.setOffset(offset);
            daoService.updateEntity(rangeEntity);
        }
        keyService.清除缩放缓存(range);

    }
}
