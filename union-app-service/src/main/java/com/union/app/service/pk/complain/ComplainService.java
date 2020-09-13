package com.union.app.service.pk.complain;

import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.complain.Complain;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    public void 新增审核投诉(String id, String userId) {
    }

    public ComplainEntity 查询投诉信息(String id) {
        return null;
    }

    public void 新增收款投诉(String id, String userId) {
    }

    public Complain 下一个投诉信息(int type) {
        return null;
    }

    public void 查询投诉信息(String pkId, String approverUserId, String userId) {
    }

    public void 添加投诉(String pkId, String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        ComplainEntity complainEntity = daoService.querySingleEntity(ComplainEntity.class,filter);
        if(ObjectUtils.isEmpty(complainEntity))
        {
            ComplainEntity newComplain = new ComplainEntity();

            newComplain.setUserId(userId);
            newComplain.setPkId(pkId);
            newComplain.setComplainStatu(ComplainStatu.处理中);
            daoService.insertEntity(newComplain);
        }
















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
