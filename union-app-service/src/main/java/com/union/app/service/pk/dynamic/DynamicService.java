package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.OperType;
import com.union.app.domain.pk.PkDynamic.*;
import com.union.app.domain.pk.PkMode;
import com.union.app.domain.pk.UserCode;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.UserDynamicEntity;
import com.union.app.entity.pk.apply.OrderType;
import com.union.app.entity.pk.apply.PayOrderEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
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
    AppDaoService daoService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

//    @Autowired
//    RedisUtil redisUtil;

    @Autowired
    RedisStringUtil redisUtil;


    public void delKey(String key){redisTemplate.delete(key);}


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









    public int 获取收藏积分(String pkId,String userId){
        int follow = getMapKeyValue(DynamicItem.PKUSER榜帖被收藏的次数,pkId,userId);
        return follow * AppConfigService.getConfigAsInteger(常量值.收藏一次的积分,100);
    }
    public int 获取今日分享积分(String pkId,String userId){
        int share = getMapKeyValue(DynamicItem.PKUSER今日分享次数,pkId,userId);
        return share * AppConfigService.getConfigAsInteger(常量值.分享一次的积分,100);
    }

    public int 获取用户排名(String pkId, String userId) {
        String keyName = DynamicKeyName.getSetKey_Value_Name(DynamicItem.PK今日排名,pkId);
        long index = redisTemplate.opsForZSet().rank(keyName,userId);
        return new Long(index).intValue();
    }


    //有序集合-按照时间排序
    public void 更新今日用户排名(String pkId,String userId){

        if(pkService.isPkCreator(pkId,userId)){return;}

        //判定用户是否收款码已审核
        if(!userInfoService.用户是否具有有效收款码(pkId,userId)){
            return;
        }

        double score = (获取今日分享积分(pkId,userId) + 获取收藏积分(pkId,userId)) * 1.0D;

        String keyName = DynamicKeyName.getSetKey_Value_Name(DynamicItem.PK今日排名,pkId);

        redisTemplate.opsForZSet().add(keyName,userId,score);


        //清除过长的部分。
        long size = redisTemplate.opsForZSet().size(keyName);
        if(size > (AppConfigService.getConfigAsInteger(常量值.排名队列长度,1000) + 20)) {
            redisTemplate.opsForZSet().removeRange(keyName, 0, size - AppConfigService.getConfigAsInteger(常量值.排名队列长度,1000) -1);
        }





    }
    public void 生成PK打赏任务(String pkId){

        User creator = pkService.queryPkCreator(pkId);
        String keyName = DynamicKeyName.getSetKey_Value_Name(DynamicItem.PK今日排名,pkId);
        Set<ZSetOperations.TypedTuple<String>> tasks = redisTemplate.opsForZSet().reverseRangeWithScores(keyName,0,49);
        for(ZSetOperations.TypedTuple<String> entry : tasks){
            FeeTask feeTask = new FeeTask();
            long score = new Double(entry.getScore()).longValue();
            String value = entry.getValue();
            long index = this.获取用户排名(pkId,value);
            String taskId = DynamicKeyName.getTaskId(creator.getUserId(),value);
            feeTask.setTaskId(taskId);
            feeTask.setPkId(pkId);
            feeTask.setCreator(creator);
            feeTask.setCashier(userService.queryUser(value));
            feeTask.setIndex(index);
            feeTask.setIntegral(score);
            feeTask.setStatu(new KeyNameValue(OrderStatu.无订单.getStatu(),OrderStatu.无订单.getStatuStr()));


            //初始化打赏订单
            PayOrderEntity payOrderEntity =  userInfoService.查询可用订单Entity(feeTask.getPkId(),feeTask.getCreator().getUserId(),feeTask.getCashier().getUserId());
            feeTask.setOrderId(payOrderEntity.getOrderId());
            if(!org.apache.commons.lang.ObjectUtils.equals(payOrderEntity.getOrderStatu(),OrderStatu.无订单))
            {
                daoService.deleteEntity(payOrderEntity);
            }

            redisTemplate.opsForHash().put(DynamicKeyName.getMapKey_Value_Name(DynamicItem.PK当前任务,pkId),taskId,JSON.toJSONString(feeTask));

        }

    }


    public List<FeeTask> 获取PK打赏任务(String pkId){
        List<FeeTask> feeTasks = new ArrayList<>();
        String keyName = DynamicKeyName.getMapKey_Value_Name(DynamicItem.PK当前任务,pkId);
        Map<Object, Object> tasks =  redisTemplate.opsForHash().entries(keyName);
        if(MapUtils.isEmpty(tasks)){
            return feeTasks;
        }
        for(Map.Entry<Object, Object> entry : tasks.entrySet()){
            String value = (String)entry.getValue();
            FeeTask feeTask = JSON.parseObject(value,FeeTask.class);
            feeTasks.add(feeTask);
        }
        return feeTasks;
    }

    public void 清理所有任务(String pkId){
        List<FeeTask> tasks = 获取PK打赏任务(pkId);
//        if(!isFinishTask(tasks)){return;}
        for(FeeTask feeTask:tasks){
            feeTask.getCreator();
            feeTask.getPkId();
            PayOrderEntity payOrderEntity =  userInfoService.查询可用订单Entity(feeTask.getPkId(),feeTask.getCreator().getUserId(),feeTask.getCashier().getUserId());

            daoService.deleteEntity(payOrderEntity);
        }
        redisTemplate.delete(DynamicKeyName.getMapKey_Value_Name(DynamicItem.PK当前任务,pkId));
    }


    public void 同步任务状态(PayOrderEntity order){
        if(org.apache.commons.lang.ObjectUtils.equals(order.getOrderType(),OrderType.审核订单)){return;}
        if(org.apache.commons.lang.ObjectUtils.equals(order.getOrderStatu(),OrderStatu.无订单)){return;}

        String taskId = DynamicKeyName.getTaskId(order.getPayerId(),order.getCashierId());
        String feeTaskStr = (String)redisTemplate.opsForHash().get(DynamicKeyName.getMapKey_Value_Name(DynamicItem.PK当前任务,order.getPkId()),taskId);
        FeeTask feeTask = JSON.parseObject(feeTaskStr,FeeTask.class);
        if(ObjectUtils.isEmpty(feeTask)){return;}
        feeTask.setStatu(new KeyNameValue(order.getOrderStatu().getStatu(),order.getOrderStatu().getStatuStr()));
        redisTemplate.opsForHash().put(DynamicKeyName.getMapKey_Value_Name(DynamicItem.PK当前任务,order.getPkId()),taskId,JSON.toJSONString(feeTask));


        if(org.apache.commons.lang.ObjectUtils.equals(order.getOrderStatu(),OrderStatu.已收款)){
            this.收款用户积分清零(order.getPkId(),order.getCashierId());
            //发消息 -  检查是否完成任务。

        }
    }


    private void 收款用户积分清零(String pkId, String cashierId) {
        this.delMapKeyValue(DynamicItem.PKUSER今日分享次数,pkId,cashierId);
        this.更新今日用户排名(pkId,cashierId);
    }



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

    public void 新增注册用户(String appName, String pkId,String userId, String fromUser) {
        if(StringUtils.isBlank(fromUser)){return;}
        this.mapValueIncr(DynamicItem.PKUSER今日分享次数,pkId,fromUser);
        this.更新今日用户排名(pkId,fromUser);






    }

    public void 添加操作动态(String pkId,String userId,OperType operType){
        FactualInfo factualInfo = new FactualInfo();
        factualInfo.setFactualId(UUID.randomUUID().toString());
        factualInfo.setUser(userService.queryUser(userId));
        factualInfo.setOperType(new KeyNameValue(operType.getKey(),operType.getValue()));
        factualInfo.setTime(String.valueOf(System.currentTimeMillis()));
        this.添加动态(pkId,factualInfo);
    }


    public void 修改PK模式(String pkId, int i) {
        setKeyValue(DynamicItem.PK模式,pkId,i);
    }

    public PkMode getPK模式(String pkId) {
        int mode = getKeyValue(DynamicItem.PK模式,pkId);
        if(mode == PkMode.打赏模式.getKey()){return PkMode.打赏模式;}
        else {return PkMode.推广模式;}
    }




    public String 获取过期订单() {
        String keyName = DynamicItem.PK订单过期时间列表.getRedisKeySuffix();
        return redisTemplate.opsForSet().pop(keyName);
    }

    public void 添加过期订单(String orderId) {
        String keyName = DynamicItem.PK订单过期时间列表.getRedisKeySuffix();
        redisTemplate.opsForSet().add(keyName,orderId);
    }


















    public void 确认收款(PayOrderEntity order) {

        User creator = pkService.queryPkCreator(order.getPkId());
        if(StringUtils.equals(creator.getUserId(),order.getCashierId()))
        {
            //推广模式
            this.valueIncr(DynamicItem.PK总推广次数,order.getPkId());
            int pkTimes = this.valueIncr(DynamicItem.PK本次周期推广次数,order.getPkId());


            //一轮推广次数
            int times = this.getKeyValue(DynamicItem.配置,"SINGLE_CYCLE_VERIFY_TIMES");

            if(times == 0){
                this.设置周期最大值();
                times = this.getKeyValue(DynamicItem.配置,"SINGLE_CYCLE_VERIFY_TIMES");
            }

            if(pkTimes >= times){
                //转为打赏模式
                this.生成PK打赏任务(order.getPkId());
                //任务模式
                this.修改PK模式(order.getPkId(),PkMode.打赏模式.getKey());
            }
        }
        else
        {
            //打赏模式
            this.valueIncr(DynamicItem.PK总打赏次数,order.getPkId());
            int pkTimes = this.valueIncr(DynamicItem.PK本次周期打赏次数,order.getPkId());

            int times = this.getKeyValue(DynamicItem.配置,"SINGLE_CYCLE_FEE_TIMES");

            if(times == 0){
                this.设置周期最大值();
                times = this.getKeyValue(DynamicItem.配置,"SINGLE_CYCLE_FEE_TIMES");
            }
            if(pkTimes >= times){
                this.修改PK模式(order.getPkId(), PkMode.推广模式.getKey());
                this.清理所有任务(order.getPkId());

                this.setKeyValue(DynamicItem.PK本次周期推广次数,order.getPkId(),0);
                this.setKeyValue(DynamicItem.PK本次周期打赏次数,order.getPkId(),0);
            }

        }

    }


    public void 设置周期最大值() {
        setKeyValue(DynamicItem.配置,"SINGLE_CYCLE_VERIFY_TIMES",1);
        setKeyValue(DynamicItem.配置,"SINGLE_CYCLE_FEE_TIMES",1);
    }


}
