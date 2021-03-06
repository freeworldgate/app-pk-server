package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.PkDynamic;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.domain.pk.daka.CreateLocation;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.kadian.UserFollowEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.pkuser.PkDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.NumberUtils;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class LocationService {




    @Autowired
    AppDaoService daoService;

    @Autowired
    AppService appService;

    @Autowired
    PkService pkService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    PkDynamicService pkDynamicService;

    @Autowired
    KeyService keyService;

    @Autowired
    LocationService locationService;

    public String 坐标转换成UUID(double latitude,double longitude,String name)
    {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.valueOf(getRealVaule(latitude,2)));
        stringBuffer.append(String.valueOf(getRealVaule(longitude,2)));
        stringBuffer.append(name.replace("[","").replace("]",""));
        String locationId = UUID.nameUUIDFromBytes(stringBuffer.toString().getBytes()).toString();
        return locationId;
    }

     public static double getRealVaule(double value,int resLen) {
                double v1 = value * 10 ;
                int v2 = (int)v1;
                double v3 = v2/(10.0D);

                return v3;
      }




    //单位米
    public static int 计算坐标间距离(Double longitude1, Double latitude1, Double longitude2, Double latitude2){

        Double EARTH_RADIUS = 6370.996; // 地球半径系数
        Double PI = 3.1415926;

        Double radLat1 = latitude1 * PI / 180.0;
        Double radLat2 = latitude2 * PI / 180.0;

        Double radLng1 = longitude1 * PI / 180.0;
        Double radLng2 = longitude2 * PI /180.0;

        Double a =  radLat1 -  radLat2;
        Double b =  radLng1 -  radLng2;

        Double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2)));
        distance = distance * EARTH_RADIUS * 1000;

        BigDecimal bg = new BigDecimal(distance);
        double d3 = bg.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        return  (int) Math.ceil(d3);
    }


    public String 创建卡点(CreateLocation createLocation) throws IOException, AppException {


        if(StringUtils.isBlank(createLocation.getName())||StringUtils.isBlank(createLocation.getBackUrl()) ||StringUtils.isBlank(createLocation.getType()) || StringUtils.isBlank(createLocation.getSign())){
            throw AppException.buildException(PageAction.信息反馈框("创建卡点错误...","参数错误"));
        }

        String pkId = this.坐标转换成UUID(createLocation.getLatitude(),createLocation.getLongitude(),createLocation.getName());
        String scene = IdGenerator.getScene();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setScene(scene);
        String codeUrl = WeChatUtil.生成二维码(scene);
        if(StringUtils.isBlank(codeUrl)){
            throw AppException.buildException(PageAction.信息反馈框("创建卡点错误...","创建卡点错误"));
        }
        pkEntity.setCodeUrl(codeUrl);
        pkEntity.setUserId(createLocation.getUserId());
        pkEntity.setLatitude(createLocation.getLatitude());
        pkEntity.setLongitude(createLocation.getLongitude());
        pkEntity.setCityCode(appService.查询城市码(createLocation.getCity()));


        pkEntity.setType(createLocation.getType());
        pkEntity.setTypeRange(createLocation.getRadius());


        pkEntity.setName(createLocation.getName());
        pkEntity.setAddress(createLocation.getAddress());
        pkEntity.setSign(createLocation.getSign());
        pkEntity.setBackUrl(createLocation.getBackUrl());
        pkEntity.setTotalImgs(0);
        pkEntity.setTotalUsers(0);
        pkEntity.setTime(System.currentTimeMillis());
        pkEntity.setRangeSet(Boolean.FALSE);
        pkEntity.setCitySet(Boolean.FALSE);
        pkEntity.setCountrySet(Boolean.FALSE);
        pkEntity.setPkLock(Boolean.FALSE);
        pkEntity.setFindSet(AppConfigService.getConfigAsBoolean(ConfigItem.打捞开关));



        daoService.insertEntity(pkEntity);
        pkDynamicService.创建DynamicEntity(pkId);
        userDynamicService.用户卡点加一(createLocation.getUserId());
        keyService.城市PK数量加一(pkEntity.getCityCode());
        keyService.通知同步队列(String.valueOf(pkEntity.getCityCode()), TimeSyncType.CITY.getScene());
        return pkId;
    }



    public PkDetail 搜索卡点(String pkId) throws IOException {


        return this.querySinglePkWithoutCache(pkId);
    }



    public PkDetail querySinglePk(String pkId) throws IOException {
        PkEntity pk = this.querySinglePkEntity(pkId);
        return this.querySinglePk(pk);
    }
    public PkDetail querySinglePkWithoutCache(String pkId) throws IOException {
        PkEntity pk = this.querySinglePkEntityWithoutCache(pkId);
        return this.querySinglePk(pk);
    }
    public PkEntity querySinglePkEntity(String pkId)
    {


            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId);
            PkEntity pkEntity = daoService.querySingleEntity(PkEntity.class,filter);



        return pkEntity;
    }
    public PkEntity querySinglePkEntityWithoutCache(String pkId)
    {

            EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId);
            PkEntity pkEntity = daoService.querySingleEntity(PkEntity.class,filter);


        return pkEntity;
    }

    public boolean isPkCreator(String pkId, String userId) {
        PkEntity pk = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pk)){return false;}
        return org.apache.commons.lang.StringUtils.equals(userId,pk.getUserId())? Boolean.TRUE:Boolean.FALSE;
    }
    public PkDetail querySinglePk(PkEntity pk) throws IOException {
        if(ObjectUtils.isEmpty(pk)){return null;}
        String pkId = pk.getPkId();
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setCity(appService.getCity(pk.getCityCode()));
        pkDetail.setCodeUrl(pk.getCodeUrl());
        pkDetail.setSign(pk.getSign());
        pkDetail.setLatitude(pk.getLatitude());
        pkDetail.setLongitude(pk.getLongitude());
        pkDetail.setName(pk.getName());
        pkDetail.setAddress(pk.getAddress());
        LocationType locationType = this.查询类型(pk);
        pkDetail.setType(locationType);
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setTime(TimeUtils.convertTime(pk.getTime()));
        pkDetail.setBackUrl(pk.getBackUrl());
        PkDynamic pkDynamic = pkDynamicService.queryPkDynamic(pkId);
        pkDetail.setPkDynamic(pkDynamic);
        pkDetail.setTopPostId(pk.getTopPostId());
        pkDetail.setTopPostType(pk.getTopPostType());
        pkDetail.setTopPostSetTime(pk.getTopPostSetTime());
        pkDetail.setTopPostTimeLengthStr(TimeUtils.顶置周期(pk.getTopPostTimeLength()));
        pkDetail.setTopPostTimeLength(pk.getTopPostTimeLength());
        Post topPost = postService.查询顶置帖子(pk);
        if(!ObjectUtils.isEmpty(topPost))
        {
            pkDetail.setTopPost(postService.查询顶置帖子(pk));
        }
        else
        {
            topPost = postService.查询最新的图片列表(pk);
            pkDetail.setTopPost(ObjectUtils.isEmpty(topPost)?null:topPost);
        }
        pkDetail.setMarker(this.查询Marker(pk));
        pkDetail.setCircle(this.查询Circle(pk,locationType));
        pkDetail.setTotalUsers(NumberUtils.convert(pk.getTotalUsers()));
        pkDetail.setTotalUserNumber(pk.getTotalUsers());
        pkDetail.setCitySet(pk.isCitySet());
        pkDetail.setCountrySet(pk.isCountrySet());
        pkDetail.setFindSet(pk.isFindSet());
        return pkDetail;
    }

    public PkDetail querySinglePkWidthList(PkEntity pk) {
        if(ObjectUtils.isEmpty(pk)){return null;}
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setCity(appService.getCity(pk.getCityCode()));
        pkDetail.setCodeUrl(pk.getCodeUrl());
        pkDetail.setSign(pk.getSign());
        pkDetail.setLatitude(pk.getLatitude());
        pkDetail.setLongitude(pk.getLongitude());
        pkDetail.setName(pk.getName());
        pkDetail.setAddress(pk.getAddress());
        LocationType locationType = this.查询类型(pk);
        pkDetail.setType(locationType);
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setTime(TimeUtils.convertTime(pk.getTime()));
        pkDetail.setBackUrl(pk.getBackUrl());
        pkDetail.setTopPostId(pk.getTopPostId());
        pkDetail.setTopPostType(pk.getTopPostType());
        pkDetail.setTopPostSetTime(pk.getTopPostSetTime());
        pkDetail.setTopPostTimeLengthStr(TimeUtils.顶置周期(pk.getTopPostTimeLength()));
        pkDetail.setTopPostTimeLength(pk.getTopPostTimeLength());
        pkDetail.setMarker(this.查询Marker(pk));
        pkDetail.setCircle(this.查询Circle(pk,locationType));
        pkDetail.setTotalUsers(NumberUtils.convert(pk.getTotalUsers()));
        pkDetail.setTotalUserNumber(pk.getTotalUsers());
        pkDetail.setSet(pk.isRangeSet());
        pkDetail.setCountrySet(pk.isCountrySet());
        pkDetail.setCitySet(pk.isCitySet());
        pkDetail.setFindSet(pk.isFindSet());
        pkDetail.setRangeLock(pk.isRangeLock());
        pkDetail.setLock(pk.isPkLock());
        return pkDetail;
    }



    private Circle 查询Circle(PkEntity pk, LocationType locationType) {
        Circle circle = new Circle();
        circle.setLatitude(pk.getLatitude());
        circle.setLongitude(pk.getLongitude());
        circle.setRadius(locationType.getRangeLength());
        return circle;

    }

    private Marker 查询Marker(PkEntity pk) {
        return buildMarker(pk.getName(),pk.getLatitude(),pk.getLongitude());
    }
    public Marker buildMarker(String name,Double latitude, Double longitude)
    {
        Marker marker = new Marker();
        marker.setId(RandomUtil.getRandomNumber());
        marker.setLatitude(latitude);
        marker.setLongitude(longitude);
        Callout callout = new Callout();
        callout.setContent(name);
        marker.setCallout(callout);
        return marker;
    }

    private LocationType 查询类型(PkEntity pk) {

        LocationType locationType = new LocationType();
        locationType.setTypeName(pk.getType());
        locationType.setScale(appService.查询指定范围缩放偏移(pk.getTypeRange()).getScale());
        locationType.setRange(this.长度转义(pk.getTypeRange()));
        locationType.setRangeValue(this.长度转义不带单位(pk.getTypeRange()));
        locationType.setRangeLength(pk.getTypeRange());
        return locationType;

    }

    private String 长度转义不带单位(int typeRange) {
        if(typeRange > 1000)
        {
            double rangeLength = typeRange/1000.0D;
            BigDecimal bg = new BigDecimal(rangeLength);
            double d3 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            return  String.valueOf(d3);

        }
        else
        {
            return  String.valueOf(typeRange);
        }



    }


    private String 长度转义(int typeRange) {
        if(typeRange >= 1000)
        {
            double rangeLength = typeRange/1000.0D;
            BigDecimal bg = new BigDecimal(rangeLength);
            double d3 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            return  String.valueOf(d3)+"公里";

        }
        else
        {
            return typeRange+"米";
        }



    }

    private LocationType 查询默认卡点类型(String type) {
        LocationType locationType = new LocationType();
        locationType.setRangeLength(100);
        locationType.setScale(16);
        locationType.setTypeName(type);
        return locationType;
    }


    public List<PkDetail> 查询附近卡点(String queryerId,double latitude, double longitude) {
        List<PkDetail> pks = new ArrayList<>();

        EntityFilterChain aroundFilter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("latitude",CompareTag.Small,latitude + 0.08D)
                .andFilter()
                .compareFilter("latitude",CompareTag.Bigger,latitude - 0.08D)
                .andFilter()
                .compareFilter("longitude",CompareTag.Small,longitude + 0.08D)
                .andFilter()
                .compareFilter("longitude",CompareTag.Bigger,longitude - 0.08D)
                .andFilter()
                .compareFilter("totalImgs",CompareTag.Bigger,0L)
                .andFilter()
                .compareFilter("citySet",CompareTag.Equal,Boolean.TRUE)
                .orderByRandomFilter()
                .pageLimitFilter(1,10);

        EntityFilterChain cityAroundFilter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("latitude",CompareTag.Small,latitude + 0.3D)
                .andFilter()
                .compareFilter("latitude",CompareTag.Bigger,latitude - 0.3D)
                .andFilter()
                .compareFilter("longitude",CompareTag.Small,longitude + 0.3D)
                .andFilter()
                .compareFilter("longitude",CompareTag.Bigger,longitude - 0.3D)
                .andFilter()
                .compareFilter("totalImgs",CompareTag.Bigger,0L)
                .andFilter()
                .compareFilter("citySet",CompareTag.Equal,Boolean.TRUE)
                .orderByRandomFilter()
                .pageLimitFilter(1,10);



//        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,aroundFilter);
//        if(CollectionUtils.isEmpty(pkEntities))
//        {
//            pkEntities = daoService.queryEntities(PkEntity.class,cityAroundFilter);
//        }
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,cityAroundFilter);
        if(pkEntities.size() < 5)
        {
            pkEntities.addAll(keyService.查询全网排行());
        }



        if(!org.apache.commons.collections4.CollectionUtils.isEmpty(pkEntities))
        {
            //去重:
            List<String> pkIds = new ArrayList<>();
            List<PkEntity> rpkEntities = new ArrayList<>();
            for(PkEntity pkEntity:pkEntities)
            {
                if(!pkIds.contains(pkEntity.getPkId()))
                {
                    rpkEntities.add(pkEntity);
                    pkIds.add(pkEntity.getPkId());
                }
            }

            rpkEntities.forEach(pk->{
                    PkDetail pkDetail = this.querySinglePkWidthList(pk);
                    int length = this.计算坐标间距离(latitude,longitude,pk.getLatitude(),pk.getLongitude());
                    pkDetail.setUserLength(length);
                    pkDetail.setUserLengthStr(this.距离转换成描述(length));
                    pkDetail.setLatitude(pkDetail.getLatitude() -  appService.查询指定范围缩放偏移(pk.getTypeRange()).getOffset() );
                    pks.add(pkDetail);
            });
        }
        appService.批量查询Pk动态表和顶置(pks);

        return pks;

    }

    public String 距离转换成描述(int length) {

        return 长度转义(length);
    }

    public PostEntity 查询最新用户Post发布时间(String pkId, String userId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId)
                .orderByFilter("time", OrderTag.DESC);
        PostEntity postEntity = daoService.querySingleEntity(PostEntity.class,cfilter);
        return postEntity;


    }




    public PkImage 查询用户卡点图片(String pkId, String userId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkImageEntity pkImageEntity = daoService.querySingleEntity(PkImageEntity.class,cfilter);
        if(!ObjectUtils.isEmpty(pkImageEntity))
        {
            PkImage image = new PkImage();
            image.setImageId(pkImageEntity.getImgId());
            image.setImgUrl(pkImageEntity.getImgUrl());
            image.setPkId(pkImageEntity.getPkId());
            image.setStatu(new KeyValuePair(pkImageEntity.getImgStatu().getKey(),pkImageEntity.getImgStatu().getValue()));
            image.setUser(userService.queryUser(pkImageEntity.getUserId()));
            image.setTime(TimeUtils.convertTime(System.currentTimeMillis()));
            return image;
        }
        return null;
    }
    public List<PkImage> 查询待审核卡点图片(String pkId, int page) {
        List<PkImage> images = new ArrayList<>();

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("imgStatu",CompareTag.Equal,ImgStatu.审核中)
                .pageLimitFilter(page,20)
                .orderByFilter("time",OrderTag.DESC);
        List<PkImageEntity> pkImageEntities = daoService.queryEntities(PkImageEntity.class,filter);

        pkImageEntities.forEach(pkImageEntity->{
            PkImage image = new PkImage();
            image.setImageId(pkImageEntity.getImgId());
            image.setImgUrl(pkImageEntity.getImgUrl());
            image.setPkId(pkImageEntity.getPkId());
            image.setStatu(new KeyValuePair(pkImageEntity.getImgStatu().getKey(),pkImageEntity.getImgStatu().getValue()));
            image.setUser(userService.queryUser(pkImageEntity.getUserId()));
            image.setTime(TimeUtils.convertTime(System.currentTimeMillis()));
            images.add(image);
        });
        return images;

    }

    public List<PkImage> 查询卡点图片(String pkId, int page) {
        List<PkImage> images = new ArrayList<>();

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("imgStatu",CompareTag.Equal,ImgStatu.审核通过)
                .pageLimitFilter(page,20)
                .orderByFilter("time",OrderTag.DESC);
        List<PkImageEntity> pkImageEntities = daoService.queryEntities(PkImageEntity.class,filter);

        pkImageEntities.forEach(pkImageEntity->{
            PkImage image = new PkImage();
            image.setImageId(pkImageEntity.getImgId());
            image.setImgUrl(pkImageEntity.getImgUrl());
            image.setPkId(pkImageEntity.getPkId());
            image.setStatu(new KeyValuePair(pkImageEntity.getImgStatu().getKey(),pkImageEntity.getImgStatu().getValue()));
            image.setUser(userService.queryUser(pkImageEntity.getUserId()));
            image.setTime(TimeUtils.convertTime(System.currentTimeMillis()));
            images.add(image);
        });
        return images;

    }

    public void 上传卡点图片(String pkId, String userId, String imgUrl) {

        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkImageEntity pkImageEntity = daoService.querySingleEntity(PkImageEntity.class,cfilter);
        if(ObjectUtils.isEmpty(pkImageEntity))
        {
            pkImageEntity = new PkImageEntity();
            pkImageEntity.setImgId(IdGenerator.getImageId());
            if(locationService.isPkCreator(pkId,userId)){pkImageEntity.setImgStatu(ImgStatu.审核通过);}else{pkImageEntity.setImgStatu(ImgStatu.审核中);}

            pkImageEntity.setImgUrl(imgUrl);
            pkImageEntity.setPkId(pkId);
            pkImageEntity.setUserId(userId);
            pkImageEntity.setTime(System.currentTimeMillis());
            daoService.insertEntity(pkImageEntity);
        }
        else
        {
            if(pkService.isPkCreator(pkId,userId)){pkImageEntity.setImgStatu(ImgStatu.审核通过);}else{pkImageEntity.setImgStatu(ImgStatu.审核中);}
            pkImageEntity.setImgUrl(imgUrl);
            pkImageEntity.setTime(System.currentTimeMillis());
            daoService.updateEntity(pkImageEntity);
        }


    }

    public List<User> 查询关注列表(String userId, int page) {

        List<User> users = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserFollowEntity.class)
                .compareFilter("followerId",CompareTag.Equal,userId)
                .pageLimitFilter(page,20)
                .orderByFilter("time",OrderTag.DESC);
        List<UserFollowEntity> userFollowEntities = daoService.queryEntities(UserFollowEntity.class,filter);

        userFollowEntities.forEach(follower->{

            User user = userService.queryUser(follower.getUserId());
            user.setFollowTime(TimeUtils.convertTime(follower.getTime()));
            user.setFollowStatu(1);
            users.add(user);
        });
        return users;


    }

    public void 添加关注(String userId, String followerId) throws AppException {
        UserFollowEntity userFollowEntity = 查询关注(userId,followerId);
        if(ObjectUtils.isEmpty(userFollowEntity))
        {
            userFollowEntity = new UserFollowEntity();
            userFollowEntity.setUserId(followerId);
            userFollowEntity.setFollowerId(userId);
            userFollowEntity.setTime(System.currentTimeMillis());
            daoService.insertEntity(userFollowEntity);
        }
        else
        {
            throw AppException.buildException(PageAction.前端数据更新("followStatu",true));
        }


    }

    public void 取消关注(String userId, String followerId) throws AppException {

        UserFollowEntity userFollowEntity = 查询关注(userId,followerId);
        if(!ObjectUtils.isEmpty(userFollowEntity))
        {
           daoService.deleteEntity(userFollowEntity);
        }
        else
        {
            throw AppException.buildException(PageAction.前端数据更新("followStatu",false));
        }
    }
    public UserFollowEntity 查询关注(String userId, String followerId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserFollowEntity.class)
                .compareFilter("userId",CompareTag.Equal,followerId)
                .andFilter()
                .compareFilter("followerId",CompareTag.Equal,userId );
        UserFollowEntity userFollowEntity = daoService.querySingleEntity(UserFollowEntity.class,filter);
        return userFollowEntity;
    }

    public List<User> 查询粉丝列表(String userId, int page) {

        List<User> users = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserFollowEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,20)
                .orderByFilter("time",OrderTag.DESC);
        List<UserFollowEntity> userFollowEntities = daoService.queryEntities(UserFollowEntity.class,filter);

        userFollowEntities.forEach(follower->{

            User user = userService.queryUser(follower.getFollowerId());
            user.setFollowTime(TimeUtils.convertTime(follower.getTime()));
            users.add(user);
        });
        return users;

    }

    public List<Post> queryHiddenPkPost(String pkId, int page) {

        List<Post> posts = new LinkedList<>();
        List<PostEntity> pageList =  this.查询隐藏列表(pkId,page);
        for(PostEntity postEntity:pageList)
        {
            Post post = postService.translate(postEntity);

            if(!ObjectUtils.isEmpty(post)) {
                posts.add(post);
            }
        }
        return posts;

    }

    private List<PostEntity> 查询隐藏列表(String pkId, int page) {
        List<PostEntity> ids = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PostEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("statu",CompareTag.Equal,PostStatu.隐藏)
                .pageLimitFilter(page,20)
                .orderByFilter("time",OrderTag.DESC);

        List<PostEntity> entities = daoService.queryEntities(PostEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities)){ids.addAll(entities);}
        return ids;



    }

    public void 删除卡点图片(String pkId, String userId, String imageId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("imgId",CompareTag.Equal,imageId);
        PkImageEntity pkImageEntity = daoService.querySingleEntity(PkImageEntity.class,cfilter);

        if(StringUtils.equalsIgnoreCase(userId,pkImageEntity.getUserId()) || userService.管理员用户(userId)){
            daoService.deleteEntity(pkImageEntity);
        }
        else if(this.isPkCreator(pkId,userId) )
        {
            pkImageEntity.setImgStatu(ImgStatu.审核中);
            daoService.updateEntity(pkImageEntity);
        }else{}
    }

    public void 设置卡点背景图片(String pkId, String userId, String imageId) throws AppException, IOException {
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);

        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("imgId",CompareTag.Equal,imageId);
        PkImageEntity pkImageEntity = daoService.querySingleEntity(PkImageEntity.class,cfilter);
        if(!StringUtils.equals(pkEntity.getBackUrl(),pkImageEntity.getImgUrl()))
        {
            if((System.currentTimeMillis() - pkEntity.getUpdateTime())/1000 < AppConfigService.getConfigAsInteger(ConfigItem.修改PK背景图时间间隔))
            {
                throw AppException.buildException(PageAction.信息反馈框("",TimeUtils.计算时间(AppConfigService.getConfigAsInteger(ConfigItem.修改PK背景图时间间隔))+"仅能修改一次卡点背景图片，请稍后再试!"));
            }

            Map<String,Object> map = new HashMap<>();
            map.put("backUrl",pkImageEntity.getImgUrl());
            map.put("updateTime",System.currentTimeMillis());

            daoService.updateColumById(PkEntity.class,"pkId",pkId,map);
            List<String> imgs = new ArrayList<>();
            imgs.add(pkImageEntity.getImgUrl());
            postService.图片打卡(pkId,userService.获取系统消息用户(),"卡点更新背景图片...",imgs);
        }


    }

    public void 审核通过卡点图片(String pkId, String userId, String imageId) throws AppException {
        if(!locationService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.信息反馈框("用户无权限...","非法用户"));
        }

        Map<String,Object> map = new HashMap<>();
        map.put("imgStatu",ImgStatu.审核通过);
        daoService.updateColumById(PkImageEntity.class,"imgId",imageId,map);


    }

    public void 修改签名(String pkId, String sign) {

        Map<String,Object> map = new HashMap<>();
        map.put("sign",sign);
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);

    }

    public void 批量查询Pk顶置内容图片(List<PkDetail> pkDetails) {
        pkDetails.forEach(pkDetail -> {
            if((pkDetail.getTopPostType() != PostType.图片.getType() )|| StringUtils.isBlank(pkDetail.getTopPostId()) || ((System.currentTimeMillis() - pkDetail.getTopPostSetTime()) >= pkDetail.getTopPostTimeLength() * 60 * 1000L))
            {
                pkDetail.setTopPost(keyService.查询顶置图片集合(pkDetail.getPkId()));
            }
        });
    }


    public void 设置卡点范围(String pkId, int radius) {
        Map<String,Object> map = new HashMap<>();
        map.put("typeRange",radius);
        map.put("rangeSet",Boolean.TRUE);
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);
    }
    public void 设置用户卡点范围(String pkId, int radius) {
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        if(pkEntity.getTypeRange() == radius){return;}

        Map<String,Object> map = new HashMap<>();
        map.put("typeRange",radius);
        map.put("rangeSet",Boolean.TRUE);
        map.put("typeRangeSetTime",System.currentTimeMillis());
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);
    }

    public void updatePkRange(String pkId) throws AppException {
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        if(pkEntity.isRangeLock()){
            throw AppException.buildException(PageAction.信息反馈框("","卡点范围已锁定!"));
        }
        long lastTime = System.currentTimeMillis()-pkEntity.getTypeRangeSetTime();

        if(lastTime < AppConfigService.getConfigAsLong(ConfigItem.修改Pk打卡范围时间间隔) * (3600 * 1000L))
        {
            throw AppException.buildException(PageAction.信息反馈框("",AppConfigService.getConfigAsLong(ConfigItem.修改Pk打卡范围时间间隔)+"小时内仅能修改一次打卡范围!"));

        }


    }

    public boolean 锁定或者解锁Range(String pkId) {
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        Map<String,Object> map = new HashMap<>();
        map.put("rangeLock",!pkEntity.isRangeLock());
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);
        return !pkEntity.isRangeLock();
    }


    public void 卡点状态检查(String pkId) throws AppException {
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        if(pkEntity.isPkLock()){throw AppException.buildException(PageAction.信息反馈框("卡点已锁定","卡点已锁定!"));}
    }

    public void 卡点隐藏打卡信息检查(String pkId) throws AppException {

        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(pkEntity.getHiddenPosts() >= AppConfigService.getConfigAsInteger(ConfigItem.隐藏打卡最大数量))
        {
            throw AppException.buildException(PageAction.信息反馈框("卡点已锁定","隐藏打卡已超过最大值!"));
        }

    }


    public void 隐藏数量加1(String pkId) {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        Map<String,Object> map = new HashMap<>();
        map.put("hiddenPosts",pkEntity.getHiddenPosts()+1);
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);
    }

    public void 隐藏数量减1(String pkId) {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        Map<String,Object> map = new HashMap<>();
        map.put("hiddenPosts",pkEntity.getHiddenPosts()>0?pkEntity.getHiddenPosts()-1:0);
        daoService.updateColumById(PkEntity.class,"pkId",pkId,map);
    }
}
