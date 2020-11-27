package com.union.app.service.pk.complain;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.complain.Complain;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkPostListEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComplainService {



    @Autowired
    AppDaoService daoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PkService pkService;


    public Complain 查询Complain投诉信息(String pkId, String userId) {
        ComplainEntity complainEntity = 查询投诉信息(pkId,userId);
        if(!ObjectUtils.isEmpty(complainEntity))
        {
            Complain complain = new Complain();
            complain.setComplainId(complainEntity.getId());
            complain.setStatu(complainEntity.getComplainStatu().getStatuStr());
            complain.setText(complainEntity.getText());
            complain.setUrl(complainEntity.getUrl());
            complain.setUser(userService.queryUser(complainEntity.getUserId()));
            complain.setPkId(complainEntity.getPkId());
            complain.setPostId(complainEntity.getPostId());
            complain.setTime(TimeUtils.convertTime(complainEntity.getTime()));
            return complain;
        }
        return null;

    }
    
    public ComplainEntity 查询投诉信息(String pkId, String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        ComplainEntity complainEntity = daoService.querySingleEntity(ComplainEntity.class,filter);
        return complainEntity;
    }

    public void 添加投诉(String pkId, String userId,String text,String url) throws AppException {

        ComplainEntity complainEntity = this.查询投诉信息(pkId,userId);
        PostEntity postEntity = postService.查询用户帖(pkId, userId);
        if(ObjectUtils.isEmpty(complainEntity))
        {


            PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
            complainEntity = new ComplainEntity();

            if(!ObjectUtils.isEmpty(postEntity))
            {
                if(postEntity.getShareTime()>0)
                {
                    complainEntity.setActive(true);
                }
            }



            complainEntity.setText(text);
            complainEntity.setUserId(userId);
            complainEntity.setPostStatu(ObjectUtils.isEmpty(postEntity)?null:postEntity.getStatu());
            complainEntity.setPkId(pkId);
            complainEntity.setPostId(ObjectUtils.isEmpty(postEntity)?null:postEntity.getPostId());
            complainEntity.setUrl(url);
//            complainEntity.setPkType(pkEntity.getPkType());
            complainEntity.setComplainStatu(ComplainStatu.处理中);
            complainEntity.setTime(System.currentTimeMillis());
            daoService.insertEntity(complainEntity);
            dynamicService.valueIncr(CacheKeyName.投诉,pkId);

        }
        else
        {
            if(!ObjectUtils.isEmpty(postEntity))
            {
                if(postEntity.getShareTime()>0 && postEntity.getShareTime()>complainEntity.getTime())
                {
                    complainEntity.setActive(true);
                }
            }


            complainEntity.setPostStatu(ObjectUtils.isEmpty(postEntity)?null:postEntity.getStatu());
            complainEntity.setUrl(url);
            complainEntity.setText(text);
            complainEntity.setTime(System.currentTimeMillis());
            daoService.updateEntity(complainEntity);
        }
















    }
    public List<Complain> 查询投诉信息(String pkId,int page) {
        List<Complain> complains = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("time",OrderTag.DESC);
        List<ComplainEntity> complainEntities = daoService.queryEntities(ComplainEntity.class,filter);
        if(!CollectionUtils.isEmpty(complainEntities))
        {
            complainEntities.forEach(complainEntity -> {
                Complain complain = new Complain();
                complain.setComplainId(complainEntity.getId());
                complain.setStatu(complainEntity.getComplainStatu().getStatuStr());
                complain.setText(complainEntity.getText());
                complain.setUrl(complainEntity.getUrl());
                complain.setUser(userService.queryUser(complainEntity.getUserId()));
                complain.setPkId(complainEntity.getPkId());
                complain.setPostId(complainEntity.getPostId());
                complain.setTime(TimeUtils.convertTime(complainEntity.getTime()));
                complains.add(complain);

            });

        }

        return complains;
    }

//    public boolean 用户是否可以收款(String pkId,String userId,int tag){
//        return dynamicService.getMapKeyValue(DynamicItem.PKUSER用户投诉,pkId,userId) == 0;
//    }
//
//
//
//    public void 处理投诉(String id,String userId,int tag){
//        ComplainEntity complainEntity = 查询投诉信息(id);
//        if(complainEntity.getComplainType() == ComplainType.收款码投诉){
//            UserDynamicEntity userDynamicEntity = userInfoService.查询用户收款码表ById(id);
//            if(tag == 1)
//            {
//                //投诉方的问题   榜主没问题
//                User creator = pkService.queryPkCreator(userDynamicEntity.getPkId());
//                complainEntity.setStatu(ComplainStatu.已处理);
//                dynamicService.mapValueDecr(DynamicItem.PKUSER用户投诉,userDynamicEntity.getPkId(),creator.getUserId());
//            }
//            else
//            {
//                complainEntity.setStatu(ComplainStatu.已处理);
//                dynamicService.mapValueDecr(DynamicItem.PKUSER用户投诉,userDynamicEntity.getPkId(),userDynamicEntity.getUserId());
//            }
//        }
//        else
//        {
//
//            PayOrderEntity payOrderEntity = userInfoService.获取OrderEntityById(id);
//            if(tag == 1)
//            {
//                //投诉方的问题
//                complainEntity.setStatu(ComplainStatu.已处理);
//                dynamicService.mapValueDecr(DynamicItem.PKUSER用户投诉,payOrderEntity.getPkId(),payOrderEntity.getPayerId());
//            }
//            else
//            {
//                complainEntity.setStatu(ComplainStatu.已处理);
//                dynamicService.mapValueDecr(DynamicItem.PKUSER用户投诉,payOrderEntity.getPkId(),payOrderEntity.getCashierId());
//
//            }
//
//        }
//
//
//
//
//
//
//
//
//
//
//    }
//
//
//    public void 新增审核投诉(String id, String userId) {
//
//
//
//        UserDynamicEntity userDynamicEntity = userInfoService.查询用户收款码表ById(id);
//        if(!org.apache.commons.lang.StringUtils.equals(userDynamicEntity.getUserId(),userId)){return;}
//
//
//        ComplainEntity complainEntity = new ComplainEntity();
//        complainEntity.setId(id);
//        complainEntity.setComplainType(ComplainType.收款码投诉);
//        complainEntity.setStatu(ComplainStatu.处理中);
//        complainEntity.setTime(System.currentTimeMillis());
//
//        daoService.insertEntity(complainEntity);
//
//        //投诉榜主
//        User creator = pkService.queryPkCreator(userDynamicEntity.getPkId());
//
//
//        dynamicService.用户投诉新增(userDynamicEntity.getPkId(),creator.getUserId(),userId);
//
//
//
//    }
//
//    public void 新增收款投诉(String id, String userId) {
//        PayOrderEntity payOrderEntity = userInfoService.获取OrderEntityById(id);
//        if(!org.apache.commons.lang.StringUtils.equals(payOrderEntity.getPayerId(),userId)){return;}
//
//        ComplainEntity complainEntity = new ComplainEntity();
//        complainEntity.setId(id);
//        complainEntity.setComplainType(ComplainType.订单收款投诉);
//        complainEntity.setStatu(ComplainStatu.处理中);
//        complainEntity.setTime(System.currentTimeMillis());
//        daoService.insertEntity(complainEntity);
//
//        //投诉收款人
//        dynamicService.用户投诉新增(payOrderEntity.getPkId(),payOrderEntity.getPayerId(),payOrderEntity.getCashierId());
//
//    }
//
//    public ComplainEntity 查询投诉信息(String id){
//        EntityFilterChain filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
//                .compareFilter("id",CompareTag.Equal,id);
//        ComplainEntity complainEntity = daoService.querySingleEntity(ComplainEntity.class,filter);
//        return complainEntity;
//
//    }
//
//    public ComplainEntity 查询未处理投诉信息(int type){
//
//        EntityFilterChain filter = null;
//        if(type == ComplainType.收款码投诉.getStatu()){
//            filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
//                    .compareFilter("statu",CompareTag.Equal,ComplainStatu.处理中)
//                    .andFilter()
//                    .compareFilter("complainType",CompareTag.Equal,ComplainType.收款码投诉);
//        }
//        else
//        {
//            filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
//                    .compareFilter("statu",CompareTag.Equal,ComplainStatu.处理中)
//                    .andFilter()
//                    .compareFilter("complainType",CompareTag.Equal,ComplainType.订单收款投诉);
//        }
//
//
//        ComplainEntity complainEntity = daoService.querySingleEntity(ComplainEntity.class,filter);
//        return complainEntity;
//
//    }
//
//
//    public Complain 下一个投诉信息(int type) throws AppException {
//        Complain complain = new Complain();
//        ComplainEntity complainEntity = 查询未处理投诉信息(type);
//        complain.setId(complainEntity.getId());
//        if(ObjectUtils.isEmpty(complainEntity)){return null;}
//
//        if(complainEntity.getComplainType() == ComplainType.收款码投诉){
//            UserCode userCode = userInfoService.查询收款码信息(complainEntity.getId());
//            complain.setUserCode(userCode);
//            complain.setComplainType(ComplainType.收款码投诉);
//
//        }
//        else
//        {
//            ApplyOrder applyOrder = userInfoService.查询订单ById(complainEntity.getId());
//            complain.setApplyOrder(applyOrder);
//            complain.setComplainType(ComplainType.订单收款投诉);
//
//        }
//
//        return complain;
//    }
}
