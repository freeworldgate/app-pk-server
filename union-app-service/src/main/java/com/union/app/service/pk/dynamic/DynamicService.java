package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.OperType;
import com.union.app.domain.pk.PkDynamic.*;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkPostListEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class DynamicService {

    @Autowired
    PkService pkService;

    @Autowired
    UserService userService;


    @Autowired
    PostService postService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    ApproveService approveService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisMapService redisMapService;

    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    RedisStringUtil redisUtil;


    public void delKey(String key){


        redisTemplate.delete(key);}


    public int getKeyValue(DynamicItem item,String key){
        Object value = redisTemplate.opsForHash().get(DynamicKeyName.getKey_Value_Name(item),key);
        if(ObjectUtils.isEmpty(value)){
            return 0;
        }
        else {
            return (int)value;
        }
    }
    public void setKeyValue(DynamicItem item,String key,long value){
        redisTemplate.opsForHash().put(DynamicKeyName.getKey_Value_Name(item),key,value);
    }
    public int valueIncr(DynamicItem item,String key){
        long value = redisTemplate.opsForHash().increment(DynamicKeyName.getKey_Value_Name(item),key,1L);
        return new Long(value).intValue();
    }
    public int valueDecr(DynamicItem item,String key){
        long value = redisTemplate.opsForHash().increment(DynamicKeyName.getKey_Value_Name(item),key,-1L);
        if(value < 0){this.setKeyValue(item,key,0);value = 0;}
        return new Long(value).intValue();
    }





    public int getMapKeyValue(DynamicItem item,String mapName,String key){
        Object value = redisTemplate.opsForHash().get(DynamicKeyName.getMapKey_Value_Name(item,mapName),key);
        if(ObjectUtils.isEmpty(value)){
            return 0;
        }
        else {
            return (int)value;
        }
    }
    public void delMapKeyValue(DynamicItem item,String mapName,String key){
        Object value = redisTemplate.opsForHash().delete(DynamicKeyName.getMapKey_Value_Name(item,mapName),key);
    }
    public void setMapKeyValue(DynamicItem item,String mapName,String key,long value){
        redisTemplate.opsForHash().put(DynamicKeyName.getMapKey_Value_Name(item,mapName),key,value);
    }
    public int mapValueIncr(DynamicItem item,String mapName,String key){
        long value = redisTemplate.opsForHash().increment(DynamicKeyName.getMapKey_Value_Name(item,mapName),key,1L);
        return new Long(value).intValue();
    }
    public int mapValueDecr(DynamicItem item,String mapName,String key){
        long value = redisTemplate.opsForHash().increment(DynamicKeyName.getMapKey_Value_Name(item,mapName),key,-1L);
        if(value < 0){this.setMapKeyValue(item,mapName,key,0);value = 0;}
        return new Long(value).intValue();
    }







//
//
//    public int 获取收藏积分(String pkId,String userId){
//        return (int)(redisMapService.getIntValue(CacheKeyName.打榜Follow(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.收藏一次的积分,100));
//    }
//    public void 收藏积分加1(String pkId,String userId){
//        redisMapService.valueIncr(CacheKeyName.打榜Follow(pkId),userId);
//    }
//    public void 收藏积分减1(String pkId,String userId){
//        redisMapService.valueDecr(CacheKeyName.打榜Follow(pkId),userId) ;
//    }
//
//    public int 获取今日分享积分(String pkId,String userId){ return (int)redisMapService.getIntValue(CacheKeyName.打榜Share(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.分享一次的积分,100) ;}
//
//
//    public int 获取用户排名(String pkId, String userId) {
//        return (int)redisSortSetService.getIndex(CacheKeyName.打榜排名列表(pkId),userId);
//    }


//    //有序集合-按照时间排序
//    public void 更新今日用户排名(String pkId,String userId,Date date){
//        if(pkService.isPkCreator(pkId,userId)){return;}
//        double score = (获取收藏积分(pkId,userId) + 获取今日分享积分(pkId,userId)) * -1.0D;
//        redisSortSetService.addEle(CacheKeyName.打榜排名列表(pkId),userId,score);
//
//    }
    public String 查询审核用户(String pkId, String postId) {
        PkPostListEntity pkPostListEntity = pkService.查询图册排列(pkId,postId);
        if(!ObjectUtils.isEmpty(pkPostListEntity) && pkPostListEntity.getStatu() == PostStatu.审核中)
        {
            return pkService.querySinglePkEntity(pkId).getUserId();
        }
        else
        {
            return "";
        }



    }

    public boolean 审核等待时间过长(String pkId, String postId) {
//        double score = redisSortSetService.getEleScore(CacheKeyName.榜主审核中列表(pkId) ,postId);
        long scoreAbs = pkService.查询POST审核时间(pkId,postId) ;//new Double(Math.abs(score)).longValue() ;
        long canComplainWaitingTime  = AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间) * 60 * 1000;
        if(System.currentTimeMillis() - scoreAbs > canComplainWaitingTime)
        {
            return true;
        }
        else
        {
            return false;
        }


    }








//    public void 榜帖恢复到审核中状态(String pkId, String postId) {
//
////        榜帖已经审核过了，重新清除所有记录
//        if(redisSortSetService.isMember(CacheKeyName.榜主已审核列表(pkId),postId))
//        {
//
//            redisSortSetService.remove(CacheKeyName.榜主已审核列表(pkId),postId);
//            redisSortSetService.remove(CacheKeyName.榜主审核中列表(pkId) ,postId);
//
//        }
//
//
//
//    }









//    private boolean 是否是预备审核员(String pkId, String userId) { return redisSortSetService.isMember(CacheKeyName.预设审核员列表(pkId),userId); }



    public List<FactualInfo> 获取当前PK操作动态(String pkId) {
        List<FactualInfo> factualInfos = new ArrayList<>();
        String keyName = DynamicKeyName.getSetKey_Value_Name(DynamicItem.PK当前操作动态,pkId);
        Set<ZSetOperations.TypedTuple<String>> infos = redisTemplate.opsForZSet().reverseRangeWithScores(keyName,0,AppConfigService.getConfigAsInteger(常量值.操作动态的保留长度,20)-1);
        if(CollectionUtils.isEmpty(infos)){return factualInfos;}
        for(ZSetOperations.TypedTuple<String> entry : infos) {
            factualInfos.add(JSON.parseObject(entry.getValue(), FactualInfo.class));
        }
        return factualInfos;
    }

    public void 添加动态(String pkId, FactualInfo factualInfo) {
        String keyName = DynamicKeyName.getSetKey_Value_Name(DynamicItem.PK当前操作动态,pkId);
        String value = JSON.toJSONString(factualInfo);
        Double time = System.currentTimeMillis() * 1.0D;
        redisTemplate.opsForZSet().add(keyName,value, time);


        //清除过长的部分。
        long size = redisTemplate.opsForZSet().size(keyName);
        if(size > (AppConfigService.getConfigAsInteger(常量值.操作动态的保留长度,20) + 20)) {
            redisTemplate.opsForZSet().removeRange(keyName, 0, size - AppConfigService.getConfigAsInteger(常量值.操作动态的保留长度,20) -1);
        }


    }
    public void 添加操作动态(String pkId,String userId,OperType operType){
        FactualInfo factualInfo = new FactualInfo();
        factualInfo.setFactualId(UUID.randomUUID().toString());
        factualInfo.setUser(userService.queryUser(userId));
        factualInfo.setOperType(new KeyNameValue(operType.getKey(),operType.getValue()));
        factualInfo.setTime(String.valueOf(System.currentTimeMillis()));
        this.添加动态(pkId,factualInfo);
    }









    public void 新增注册用户(String appName, String pkId,String userId, String fromUser,Date date) {
        if(StringUtils.isBlank(fromUser)){return;}
//        this.mapValueIncr(DynamicItem.PKUSER今日分享次数,pkId,fromUser);
//        this.更新今日用户排名(pkId,fromUser,date);
    }


    /**
     *
     * 有效期5分钟
     *
     * @param pkId
     * @param postId
     */
    public void 设置帖子的审核用户(String pkId, String postId) {
       // pkService.querySinglePkEntity()
        PkPostListEntity pkPostListEntity = pkService.查询图册排列(pkId,postId);
        if(ObjectUtils.isEmpty(pkPostListEntity))
        {
            pkPostListEntity = new PkPostListEntity();
            pkPostListEntity.setPkId(pkId);
            pkPostListEntity.setPostId(postId);

            pkPostListEntity.setStatu(PostStatu.审核中);
            pkPostListEntity.setTime(System.currentTimeMillis());

            daoService.insertEntity(pkPostListEntity);
            pkService.添加一个审核中(pkId);

        }








//        redisSortSetService.addEle(CacheKeyName.榜主审核中列表(pkId),postId,System.currentTimeMillis() * -1D);

    }

    public void 已审核(String pkId, String postId) {

//            redisSortSetService.addEle(CacheKeyName.榜主已审核列表(pkId),postId,System.currentTimeMillis() * -1D);
//            redisSortSetService.remove(CacheKeyName.榜主审核中列表(pkId),postId);
            pkService.更新图册状态列表(pkId,postId);

            this.更新PK排名(pkId);

    }

    public void 驳回用户审核(String pkId, String postId) {
        pkService.删除审核中Post(pkId,postId);
//        redisSortSetService.remove(CacheKeyName.榜主审核中列表(pkId),postId);
    }



        //有序集合-按照时间排序
    public void 更新PK排名(String pkId){
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        int size =  ObjectUtils.isEmpty(pkEntity)?0:pkEntity.getApproved() ;
        double score = size * -1.0D;
        redisSortSetService.addEle(CacheKeyName.PK排名(),pkId,score);

    }








//    public int 今日打榜总人数(String pkId,Date date) { return redisSortSetService.size(CacheKeyName.打榜排名列表(pkId)); }

//    public boolean 添加预备审核员(String pkId,String userId) {
//        boolean result = false;
//        synchronized (pkId)
//        {
//
//            if(this.已设置预审核员数量(pkId) < approveService.计算管理员设置人数(pkId, new Date()))
//            {
//                result =  redisSortSetService.addEle(CacheKeyName.预设审核员列表(pkId),userId,System.currentTimeMillis() * 1D);
//            }
//        }
//        return result;
//    }
//
//    public void 删除预备审核员(String pkId,String userId) { redisSortSetService.remove(CacheKeyName.预设审核员列表(pkId),userId); }


//    public List<UserIntegral> queryUserIntegrals(String pkId,int page,Date date){
//        List<UserIntegral> userIntegrals = new ArrayList<>();
//        Set<String> pageList = redisSortSetService.queryPage(CacheKeyName.打榜排名列表(pkId),page);
//        for(String value : pageList){
//            UserIntegral userIntegral = 查询用户打榜信息(pkId,value);
//            userIntegrals.add(userIntegral);
//        }
//        userIntegrals.sort(new Comparator<UserIntegral>() {
//            @Override
//            public int compare(UserIntegral o1, UserIntegral o2) {
//                return o1.getIndex() - o2.getIndex();
//            }
//        });
//        return userIntegrals;
//    }


//    public UserIntegral 查询用户打榜信息(String pkId,String userId){
//        UserIntegral userIntegral = new UserIntegral();
//        int sortIndex = this.获取用户排名(pkId,userId);
//        int follow = this.获取收藏积分(pkId,userId);
//        int share = this.获取今日分享积分(pkId,userId);
//        userIntegral.setIndex(sortIndex);
//        userIntegral.setShare(share);
//        userIntegral.setFollow(follow);
//        userIntegral.setUser(userService.queryUser(userId));
//        userIntegral.setCreator(pkService.isPkCreator(pkId,userId));
//        userIntegral.setSelectPreApprover(this.是否是预备审核员(pkId,userId));
//        return userIntegral;
//    }











//    public UserIntegral 查询审核用户信息(String pkId, String userId,Date date) {
//        UserIntegral userIntegral = null;
//        if(pkService.isPkCreator(pkId,userId))
//        {
//            userIntegral = new UserIntegral();
//            userIntegral.setCreator(true);
//            userIntegral.setUser(pkService.queryPkCreator(pkId));
//            userIntegral.setSelect(1);
//            userIntegral.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkId)));
//            userIntegral.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkId)));
//
//        }
//        else
//        {
//            userIntegral = redisMapService.getValue(CacheKeyName.所有审核人(pkId),userId,UserIntegral.class);
//            if(!ObjectUtils.isEmpty(userIntegral)){
//                userIntegral.setApproved(redisSortSetService.size(CacheKeyName.审核员已审核列表(pkId,date,userIntegral.getUser().getUserId())));
//                userIntegral.setApproving(redisSortSetService.size(CacheKeyName.审核员审核中列表(pkId,date,userIntegral.getUser().getUserId())));
//            }
//
//        }
//        return userIntegral;
//    }
//
//
//    public List<UserIntegral> 查询预备审核用户列表(String pkId,Date date) {
//        List<UserIntegral> userIntegrals = new ArrayList<>();
//        Set<String> userIds = redisSortSetService.members(CacheKeyName.预设审核员列表(pkId));
//        for(String userId:userIds){
//            UserIntegral userIntegral = 查询用户打榜信息(pkId,userId);
//            userIntegrals.add(userIntegral);
//        }
//        return userIntegrals;
//    }






















































    public int 计算今日剩余时间(String pkId) throws ParseException {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String dateStr = simpleDateFormat.format(new Date());
        Date today = null;

        today = simpleDateFormat.parse(dateStr);

        long leftTime = 24 * 60 * 60  - (System.currentTimeMillis() - today.getTime())/1000;


        return new Long(leftTime).intValue();
    }


    public boolean 是否开抢时间(String pkId) throws ParseException {

        int leftTime = this.计算今日剩余时间(pkId);
        if(!((60 * 60 > leftTime) && (leftTime > 15 * 60))){
            return false;
        }
        else
        {
            return true;
        }


    }




//    private int 已设置预审核员数量(String pkId) { return redisSortSetService.size(CacheKeyName.预设审核员列表(pkId)); }

//    public boolean 用户是否为今日审核员(String pkId, String approveUserId,Date date) {
//        User user = pkService.queryPkCreator(pkId);
//        if(StringUtils.equals(user.getUserId(),approveUserId))
//        {
//            return true;
//        }
//        return false;
//
//
//    }



    public List<Post> 查询已审核指定范围的Post(String userId,String pkId,int page) throws UnsupportedEncodingException {


        List<Post> posts = new ArrayList<>();
        List<String> postIds = new ArrayList<>();

        List<String> pageList =  pkService.查询已审核页(pkId,page) ;//redisSortSetService.queryPage(CacheKeyName.榜主已审核列表(pkId),page);
        postIds.addAll(pageList);


        for(String postId:postIds)
        {
            Post post = postService.查询帖子(pkId,postId,"");
            if(!ObjectUtils.isEmpty(post)) {
                posts.add(post);
            }
        }
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        for (Post post : posts) {
            post.setApproveComment(approveService.获取留言信息(pkId, post.getPostId()));
        }
        return posts;


    }
    public Post 查询审核中指定范围的Post(String pkId) throws UnsupportedEncodingException {


        String postId = pkService.获取一个审核中Post(pkId);//redisSortSetService.popEle(CacheKeyName.榜主审核中列表(pkId));
        if(StringUtils.isBlank(postId)){return null;}
        Post post = postService.查询帖子(pkId,postId,"");
        if(!ObjectUtils.isEmpty(post)) {
            post.setApproveComment(approveService.获取留言信息(pkId, postId));
        }



        return post;

    }

//    public int 已审核订单数量(String pkId, String approverUserId,Date date) {
//        if(pkService.isPkCreator(pkId,approverUserId))
//        {
//            return redisSortSetService.size(CacheKeyName.榜主已审核列表(pkId));
//        }
//        else
//        {
//            return redisSortSetService.size(CacheKeyName.审核员已审核列表(pkId,date,approverUserId));
//        }
//
//
//    }
//
//    public int 审核中订单数量(String pkId, String approverUserId,Date date) {
//        if(pkService.isPkCreator(pkId,approverUserId))
//        {
//            return redisSortSetService.size(CacheKeyName.榜主审核中列表(pkId));
//        }
//        else
//        {
//            return redisSortSetService.size(CacheKeyName.审核员审核中列表(pkId,date,approverUserId));
//
//        }
//    }

    public String 用户拉取群组记录(String pkId, String fromUserName) {


        return null;
    }

    public String 获取榜主群组二维码(String pkId) {

        return null;

    }

    public void 设置PK群组二维码MediaId(String pkId, String mediaId) {
        redisMapService.setStringValue(CacheKeyName.群组二维码(),pkId,mediaId);
    }
//    public void 设置内置公开PK群组二维码MediaId(String pkId, String mediaId) {
//        redisMapService.setStringValue(CacheKeyName.内置公开PK群组二维码(),pkId,mediaId);
//    }
    public String 查询PK群组二维码MediaId(String pkId) {
        return redisMapService.getStringValue(CacheKeyName.群组二维码(),pkId);
    }
    public void 设置PK群组二维码Url(String pkId, String url) {
        redisMapService.setStringValue(CacheKeyName.群组URL(),pkId,url);
    }
//    public void 设置内置公开PK群组二维码Url(String pkId, String url) {
//        redisMapService.setStringValue(CacheKeyName.内置公开PK群组URL(),pkId,url);
//    }
    public String 查询PK群组二维码Url(String pkId) {
        return redisMapService.getStringValue(CacheKeyName.群组URL(),pkId);
    }

//    public String 查询内置公开PK群组二维码Url(String pkId) {
////        return redisMapService.getStringValue(CacheKeyName.内置公开PK群组URL(),pkId);
////    }
    public String 获取当前拉取图片(String fromUserName) {

        return redisMapService.getStringValue(CacheKeyName.拉取资源图片(),fromUserName);

    }

    public void 当前拉取图片(String fromUserName, String mediaId) {

        redisMapService.setStringValue(CacheKeyName.拉取资源图片(),fromUserName,mediaId);

    }

//    public String 查询今日审核员数量(String pkId) {
//
//        int size = redisMapService.size(CacheKeyName.所有审核人(pkId));
//        return String.valueOf(size);
//
//    }
//
//    public String 查询今日打榜用户数量(String pkId) {
//        int size = redisSortSetService.size(CacheKeyName.打榜排名列表(pkId));
//        return String.valueOf(size);
//
//
//    }



    //
    public long 查询群组分配的人数(String groupId) { return redisMapService.getIntValue(CacheKeyName.群组分配人数(),groupId); }
    public long 群组分配的人数加一(String groupId) { return redisMapService.valueIncr(CacheKeyName.群组分配人数(),groupId); }


    public long 查询收款码分配的人数(String feeCodeId) { return redisMapService.getIntValue(CacheKeyName.收款码分配人数(),feeCodeId); }
    public long 收款码分配的人数加一(String feeCodeId) { return redisMapService.valueIncr(CacheKeyName.收款码分配人数(),feeCodeId); }











    public long 查看内置相册已审核榜帖(String pkId) {
        return redisMapService.getIntValue(CacheKeyName.内置相册已审核(),pkId);


    }

    public long 查看内置相册审核中榜帖(String pkId) {



        return redisMapService.getIntValue(CacheKeyName.内置相册审核中(),pkId);
    }

    public boolean 查看内置相册群组状态(String pkId) {


        return redisMapService.getIntValue(CacheKeyName.内置相册群组状态(),pkId) == 0;

    }

    public void 更新内置相册已审核数量(String pkId, int value) {
        redisMapService.setLongValue(CacheKeyName.内置相册已审核(),pkId,Long.valueOf(value+""));
    }

    public void 更新内置相册审核中数量(String pkId, int value) {
        redisMapService.setLongValue(CacheKeyName.内置相册审核中(),pkId,Long.valueOf(value+""));

    }

    public void 更新内置相册群组状态(String pkId) {
        if(redisMapService.getIntValue(CacheKeyName.内置相册群组状态(),pkId) == 0)
        {
            redisMapService.setLongValue(CacheKeyName.内置相册群组状态(),pkId,1L);
        }
        else
        {
            redisMapService.setLongValue(CacheKeyName.内置相册群组状态(),pkId,0L);
        }

    }

//    public String 查询内置公开PK群组二维码MediaId(String pkId) {
//        return redisMapService.getStringValue(CacheKeyName.内置公开PK群组二维码(),pkId);
//    }

    public void 更新内置相册参数(String pkId) {
        if(RandomUtil.getRandomNumber()%2 == 0){
            redisMapService.valueIncr(CacheKeyName.内置相册审核中(),pkId,1L);
        }
        if(RandomUtil.getRandomNumber()%2 == 0){
            redisMapService.valueIncr(CacheKeyName.内置相册已审核(),pkId,1L);
        }



    }

    public void 扫描次数加1(String pkId, String postId) {
         redisMapService.valueIncr(CacheKeyName.PK扫码次数(pkId),postId);



    }

    public long 查询扫码次数(String pkId, String postId) {

        return redisMapService.getIntValue(CacheKeyName.PK扫码次数(pkId),postId);

    }

//    public long 查询收款码确认次数(String feeCodeId) { return redisMapService.getIntValue(CacheKeyName.收款码确认次数(),feeCodeId); }
//    public long 收款码确认次数加一(String feeCodeId) { return redisMapService.valueIncr(CacheKeyName.收款码确认次数(),feeCodeId); }
















//    public String 查询PK公告消息Id(String pkId) {
//        return redisMapService.getStringValue(CacheKeyName.审核消息ID(),pkId);
//
//    }
//    public void 设置PK公告消息Id(String pkId, String messageId) {
//        redisMapService.setStringValue(CacheKeyName.审核消息ID(),pkId,messageId);
//    }

//    public String 获取需要更新公告的PKId() {
//        return redisTemplate.opsForSet().pop(CacheKeyName.更新公告的PKId列表());
//
//    }
//
//    public void 添加需要更新MediaId的PKId(String pkId) {
//        redisTemplate.opsForSet().add(CacheKeyName.更新公告的PKId列表(),pkId);
//    }


}




