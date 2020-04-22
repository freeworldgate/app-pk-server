package com.union.app.service.pk.service;

import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.OperType;
import com.union.app.domain.pk.UserCode;
import com.union.app.domain.pk.apply.ApplyOrder;
import com.union.app.domain.pk.apply.ApproveCode;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.UserDynamicEntity;
import com.union.app.entity.pk.apply.OrderType;
import com.union.app.entity.pk.apply.PayOrderEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicItem;
import com.union.app.service.pk.dynamic.DynamicKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.UUID;

@Service
public class UserInfoService {


    @Autowired
    AppDaoService daoService;

    @Autowired
    UserService userService;


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;


    public UserCode 查询并创建收款码信息(String pkId,String userId) {
        UserCode userCode = new UserCode();

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);
        if(org.springframework.util.ObjectUtils.isEmpty(userDynamicEntity)){
            userDynamicEntity = new UserDynamicEntity();
            userDynamicEntity.setDynamicId(com.union.app.util.idGenerator.IdGenerator.生成收款码ID());
            userDynamicEntity.setPkId(pkId);
            userDynamicEntity.setUserId(userId);
            userDynamicEntity.setFeeImgStatu(ImgStatu.无内容);
            daoService.insertEntity(userDynamicEntity);
        }

        userCode.setDynamicId(userDynamicEntity.getDynamicId());
        userCode.setFeeNum(pkService.查询Pk打赏金额(pkId));
        userCode.setPkId(pkId);
        userCode.setUser(userService.queryUser(userId));
        userCode.setPhone(userDynamicEntity.getPhone());
        userCode.setStatu(new KeyNameValue(userDynamicEntity.getFeeImgStatu().getKey(),userDynamicEntity.getFeeImgStatu().getValue()));
        userCode.setUrl(userDynamicEntity.getFeePayUrl());

        return userCode;

    }
    public UserCode 查询收款码信息(String dynamicId) {
        UserCode userCode = new UserCode();


        UserDynamicEntity userDynamicEntity = this.查询用户收款码表ById(dynamicId);


        userCode.setDynamicId(userDynamicEntity.getDynamicId());
        userCode.setFeeNum(pkService.查询Pk打赏金额(userDynamicEntity.getPkId()));
        userCode.setPkId(userDynamicEntity.getPkId());
        userCode.setUser(userService.queryUser(userDynamicEntity.getUserId()));
        userCode.setPhone(userDynamicEntity.getPhone());
        userCode.setStatu(new KeyNameValue(userDynamicEntity.getFeeImgStatu().getKey(),userDynamicEntity.getFeeImgStatu().getValue()));
        userCode.setUrl(userDynamicEntity.getFeePayUrl());

        return userCode;

    }




    public UserDynamicEntity 查询用户收款码表(String pkId,String userId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);
        return userDynamicEntity;
    }

    public UserDynamicEntity 查询用户收款码表ById(String dynamicId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("dynamicId",CompareTag.Equal,dynamicId);
        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);
        return userDynamicEntity;
    }


    public UserCode 查询收款码信息(String pkId,String userId) {


        UserDynamicEntity userDynamicEntity = 查询用户收款码表(pkId,userId);

        if(org.springframework.util.ObjectUtils.isEmpty(userDynamicEntity)){return null;}

        UserCode userCode = new UserCode();
        userCode.setFeeNum(pkService.查询Pk打赏金额(pkId));
        userCode.setPkId(userDynamicEntity.getPkId());
        userCode.setUser(userService.queryUser(userDynamicEntity.getUserId()));
        userCode.setPhone(userDynamicEntity.getPhone());
        userCode.setStatu(new KeyNameValue(userDynamicEntity.getFeeImgStatu().getKey(),userDynamicEntity.getFeeImgStatu().getValue()));
        userCode.setUrl(userDynamicEntity.getFeePayUrl());

        return userCode;

    }



    private String praseStr(String phone) throws AppException {
        if(StringUtils.isBlank(phone)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"无效号码"));}
        if(phone.length() != 11){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"11位手机号码"));}

        return phone;
    }

    public void 设置手机号码(String pkId,String userId, String phone) throws AppException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);

//        if(userDynamicEntity.getFeeImgStatu() == ImgStatu.审核通过){
//            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"订单已锁定"));
//        }
        userDynamicEntity.setPhone(praseStr(phone));
        daoService.updateEntity(userDynamicEntity);



    }















    private void 设置收款码状态(PayOrderEntity order) {

        User creator = pkService.queryPkCreator(order.getPkId());

        if(StringUtils.equals(creator.getUserId(),order.getCashierId()))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,order.getPkId())
                    .andFilter()
                    .compareFilter("userId",CompareTag.Equal,order.getPayerId());
            UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);
            userDynamicEntity.setFeeImgStatu(ImgStatu.审核中);
            daoService.updateEntity(userDynamicEntity);
        }
        dynamicService.valueIncr(DynamicItem.PK审核中收款码数量,order.getPkId());




    }




    private void 审核动态(String pkId,ApproveCode approveCode) {

        approveCode.setApproved(dynamicService.getKeyValue(DynamicItem.PK审核通过收款码数量,pkId));
        approveCode.setApproving(dynamicService.getKeyValue(DynamicItem.PK审核中收款码数量,pkId));
        approveCode.setUnapproved(dynamicService.getKeyValue(DynamicItem.PK审核不通过收款码数量,pkId));

    }

    private UserDynamicEntity 查询下一个UserDynamicEntity(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("feeImgStatu",CompareTag.Equal, ImgStatu.审核中);


        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);


        return userDynamicEntity;

    }





    public void 审核通过(String dynamicId, String userId) throws AppException {

        UserDynamicEntity userDynamicEntity = this.查询UserDynamicEntityById(dynamicId);

        String creator = pkService.querySinglePkEntity(userDynamicEntity.getPkId()).getUserId();
        if(!StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}

        userDynamicEntity.setFeeImgStatu(ImgStatu.审核通过);
        daoService.insertEntity(userDynamicEntity);

        dynamicService.valueDecr(DynamicItem.PK审核中收款码数量,userDynamicEntity.getPkId());
        dynamicService.valueIncr(DynamicItem.PK审核通过收款码数量,userDynamicEntity.getPkId());



    }


    public void 审核不通过(String dynamicId, String userId) throws AppException
    {
        UserDynamicEntity userDynamicEntity = this.查询UserDynamicEntityById(dynamicId);
        String creator = pkService.querySinglePkEntity(userDynamicEntity.getPkId()).getUserId();
        if(!StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}

        userDynamicEntity.setFeeImgStatu(ImgStatu.审核不通过);
        daoService.insertEntity(userDynamicEntity);

        dynamicService.valueDecr(DynamicItem.PK审核中收款码数量,userDynamicEntity.getPkId());
        dynamicService.valueIncr(DynamicItem.PK审核不通过收款码数量,userDynamicEntity.getPkId());


    }

    private UserDynamicEntity 查询UserDynamicEntityById(String dynamicId) {

        EntityFilterChain filter  = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("dynamicId",CompareTag.Equal,dynamicId);

        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);

        return userDynamicEntity;
    }






}
