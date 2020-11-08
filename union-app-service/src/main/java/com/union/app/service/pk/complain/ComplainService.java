package com.union.app.service.pk.complain;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
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



    public ComplainEntity 查询投诉信息(String pkId, String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ComplainEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        ComplainEntity complainEntity = daoService.querySingleEntity(ComplainEntity.class,filter);
        return complainEntity;
    }

    public void 添加投诉(String pkId, String userId,String text) throws AppException {

        ComplainEntity complainEntity = this.查询投诉信息(pkId,userId);

        if(ObjectUtils.isEmpty(complainEntity))
        {
            PostEntity postEntity = postService.查询用户帖(pkId, userId);
            if(postEntity.getStatu() != PostStatu.上线)
            {

                if(userService.是否是遗传用户(userId)) {
                    PkPostListEntity pkPostListEntity = pkService.查询图册排列(pkId, postEntity.getPkId());
                    if (ObjectUtils.isEmpty(pkPostListEntity)) {
                        throw AppException.buildException(PageAction.信息反馈框("投诉失败", "发布图册后申请审核图册!"));
                    }
                    if (!dynamicService.审核等待时间过长(pkId, postEntity.getPostId())) {
                        throw AppException.buildException(PageAction.信息反馈框("投诉失败", "发布图册后，等待审核时间超过" + AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间) + "分钟后方可投诉!"));
                    }
                }

            }



            PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
            ComplainEntity newComplain = new ComplainEntity();

            newComplain.setText(text);
            newComplain.setUserId(userId);
            newComplain.setPostStatu(postEntity.getStatu());
            newComplain.setPkId(pkId);
            newComplain.setPkType(pkEntity.getPkType());
            newComplain.setComplainStatu(ComplainStatu.处理中);
            newComplain.setUpdateTime(System.currentTimeMillis());
            daoService.insertEntity(newComplain);
            dynamicService.valueIncr(CacheKeyName.投诉,pkId);

        }
        else
        {

             throw AppException.buildException(PageAction.信息反馈框("已投诉","已接受投诉,不能重复投诉..."));

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
