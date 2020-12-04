package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.daka.CreateLocation;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.UserKvEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
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
import java.math.BigDecimal;
import java.util.*;

@Service
public class LocationService {




    @Autowired
    PayService payService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    LocationService appService;

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
    ComplainService complainService;


    public String 坐标转换成UUID(double latitude,double longitude,String name)
    {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.valueOf(getRealVaule(latitude,2)));
        stringBuffer.append(String.valueOf(getRealVaule(longitude,2)));
        stringBuffer.append(name);
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


    public String 创建卡点(CreateLocation createLocation) {


        String pkId = this.坐标转换成UUID(createLocation.getLatitude(),createLocation.getLongitude(),createLocation.getName());
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setUserId(createLocation.getUserId());
        pkEntity.setLatitude(createLocation.getLatitude());
        pkEntity.setLongitude(createLocation.getLongitude());
        LocationType locationType = this.查询默认卡点类型(createLocation.getType());

        pkEntity.setType(locationType.getTypeName());
        pkEntity.setTypeRange(locationType.getRangeLength());
        pkEntity.setTypeScale(locationType.getScale());

        pkEntity.setName(createLocation.getName());
        pkEntity.setAddress(createLocation.getAddress());
        pkEntity.setSign(createLocation.getSign());
        pkEntity.setBackUrl(createLocation.getBackUrl());
        pkEntity.setTime(System.currentTimeMillis());
        daoService.insertEntity(pkEntity);

        return pkId;
    }




    public PkDetail 搜索卡点(String pkId) throws IOException {


        return this.querySinglePk(pkId);
    }



    public PkDetail querySinglePk(String pkId) throws IOException {
        PkEntity pk = this.querySinglePkEntity(pkId);
        return this.querySinglePk(pk);
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



    public PkDetail querySinglePk(PkEntity pk) throws IOException {
        if(ObjectUtils.isEmpty(pk)){return null;}
        String pkId = pk.getPkId();
        PkDetail pkDetail = new PkDetail();
        pkDetail.setPkId(pk.getPkId());
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
        pkDetail.setApproved(dynamicService.getKeyValue(CacheKeyName.已审核数量,pkId));
        pkDetail.setTopPostId(pk.getTopPostId());
        pkDetail.setTopPost(postService.查询顶置帖子(pk));
        pkDetail.setMarker(this.查询Marker(pk));
        pkDetail.setCircle(this.查询Circle(pk,locationType));


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
        locationType.setScale(pk.getTypeScale());
        locationType.setRange(this.长度转义(pk.getTypeRange()));
        locationType.setRangeLength(pk.getTypeRange());
        return locationType;

    }

    private String 长度转义(int typeRange) {
        if(typeRange > 1000)
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
        locationType.setRangeLength(RandomUtil.getRandomNumber());
        locationType.setScale(RandomUtil.getRandomScale());
        locationType.setTypeName("商业中心");
        return locationType;
    }


    public List<PkDetail> 查询附近卡点(String queryerId,double latitude, double longitude) {
        List<PkDetail> pks = new ArrayList<>();

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("latitude",CompareTag.Small,latitude + 0.06D)
                .andFilter()
                .compareFilter("latitude",CompareTag.Bigger,latitude - 0.06D)
                .andFilter()
                .compareFilter("longitude",CompareTag.Small,longitude + 0.06D)
                .andFilter()
                .compareFilter("longitude",CompareTag.Bigger,longitude - 0.06D)
                .andFilter()
                .nullFilter("topPostId",false)
                .orderByRandomFilter()
                .pageLimitFilter(1,20);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);

        if(!org.apache.commons.collections4.CollectionUtils.isEmpty(pkEntities))
        {
            pkEntities.forEach(pk->{
                try {
                    PkDetail pkDetail = this.querySinglePk(pk);
                    int length = this.计算坐标间距离(latitude,longitude,pk.getLatitude(),pk.getLongitude());
                    pkDetail.setUserLength(length);
                    pkDetail.setUserLengthStr(this.距离转换成描述(length));
                    pks.add(pkDetail);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            });
        }

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

    public int 查询用户打卡次数(String pkId, String userId) {

        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkUserEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkUserEntity pkUserEntity = daoService.querySingleEntity(PkUserEntity.class,cfilter);
        if(ObjectUtils.isEmpty(pkUserEntity)){
            return 0;
        }
        else
        {
            return  pkUserEntity.getPostTimes();
        }

    }

    public void 用户发布打卡一次(String pkId, String userId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkUserEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkUserEntity pkUserEntity = daoService.querySingleEntity(PkUserEntity.class,cfilter);
        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            pkUserEntity.setPostTimes(pkUserEntity.getPostTimes()+1);
            daoService.updateEntity(pkUserEntity);
        }
        else
        {
            pkUserEntity = new PkUserEntity();
            pkUserEntity.setPostTimes(1);
            pkUserEntity.setPkId(pkId);
            pkUserEntity.setUserId(userId);
            daoService.insertEntity(pkUserEntity);
        }


    }

    public void 打卡次数减一(String pkId, String userId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkUserEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkUserEntity pkUserEntity = daoService.querySingleEntity(PkUserEntity.class,cfilter);
        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            pkUserEntity.setPostTimes(pkUserEntity.getPostTimes()-1<0?0:pkUserEntity.getPostTimes()-1);
            daoService.updateEntity(pkUserEntity);
        }
        else
        {
            pkUserEntity = new PkUserEntity();
            pkUserEntity.setPostTimes(0);
            pkUserEntity.setPkId(pkId);
            pkUserEntity.setUserId(userId);
            daoService.insertEntity(pkUserEntity);
        }


    }
}
