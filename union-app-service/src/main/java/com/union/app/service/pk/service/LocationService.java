package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.pk.daka.CreateLocation;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.卡点.UserFollowEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
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
        pkDetail.setApproved(dynamicService.getKeyValue(CacheKeyName.已审核数量,pkId));
        pkDetail.setTopPostId(pk.getTopPostId());
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
        locationType.setTypeName(type);
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


    public void 打卡次数加一(String pkId, String userId) {
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
            pkUserEntity.setPostTimes(0);
            pkUserEntity.setPkId(pkId);
            pkUserEntity.setUserId(userId);
            daoService.insertEntity(pkUserEntity);
        }


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
            if(pkService.isPkCreator(pkId,userId)){pkImageEntity.setImgStatu(ImgStatu.审核通过);}else{pkImageEntity.setImgStatu(ImgStatu.审核中);}

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

    public void 添加关注(String userId, String followerId) {
        UserFollowEntity userFollowEntity = new UserFollowEntity();
        userFollowEntity.setUserId(followerId);
        userFollowEntity.setFollowerId(userId);
        userFollowEntity.setTime(System.currentTimeMillis());
        daoService.insertEntity(userFollowEntity);

    }

    public void 取消关注(String userId, String followerId) {

        UserFollowEntity userFollowEntity = 查询关注(userId,followerId);
        if(!ObjectUtils.isEmpty(userFollowEntity))
        {
           daoService.deleteEntity(userFollowEntity);
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

            User user = userService.queryUser(follower.getUserId());
            user.setFollowTime(TimeUtils.convertTime(follower.getTime()));
            users.add(user);
        });
        return users;

    }

    public List<Post> queryHiddenPkPost(String pkId, int page) throws UnsupportedEncodingException {

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
        if(StringUtils.equalsIgnoreCase(userId,pkImageEntity.getUserId())){
            daoService.deleteEntity(pkImageEntity);
        }else if(pkService.isPkCreator(pkId,userId)){
            pkImageEntity.setImgStatu(ImgStatu.审核中);
            daoService.updateEntity(pkImageEntity);
        }else{}
    }

    public void 设置卡点背景图片(String pkId, String userId, String imageId) throws AppException {
        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.信息反馈框("用户无权限...","非法用户"));
        }
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("imgId",CompareTag.Equal,imageId);
        PkImageEntity pkImageEntity = daoService.querySingleEntity(PkImageEntity.class,cfilter);
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        pkEntity.setBackUrl(pkImageEntity.getImgUrl());
        daoService.updateEntity(pkEntity);

    }

    public void 审核通过卡点图片(String pkId, String userId, String imageId) throws AppException {
        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.信息反馈框("用户无权限...","非法用户"));
        }
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkImageEntity.class)
                .compareFilter("imgId",CompareTag.Equal,imageId);
        PkImageEntity pkImageEntity = daoService.querySingleEntity(PkImageEntity.class,cfilter);
        pkImageEntity.setImgStatu(ImgStatu.审核通过);
        daoService.updateEntity(pkImageEntity);

    }
}
