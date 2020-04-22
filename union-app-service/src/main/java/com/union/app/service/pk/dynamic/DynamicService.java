package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.id.KeyGetter;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.OperType;
import com.union.app.domain.pk.PkDynamic.*;
import com.union.app.domain.pk.PkMode;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.UserCode;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.UserDynamicEntity;
import com.union.app.entity.pk.apply.OrderType;
import com.union.app.entity.pk.apply.PayOrderEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.imp.RedisMapService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class DynamicService {

    @Autowired
    PkService pkService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

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









    public int 获取收藏积分(String pkId,String userId){ return (int)(redisMapService.getIntValue(CacheKeyName.打榜Follow(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.收藏一次的积分,100)); }

    public int 获取今日分享积分(String pkId,Date date,String userId){ return (int)redisMapService.getIntValue(CacheKeyName.打榜Share(pkId,date),userId) * AppConfigService.getConfigAsInteger(常量值.分享一次的积分,100) ;}


    public int 获取用户排名(String pkId, String userId,Date date) { return (int)redisSortSetService.getIndex(CacheKeyName.打榜排名列表(pkId,date),userId); }


    //有序集合-按照时间排序
    public void 更新今日用户排名(String pkId,String userId,Date date){
        if(pkService.isPkCreator(pkId,userId)){return;}
        double score = (获取收藏积分(pkId,userId) + 获取今日分享积分(pkId,date,userId)) * -1.0D;
        redisSortSetService.addEle(CacheKeyName.打榜排名列表(pkId,date),userId,score);

    }
    public String 查询审核用户(String pkId, String postId,Date date) { return redisMapService.getStringValue(CacheKeyName.榜帖有效审核人(pkId,date),postId); }









    private int 是否是预备审核员(String pkId, String userId) { return redisSortSetService.isMember(CacheKeyName.预设审核员列表(pkId),userId)?1:0; }



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
        this.mapValueIncr(DynamicItem.PKUSER今日分享次数,pkId,fromUser);
        this.更新今日用户排名(pkId,fromUser,date);
    }


    /**
     *
     * 有效期5分钟
     *
     * @param pkId
     * @param postId
     * @param approveUserId
     */
    public void 设置帖子的审核用户(String pkId, String postId, String approveUserId,Date date) {


        if(pkService.isPkCreator(pkId,approveUserId)){
            redisSortSetService.addEle(CacheKeyName.榜主审核中列表(pkId),postId,System.currentTimeMillis() * 1D);

        }
        else
        {
            redisSortSetService.addEle(CacheKeyName.审核员审核中列表(pkId,date,approveUserId),postId,System.currentTimeMillis() * 1D);

        }
        redisMapService.setStringValue(CacheKeyName.榜帖有效审核人(pkId,date),postId,approveUserId);
    }

    public ApproveMessage 查询审核用户的消息(String pkId, String approveUserId,Date date) {
        ApproveMessage approveMessage = new ApproveMessage();
        String message = redisMapService.getStringValue(CacheKeyName.审核人消息(pkId,date),approveUserId);
        if(StringUtils.isBlank(message)){
            approveMessage.setText("消息内容");
            approveMessage.setTitle("消息标题");
            approveMessage.setUrl(RandomUtil.getRandomImage());
        }
        else
        {
            approveMessage = JSON.parseObject(message,ApproveMessage.class);
        }
        return approveMessage;
    }

    public void 已审核(String pkId, String postId,String approveUserId,Date date) {

        if(pkService.isPkCreator(pkId,approveUserId)){


            redisSortSetService.addEle(CacheKeyName.榜主已审核列表(pkId),postId,System.currentTimeMillis() * 1D);
            redisSortSetService.remove(CacheKeyName.榜主审核中列表(pkId),postId);

        }
        else
        {

            redisSortSetService.addEle(CacheKeyName.审核员已审核列表(pkId,date,approveUserId),postId,System.currentTimeMillis() * 1D);
            redisSortSetService.remove(CacheKeyName.审核员审核中列表(pkId,date,approveUserId),postId);
        }

    }


    public int 今日打榜总人数(String pkId,Date date) { return redisSortSetService.size(CacheKeyName.打榜排名列表(pkId,date)); }

    public void 添加预备审核员(String pkId,String userId) { redisSortSetService.addEle(CacheKeyName.预设审核员列表(pkId),userId,System.currentTimeMillis() * 1D); }

    public void 删除预备审核员(String pkId,String userId) { redisSortSetService.remove(CacheKeyName.预设审核员列表(pkId),userId); }


    /**
     * 打榜排名信息
     * @param pkId
     * @param page
     * @param date
     * @return
     */
    public List<UserIntegral> queryUserIntegrals(String pkId,int page,Date date){
        List<UserIntegral> userIntegrals = new ArrayList<>();
        Set<String> pageList = redisSortSetService.queryPage(CacheKeyName.打榜排名列表(pkId,date),page);
        for(String value : pageList){
            UserIntegral userIntegral = 查询用户打榜信息(pkId,value,date);
            userIntegrals.add(userIntegral);
        }
        userIntegrals.sort(new Comparator<UserIntegral>() {
            @Override
            public int compare(UserIntegral o1, UserIntegral o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        return userIntegrals;
    }


    public UserIntegral 查询用户打榜信息(String pkId,String userId,Date date){
        UserIntegral userIntegral = new UserIntegral();
        int sortIndex = this.获取用户排名(pkId,userId,date);
        int follow = this.获取收藏积分(pkId,userId);
        int share = this.获取今日分享积分(pkId,date,userId);
        userIntegral.setIndex(sortIndex);
        userIntegral.setShare(share);
        userIntegral.setFollow(follow);
        userIntegral.setUser(userService.queryUser(userId));
        userIntegral.setSelect(this.是否是预备审核员(pkId,userId));
        return userIntegral;
    }








    public List<UserIntegral> 查询今日审核用户列表(String pkId,Date date) {

        List<UserIntegral> allUsers = new ArrayList<>();
        List<UserIntegral> userIntegrals = new ArrayList<>();
        User user = pkService.queryPkCreator(pkId);
        UserIntegral creator = 查询审核用户信息(pkId,user.getUserId(),date);
        userIntegrals.add(creator);

        if(!redisTemplate.hasKey(CacheKeyName.所有审核人(pkId,date)))
        {
            //12点以后   更新
            synchronized (pkId) {
                if(!redisTemplate.hasKey(CacheKeyName.所有审核人(pkId,date)))
                {
                    this.更新今日审核员(pkId, date);
                }
            }
        }
        List<UserIntegral> dailyApprovers = redisMapService.values(CacheKeyName.所有审核人(pkId,date),UserIntegral.class);

        if(!CollectionUtils.isEmpty(dailyApprovers)){
            userIntegrals.addAll(dailyApprovers);
        }
        for(UserIntegral userIntegral:userIntegrals)
        {
            UserIntegral fullUserIntegral = 查询审核用户信息(pkId,userIntegral.getUser().getUserId(),date);
            allUsers.add(fullUserIntegral);
        }
        return allUsers;
    }





    public UserIntegral 查询审核用户信息(String pkId, String userId,Date date) {
        UserIntegral userIntegral = null;
        if(pkService.isPkCreator(pkId,userId))
        {
            userIntegral = new UserIntegral();
            userIntegral.setCreator(true);
            userIntegral.setUser(pkService.queryPkCreator(pkId));
            userIntegral.setSelect(1);
            userIntegral.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkId)));
            userIntegral.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkId)));

        }
        else
        {
            userIntegral = redisMapService.getValue(CacheKeyName.所有审核人(pkId,date),userId,UserIntegral.class);
            if(!ObjectUtils.isEmpty(userIntegral)){
                userIntegral.setApproved(redisSortSetService.size(CacheKeyName.审核员已审核列表(pkId,date,userIntegral.getUser().getUserId())));
                userIntegral.setApproving(redisSortSetService.size(CacheKeyName.审核员审核中列表(pkId,date,userIntegral.getUser().getUserId())));
            }

        }
        return userIntegral;
    }


    public List<UserIntegral> 查询预备审核用户列表(String pkId,Date date) {
        List<UserIntegral> userIntegrals = new ArrayList<>();
        Set<String> userIds = redisSortSetService.members(CacheKeyName.预设审核员列表(pkId));
        for(String userId:userIds){
            UserIntegral userIntegral = 查询用户打榜信息(pkId,userId,date);
            userIntegrals.add(userIntegral);
        }
        return userIntegrals;
    }











    private void 更新今日审核员(String pkId,Date date) {

        this.补全所有预备审核员(pkId,date);

        Set<String> allPreApprovers = redisSortSetService.members(CacheKeyName.预设审核员列表(pkId));

        List<UserIntegral> userIntegrals = new ArrayList<>();
        for(String userId:allPreApprovers){
            UserIntegral userIntegral = 查询用户打榜信息(pkId,userId,TimeUtils.前一天(date));
            userIntegrals.add(userIntegral);
        }

        for(UserIntegral integral:userIntegrals)
        {
            redisMapService.setStringValue(CacheKeyName.所有审核人(pkId,date),integral.getUser().getUserId(),JSON.toJSONString(integral));
        }
        redisSortSetService.delete(CacheKeyName.预设审核员列表(pkId));
    }




    private void 补全所有预备审核员(String pkId,Date date) {
        //榜主为默认审核员

        long currentSize = this.已设置预审核员数量(pkId);
        int approveNum = approveService.计算管理员设置人数(pkId);
        if(currentSize >= approveNum){return;}
        int i=0;
        while(this.已设置预审核员数量(pkId) < approveNum){
            Set<String> keyStrs =redisTemplate.opsForZSet().range(CacheKeyName.打榜排名列表(pkId,TimeUtils.前一天(date)),i ,i);
            if(!CollectionUtils.isEmpty(keyStrs)){
                this.添加预备审核员(pkId,keyStrs.iterator().next());
            }
            else
            {
                break;
            }
            i = i+1;
        }
    }









































    public int 计算今日剩余时间(String pkId){


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = (String)redisTemplate.opsForHash().get("PK-CUREENT-DATE",pkId);
        Date pkTime = null;
        try {
            pkTime = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return 0;
        }
        long leftTime = 24 * 60 * 60  - (System.currentTimeMillis() - pkTime.getTime())/1000;


        return new Long(leftTime).intValue();
    }

    public void 验证预备审核员(String pkId, String userId,Date date) throws AppException, ParseException {
        //时间是否允许。
        int leftTime = 计算今日剩余时间(pkId);
        if(!((20 * 60 > leftTime) && (leftTime > 5 * 60))){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非设置时间段"));
        }



        //是否在前20%用户
        int sortIndex = this.获取用户排名(pkId,userId,date);
        int 今日打榜总人数 = 今日打榜总人数(pkId,date);

        if(sortIndex > 今日打榜总人数/10 * AppConfigService.getConfigAsInteger(常量值.审核员合法排名比例,2)){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"用户排名过低"));
        }


        int approverNum = approveService.计算管理员设置人数(pkId);
        int hasSettingNum = this.已设置预审核员数量(pkId);
        if(hasSettingNum >= approverNum){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"审核员名额已满"));
        }




    }

    private int 已设置预审核员数量(String pkId) { return redisSortSetService.size(CacheKeyName.预设审核员列表(pkId)); }

    public boolean 用户是否为今日审核员(String pkId, String approveUserId,Date date) {
        User user = pkService.queryPkCreator(pkId);
        List<UserIntegral> userIntegrals = this.查询今日审核用户列表(pkId,date);
        Set<String> approvers = new HashSet<>();
        approvers.add(user.getUserId());
        for(UserIntegral userIntegral:userIntegrals){
            approvers.add(userIntegral.getUser().getUserId());
        }
        return approvers.contains(approveUserId);

    }



    public List<Post> 查询已审核指定范围的Post(String pkId,String approveUserId,int page,Date date) throws UnsupportedEncodingException {


        List<Post> posts = new ArrayList<>();
        List<String> postIds = new ArrayList<>();
        if(pkService.isPkCreator(pkId,approveUserId))
        {
            Set<String> pageList = redisSortSetService.queryPage(CacheKeyName.榜主已审核列表(pkId),page);
            postIds.addAll(pageList);
        }
        else
        {

            Set<String> pageList = redisSortSetService.queryPage(CacheKeyName.审核员已审核列表(pkId,date,approveUserId),page);
            postIds.addAll(pageList);
        }
        for(String postId:postIds)
        {
            Post post = postService.查询帖子(pkId,postId,"",date);
            post.setApproveComment(approveService.获取留言信息(pkId,postId,approveUserId));
            posts.add(post);

        }

        return posts;


    }
    public List<Post> 查询审核中指定范围的Post(String pkId,String approveUserId,int page,Date date) throws UnsupportedEncodingException {

        List<Post> posts = new ArrayList<>();
        List<String> postIds = new ArrayList<>();
        if(pkService.isPkCreator(pkId,approveUserId))
        {
            Set<String> pageList = redisSortSetService.queryPage(CacheKeyName.榜主审核中列表(pkId),page);
            postIds.addAll(pageList);
        }
        else
        {

            Set<String> pageList = redisSortSetService.queryPage(CacheKeyName.审核员审核中列表(pkId,date,approveUserId),page);
            postIds.addAll(pageList);
        }
        for(String postId:postIds)
        {
            Post post = postService.查询帖子(pkId,postId,"",date);
            post.setApproveComment(approveService.获取留言信息(pkId,postId,approveUserId));
            posts.add(post);

        }

        return posts;

    }
}




