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


        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);
        if(org.springframework.util.ObjectUtils.isEmpty(userDynamicEntity)){
            userDynamicEntity = new UserDynamicEntity();
            userDynamicEntity.setDynamicId(UUID.randomUUID().toString());
            userDynamicEntity.setPkId(pkId);
            userDynamicEntity.setUserId(userId);
            userDynamicEntity.setFeeImgStatu(ImgStatu.无内容);
            daoService.insertEntity(userDynamicEntity);
        }

        UserCode userCode = new UserCode();
        userCode.setFeeNum(pkService.查询Pk打赏金额(pkId));
        userCode.setPkId(pkId);
        userCode.setUser(userService.queryUser(userId));
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








    public UserCode 设置收款码(String pkId,String userId, String url) throws AppException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);

        if(userDynamicEntity.getFeeImgStatu() == ImgStatu.审核中 || userDynamicEntity.getFeeImgStatu() == ImgStatu.审核通过 || userDynamicEntity.getFeeImgStatu() == ImgStatu.审核不通过){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"订单已锁定"));
        }

        userDynamicEntity.setFeePayUrl(url);
        userDynamicEntity.setFeeImgStatu(ImgStatu.待审核);
        daoService.updateEntity(userDynamicEntity);



        UserCode userCode = new UserCode();
        userCode.setFeeNum(pkService.查询Pk打赏金额(pkId));
        userCode.setPkId(userDynamicEntity.getPkId());
        userCode.setUser(userService.queryUser(userDynamicEntity.getUserId()));
        userCode.setPhone(userDynamicEntity.getPhone());
        userCode.setStatu(new KeyNameValue(userDynamicEntity.getFeeImgStatu().getKey(),userDynamicEntity.getFeeImgStatu().getValue()));
        userCode.setUrl(userDynamicEntity.getFeePayUrl());



        dynamicService.添加操作动态(pkId,userId,OperType.上传收款码);


        return userCode;

    }

    public PayOrderEntity 获取ApplyOrderEntity(String pkId,String payerId,String cashierId){

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("payerId",CompareTag.Equal,payerId)
                .andFilter()
                .compareFilter("cashierId",CompareTag.Equal, cashierId)
                ;

        PayOrderEntity applyOrderEntity = daoService.querySingleEntity(PayOrderEntity.class,filter);

        return applyOrderEntity;
    }

    public PayOrderEntity 获取OrderEntityById(String orderId){

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                .compareFilter("orderId",CompareTag.Equal,orderId);
                ;
        PayOrderEntity applyOrderEntity = daoService.querySingleEntity(PayOrderEntity.class,filter);

        return applyOrderEntity;
    }

    public ApplyOrder 查询审核订单(String pkId,String userId,String cashierId){

        int feeNum = pkService.查询Pk打赏金额(pkId);
        String creatorId = pkService.querySinglePkEntity(pkId).getUserId();
        UserCode userCode = this.查询收款码信息(pkId,creatorId);
        ApplyOrder applyOrder = new ApplyOrder();
        applyOrder.setType(new KeyNameValue(OrderType.审核订单.getType(),OrderType.审核订单.getTitle()));
        applyOrder.setFeeNum(feeNum);
        applyOrder.setPkId(pkId);
        applyOrder.setCashier(userService.queryUser(creatorId));
        applyOrder.setFeeCodeUrl(org.springframework.util.ObjectUtils.isEmpty(userCode)?"":userCode.getUrl());
        PayOrderEntity applyOrderEntity = this.获取ApplyOrderEntity(pkId,userId,creatorId);
        if(!org.springframework.util.ObjectUtils.isEmpty(applyOrderEntity)){
            applyOrder.setOrderId(applyOrderEntity.getOrderId());
            applyOrder.setPayer(userService.queryUser(applyOrderEntity.getPayerId()));
            applyOrder.setComplain(applyOrderEntity.isComplain());
            applyOrder.setOrderCut(applyOrderEntity.getOrderCut());
            applyOrder.setStatu(new KeyNameValue(applyOrderEntity.getOrderStatu().getStatu(), applyOrderEntity.getOrderStatu().getStatuStr()));


        }
        else
        {
            applyOrder.setStatu(new KeyNameValue(OrderStatu.无订单.getStatu(),OrderStatu.无订单.getStatuStr()));
        }

        return applyOrder;



    }

    public void 设置订单截图(String orderId, String userId, String url) throws AppException {

        PayOrderEntity payOrderEntity = 获取OrderEntityById(orderId);
        if(org.springframework.util.ObjectUtils.isEmpty(payOrderEntity)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"不存在订单"));}
        if(!StringUtils.equals(userId,payOrderEntity.getPayerId())){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非支付用户"));}
        if(!ObjectUtils.equals(payOrderEntity.getOrderStatu(),OrderStatu.无订单)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"订单已锁定"));}
        payOrderEntity.setOrderCut(url);
        daoService.updateEntity(payOrderEntity);

    }



    public void 确定创建订单(String orderId, String userId) throws AppException {
        PayOrderEntity payOrderEntity = 获取OrderEntityById(orderId);
        if(!StringUtils.equals(userId,payOrderEntity.getPayerId())){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法用户"));}
        if(org.springframework.util.ObjectUtils.isEmpty(payOrderEntity)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"不存在订单"));}
        if(!StringUtils.equals(userId,payOrderEntity.getPayerId())){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非支付用户"));}
        if(!ObjectUtils.equals(payOrderEntity.getOrderStatu(),OrderStatu.无订单)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"订单已锁定"));}
        payOrderEntity.setOrderStatu(OrderStatu.订单确认中);
        daoService.updateEntity(payOrderEntity);
    }

    public ApplyOrder 查询收款类型订单(String pkId, String userId,int type,int page) throws AppException {
        int feeNum = pkService.查询Pk打赏金额(pkId);
        ApplyOrder applyOrder = new ApplyOrder();
        applyOrder.setFeeNum(feeNum);
        applyOrder.setPkId(pkId);
        applyOrder.setCashier(userService.queryUser(userId));
        PayOrderEntity orderEntity = this.获取收款类型订单Entity(pkId,userId,type,page);
        if(!org.springframework.util.ObjectUtils.isEmpty(orderEntity)){
            applyOrder.setOrderId(orderEntity.getOrderId());
            applyOrder.setType(new KeyNameValue(orderEntity.getOrderType().getType(),orderEntity.getOrderType().getTitle()));
            applyOrder.setStatu(new KeyNameValue(orderEntity.getOrderStatu().getStatu(), orderEntity.getOrderStatu().getStatuStr()));
            applyOrder.setPayer(userService.queryUser(orderEntity.getPayerId()));
            applyOrder.setComplain(orderEntity.isComplain());
            applyOrder.setOrderCut(orderEntity.getOrderCut());
        }
        else
        {
            applyOrder.setStatu(new KeyNameValue(OrderStatu.无订单.getStatu(), OrderStatu.无订单.getStatuStr()));
        }

        return applyOrder;

    }



    public ApplyOrder 查询打赏订单(String pkId, String userId) throws AppException {
        User creator = pkService.queryPkCreator(pkId);
        int feeNum = pkService.查询Pk打赏金额(pkId);
        ApplyOrder applyOrder = new ApplyOrder();
        applyOrder.setFeeNum(feeNum);
        applyOrder.setPkId(pkId);
        applyOrder.setCashier(userService.queryUser(userId));
        PayOrderEntity orderEntity = this.查询可用订单Entity(pkId,userId,creator.getUserId());

        applyOrder.setOrderId(orderEntity.getOrderId());
        applyOrder.setStatu(new KeyNameValue(orderEntity.getOrderStatu().getStatu(), orderEntity.getOrderStatu().getStatuStr()));
        applyOrder.setType(new KeyNameValue(orderEntity.getOrderType().getType(),orderEntity.getOrderType().getTitle()));
        applyOrder.setComplain(orderEntity.isComplain());
        applyOrder.setPayer(userService.queryUser(orderEntity.getPayerId()));
        applyOrder.setOrderCut(orderEntity.getOrderCut());
        applyOrder.setRewardTimes(dynamicService.getMapKeyValue(DynamicItem.PKUSER收款次数,pkId,userId));
        return applyOrder;

    }




    public void 确认已收款(String orderId, String userId) throws AppException {
        PayOrderEntity order = this.获取OrderEntityById(orderId);
        //操作者必须为收款人
        if(!StringUtils.equals(order.getCashierId(),userId)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}
        if(!ObjectUtils.equals(order.getOrderStatu(),OrderStatu.订单确认中)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"订单已锁定"));}


        order.setOrderStatu(OrderStatu.已收款);
        order.setConfirmTime(System.currentTimeMillis());
        daoService.updateEntity(order);
        //用户收到的钱次数+1
        dynamicService.mapValueIncr(DynamicItem.PKUSER收款次数,order.getPkId(),order.getCashierId());


        User creator = pkService.queryPkCreator(order.getPkId());
        if(!StringUtils.equals(creator.getUserId(),order.getCashierId()))
        {
            //收款人不是榜主
            dynamicService.valueIncr(DynamicItem.PK完成打赏订单次数,order.getPkId());
        }
        else {
            //收款人是榜主
            dynamicService.valueIncr(DynamicItem.PK完成审核订单次数,order.getPkId());
            this.设置收款码状态(order.getPkId(),order.getPayerId(),ImgStatu.审核中);
        }




    }

    private void 设置收款码状态(String pkId, String payerId, ImgStatu statu) {

            EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("userId",CompareTag.Equal,payerId);
            UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);
            userDynamicEntity.setFeeImgStatu(ImgStatu.审核中);
            daoService.updateEntity(userDynamicEntity);

    }


    public void 确认未收款(String orderId, String userId) {
        PayOrderEntity order = this.获取OrderEntityById(orderId);
        if(ObjectUtils.equals(order.getOrderStatu(),OrderStatu.订单确认中)){
            order.setOrderStatu(OrderStatu.未收款);
            daoService.updateEntity(order);
        }
    }

    public ApplyOrder 查询订单ById(String orderId) {
        PayOrderEntity order = this.获取OrderEntityById(orderId);

        ApplyOrder applyOrder = translate(order);

        dynamicService.同步任务状态(order);

        return applyOrder;
    }

    public ApplyOrder translate(PayOrderEntity order){
        ApplyOrder payOrder = new ApplyOrder();
        payOrder.setOrderId(order.getOrderId());
        payOrder.setPkId(order.getPkId());
        int feeNum = pkService.查询Pk打赏金额(order.getPkId());
        payOrder.setFeeNum(feeNum);
        payOrder.setCashier(userService.queryUser(order.getCashierId()));
        UserCode userCode = this.查询收款码信息(order.getPkId(),order.getCashierId());
        payOrder.setFeeCodeUrl(org.springframework.util.ObjectUtils.isEmpty(userCode)?"":userCode.getUrl());
        payOrder.setOrderCut(order.getOrderCut());
        payOrder.setPayer(userService.queryUser(order.getPayerId()));
        payOrder.setComplain(order.isComplain());
        payOrder.setOrderCut(order.getOrderCut());
        payOrder.setStatu(new KeyNameValue(order.getOrderStatu().getStatu(), order.getOrderStatu().getStatuStr()));
        payOrder.setType(new KeyNameValue(order.getOrderType().getType(),order.getOrderType().getTitle()));
        return payOrder;
    }


    public ApplyOrder 查询支付类型订单(String pkId, String userId, int type, int page) {
        int feeNum = pkService.查询Pk打赏金额(pkId);
        ApplyOrder applyOrder = new ApplyOrder();
        applyOrder.setFeeNum(feeNum);
        applyOrder.setPkId(pkId);
        applyOrder.setPayer(userService.queryUser(userId));

        PayOrderEntity orderEntity = this.获取支付类型订单Entity(pkId,userId,type,page);

        if(!org.springframework.util.ObjectUtils.isEmpty(orderEntity)){
            applyOrder.setOrderId(orderEntity.getOrderId());
            applyOrder.setStatu(new KeyNameValue(orderEntity.getOrderStatu().getStatu(), orderEntity.getOrderStatu().getStatuStr()));
            applyOrder.setCashier(userService.queryUser(orderEntity.getCashierId()));
            applyOrder.setComplain(orderEntity.isComplain());
            applyOrder.setOrderCut(orderEntity.getOrderCut());
            UserCode userCode = this.查询收款码信息(pkId,orderEntity.getCashierId());
            applyOrder.setFeeCodeUrl(org.springframework.util.ObjectUtils.isEmpty(userCode)?"":userCode.getUrl());
        }
        else
        {
            applyOrder.setStatu(new KeyNameValue(OrderStatu.无订单.getStatu(), OrderStatu.无订单.getStatuStr()));
        }
        return applyOrder;
    }

    private PayOrderEntity 获取支付类型订单Entity(String pkId, String userId, int type, int page) {
        EntityFilterChain filter = null;
        if(type == OrderStatu.已收款.getStatu()){
            filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("payerId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.已收款)
                    .pageLimitFilter(page,1);
        }
        else if(type == OrderStatu.未收款.getStatu()){
            filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("payerId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.未收款)
                    .pageLimitFilter(page,1);;
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("payerId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.订单确认中)
                    .pageLimitFilter(page,1);;
        }

        List<PayOrderEntity> payOrderEntities = daoService.queryEntities(PayOrderEntity.class,filter);

        return CollectionUtils.isEmpty(payOrderEntities)?null:payOrderEntities.get(0);
    }


    private PayOrderEntity 获取收款类型订单Entity(String pkId, String userId, int type,int page) {
        EntityFilterChain filter = null;
        if(type == OrderStatu.已收款.getStatu()){
            filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("cashierId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.已收款)
                    .pageLimitFilter(page,1);
        }
        else if(type == OrderStatu.未收款.getStatu()){
            filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("cashierId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.未收款)
                    .pageLimitFilter(page,1);;
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("cashierId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.订单确认中)
                    .pageLimitFilter(page,1);;
        }

        List<PayOrderEntity> payOrderEntities = daoService.queryEntities(PayOrderEntity.class,filter);

        return CollectionUtils.isEmpty(payOrderEntities)?null:payOrderEntities.get(0);
    }



    public ApplyOrder 查询或创建订单(String pkId, String userId,String cashierId){

        PayOrderEntity applyOrderEntity = this.查询可用订单Entity(pkId,userId,cashierId);
        ApplyOrder applyOrder = translate(applyOrderEntity);

        return applyOrder;
    }

    private PayOrderEntity 创建可用订单(String pkId, String userId, String cashierId) {
        String creatorId = pkService.querySinglePkEntity(pkId).getUserId();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(com.union.app.util.idGenerator.IdGenerator.getOrderId());
        payOrderEntity.setPkId(pkId);
        payOrderEntity.setCashierId(cashierId);
        payOrderEntity.setPayerId(userId);
        payOrderEntity.setOrderStatu(OrderStatu.无订单);
        payOrderEntity.setComplain(false);
        if(StringUtils.equals(cashierId,creatorId)){
            payOrderEntity.setOrderType(OrderType.审核订单);
        }
        else
        {
            payOrderEntity.setOrderType(OrderType.打赏订单);
        }
        daoService.insertEntity(payOrderEntity);
        return payOrderEntity;
    }


    public PayOrderEntity 查询可用订单Entity(String pkId, String payerId,String cashierId){

        PayOrderEntity applyOrderEntity = this.获取ApplyOrderEntity(pkId,payerId,cashierId);

        if(org.springframework.util.ObjectUtils.isEmpty(applyOrderEntity)){
            applyOrderEntity = this.创建可用订单(pkId,payerId,cashierId);
        }
        return applyOrderEntity;

    }


    public ApproveCode 查询不同类型用户收款码(String pkId, String userId, int type, int page) throws AppException {
        int feeNum = pkService.查询Pk打赏金额(pkId);
        String creator = pkService.querySinglePkEntity(pkId).getUserId();
        if(!StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}


        ApproveCode approveCode = new ApproveCode();
        approveCode.setFeeNum(feeNum);
        approveCode.setPkId(pkId);

        UserDynamicEntity userDynamicEntity = this.查询下一个UserDynamicEntity(pkId,type,page);
        if(org.springframework.util.ObjectUtils.isEmpty(userDynamicEntity))
        {
            approveCode.setStatu(new KeyNameValue(ImgStatu.无内容.getKey(), ImgStatu.无内容.getValue()));
        }
        else
        {
            approveCode.setDynamicId(userDynamicEntity.getDynamicId());
            approveCode.setStatu(new KeyNameValue(userDynamicEntity.getFeeImgStatu().getKey(),userDynamicEntity.getFeeImgStatu().getValue()));
            approveCode.setPhone(userDynamicEntity.getPhone());
            approveCode.setUrl(userDynamicEntity.getFeePayUrl());
            approveCode.setUser(userService.queryUser(userDynamicEntity.getUserId()));
        }

        
        return approveCode;



    }

    private UserDynamicEntity 查询下一个UserDynamicEntity(String pkId, int type, int page) {
        EntityFilterChain filter = null;
        if(type == ImgStatu.审核中.getKey()){
            filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("feeImgStatu",CompareTag.Equal, ImgStatu.审核中)
                    .pageLimitFilter(page,1);
        }
        else if(type == ImgStatu.审核通过.getKey()){
            filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("feeImgStatu",CompareTag.Equal, ImgStatu.审核通过)
                    .pageLimitFilter(page,1);;
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("feeImgStatu",CompareTag.Equal, ImgStatu.审核不通过)
                    .pageLimitFilter(page,1);
        }
        List<UserDynamicEntity> userDynamicEntities = daoService.queryEntities(UserDynamicEntity.class,filter);
        return CollectionUtils.isEmpty(userDynamicEntities)?null:userDynamicEntities.get(0);

    }





    public void 审核通过(String dynamicId, String userId) throws AppException {

        UserDynamicEntity userDynamicEntity = this.查询UserDynamicEntityById(dynamicId);

        String creator = pkService.querySinglePkEntity(userDynamicEntity.getPkId()).getUserId();
        if(!StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}

        userDynamicEntity.setFeeImgStatu(ImgStatu.审核通过);
        daoService.insertEntity(userDynamicEntity);


    }


    public void 审核不通过(String dynamicId, String userId) throws AppException
    {
        UserDynamicEntity userDynamicEntity = this.查询UserDynamicEntityById(dynamicId);
        String creator = pkService.querySinglePkEntity(userDynamicEntity.getPkId()).getUserId();
        if(!StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}

        userDynamicEntity.setFeeImgStatu(ImgStatu.审核不通过);
        daoService.insertEntity(userDynamicEntity);

    }

    private UserDynamicEntity 查询UserDynamicEntityById(String dynamicId) {

        EntityFilterChain filter  = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("dynamicId",CompareTag.Equal,dynamicId);

        UserDynamicEntity userDynamicEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);

        return userDynamicEntity;
    }


    public ApproveCode 查询ApproveCodeById(String dynamicId) {

        ApproveCode approveCode = new ApproveCode();


        UserDynamicEntity userDynamicEntity = this.查询UserDynamicEntityById(dynamicId);

        approveCode.setDynamicId(userDynamicEntity.getDynamicId());
        approveCode.setPkId(userDynamicEntity.getPkId());
        approveCode.setFeeNum(pkService.查询Pk打赏金额(userDynamicEntity.getPkId()));
        approveCode.setStatu(new KeyNameValue(userDynamicEntity.getFeeImgStatu().getKey(),userDynamicEntity.getFeeImgStatu().getValue()));
        approveCode.setPhone(userDynamicEntity.getPhone());
        approveCode.setUrl(userDynamicEntity.getFeePayUrl());
        approveCode.setUser(userService.queryUser(userDynamicEntity.getUserId()));

        return approveCode;

    }



    public boolean 用户是否具有有效收款码(String pkId, String userId) {
        UserDynamicEntity userDynamicEntity = this.查询用户收款码表(pkId,userId);
        if(org.springframework.util.ObjectUtils.isEmpty(userDynamicEntity)){return Boolean.FALSE;}
        if(userDynamicEntity.getFeeImgStatu() != ImgStatu.审核通过){return Boolean.FALSE;}
        return Boolean.TRUE;

    }

    public KeyNameValue 查询当前打赏订单状态(String pkId, String payerId, String cashierId) {

        PayOrderEntity payOrderEntity = this.查询可用订单Entity(pkId,payerId,cashierId);

        return new KeyNameValue(payOrderEntity.getOrderStatu().getStatu(),payOrderEntity.getOrderStatu().getStatuStr());


    }
}
