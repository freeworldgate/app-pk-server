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
    private static int 计算坐标间距离(Double longitude1, Double latitude1, Double longitude2, Double latitude2){

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
        pkEntity.setType(createLocation.getType());
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
        pkDetail.setType(this.查询类型(pk.getType()));
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setTime(TimeUtils.convertTime(pk.getTime()));
        pkDetail.setBackUrl(pk.getBackUrl());
        pkDetail.setApproved(dynamicService.getKeyValue(CacheKeyName.已审核数量,pkId));
        pkDetail.setTopPostId(pk.getTopPostId());
        pkDetail.setTopPost(postService.查询顶置帖子(pk));
        return pkDetail;
    }

    private LocationType 查询类型(String type) {


        LocationType locationType = new LocationType();
        locationType.setRange(200);
        locationType.setTypeName("商业街区");

        return locationType;

    }


    public List<PkDetail> 查询附近卡点(String s, String s1) {
        List<PkDetail> pks = new ArrayList<>();

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .pageLimitFilter(1,20);
        List<PkEntity> pkEntities = daoService.queryEntities(PkEntity.class,filter);

        if(!org.apache.commons.collections4.CollectionUtils.isEmpty(pkEntities))
        {
            pkEntities.forEach(pk->{
                try {
                    pks.add(this.querySinglePk(pk));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            });
        }

        return pks;

    }
}
