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

        this.订单是否过期(applyOrderEntity);

        return applyOrderEntity;
    }

    public PayOrderEntity 获取OrderEntityById(String orderId){

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                .compareFilter("orderId",CompareTag.Equal,orderId);
                ;
        PayOrderEntity applyOrderEntity = daoService.querySingleEntity(PayOrderEntity.class,filter);


        this.订单是否过期(applyOrderEntity);


        return applyOrderEntity;
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
        //订单开始时间，计时5分钟
        payOrderEntity.setStartTime(System.currentTimeMillis());
        daoService.updateEntity(payOrderEntity);
    }


    public ApplyOrder 查询审核支付订单(String pkId, String userId) throws AppException {

        ApplyOrder applyOrder = new ApplyOrder();

        PayOrderEntity orderEntity = this.查询榜主收款订单(pkId,userId);

        if(org.springframework.util.ObjectUtils.isEmpty(orderEntity))
        {
            applyOrder.setFeeNum(pkService.查询Pk打赏金额(pkId));
            applyOrder.setPkId(pkId);
            applyOrder.setCashier(userService.queryUser(userId));
            applyOrder.setStatu(new KeyNameValue(OrderStatu.无订单.getStatu(), OrderStatu.无订单.getStatuStr()));
            applyOrder.setRewardTimes(dynamicService.getMapKeyValue(DynamicItem.PKUSER收款次数,pkId,userId));

        }
        else
        {
            applyOrder = this.translate(orderEntity);
        }

        return applyOrder;

    }

    private PayOrderEntity 查询榜主收款订单(String pkId, String userId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("cashierId",CompareTag.Equal, userId)
                    .andFilter()
                    .compareFilter("orderStatu",CompareTag.Equal, OrderStatu.订单确认中);
        PayOrderEntity orderEntity = daoService.querySingleEntity(PayOrderEntity.class,filter);

        this.订单是否过期(orderEntity);

        return orderEntity;

    }
    private PayOrderEntity 查询用户打赏订单(String pkId, String payerId, String cashierId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PayOrderEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("payerId",CompareTag.Equal, payerId)
                .andFilter()
                .compareFilter("cashierId",CompareTag.Equal, cashierId);

        PayOrderEntity orderEntity = daoService.querySingleEntity(PayOrderEntity.class,filter);

        this.订单是否过期(orderEntity);

        return orderEntity;


    }


    public ApplyOrder 查询打赏订单(String pkId, String userId) throws AppException {
        ApplyOrder applyOrder = new ApplyOrder();

        User creator = pkService.queryPkCreator(pkId);

        PayOrderEntity orderEntity = this.查询用户打赏订单(pkId,creator.getUserId(),userId);

        if(org.springframework.util.ObjectUtils.isEmpty(orderEntity))
        {
            applyOrder.setPkId(pkId);
            applyOrder.setFeeNum(pkService.查询Pk打赏金额(pkId));
            applyOrder.setCashier(userService.queryUser(userId));
            applyOrder.setStatu(new KeyNameValue(OrderStatu.无订单.getStatu(), OrderStatu.无订单.getStatuStr()));
            applyOrder.setRewardTimes(dynamicService.getMapKeyValue(DynamicItem.PKUSER收款次数,pkId,userId));
        }
        else
        {
            applyOrder = this.translate(orderEntity);
        }








        return applyOrder;

    }


    private void 订单是否过期(PayOrderEntity orderEntity) {
        if(!org.springframework.util.ObjectUtils.isEmpty(orderEntity) && ObjectUtils.equals(orderEntity.getOrderStatu(),OrderStatu.订单确认中)){
            int seconds = this.获取订单剩余时间(orderEntity);
            if(seconds == 0)
            {
                dynamicService.添加过期订单(orderEntity.getOrderId());
            }
        }

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

        dynamicService.确认收款(order);

        this.设置收款码状态(order);


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

    public void 确认未收款(String orderId, String userId) throws AppException {
        PayOrderEntity order = this.获取OrderEntityById(orderId);
        if(ObjectUtils.equals(order.getOrderStatu(),OrderStatu.订单确认中)){
            order.setOrderStatu(OrderStatu.未收款);
            daoService.updateEntity(order);
        }
        else
        {
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"订单已锁定"));
        }
    }

    public ApplyOrder 查询订单ById(String orderId) throws AppException {
        if(StringUtils.isBlank(orderId)){ throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"不存在订单"));}
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
        payOrder.setLeftTimes(获取订单剩余时间(order));
        payOrder.setRewardTimes(dynamicService.getMapKeyValue(DynamicItem.PKUSER收款次数,order.getPkId(),order.getCashierId()));
        return payOrder;
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



    public ApproveCode 查询不同类型用户收款码(String pkId, String userId) throws AppException {
        int feeNum = pkService.查询Pk打赏金额(pkId);
        String creator = pkService.querySinglePkEntity(pkId).getUserId();
        if(!StringUtils.equals(userId,creator)){throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}


        ApproveCode approveCode = new ApproveCode();
        this.审核动态(pkId,approveCode);
        approveCode.setFeeNum(feeNum);
        approveCode.setPkId(pkId);

        UserDynamicEntity userDynamicEntity = this.查询下一个UserDynamicEntity(pkId);
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


    public ApproveCode 查询ApproveCodeById(String dynamicId) {

        ApproveCode approveCode = new ApproveCode();
        UserDynamicEntity userDynamicEntity = this.查询UserDynamicEntityById(dynamicId);
        this.审核动态(userDynamicEntity.getPkId(),approveCode);
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



    public void 订单超时(String orderId) throws AppException {
        PayOrderEntity orderEntity = this.获取OrderEntityById(orderId);
        int leftTime = this.获取订单剩余时间(orderEntity);
        if(leftTime == 0){
            this.确认已收款(orderId,orderEntity.getCashierId());
        }


    }

    public int 获取订单剩余时间(PayOrderEntity orderEntity) {
        long leftMiniSeconds = System.currentTimeMillis() - orderEntity.getStartTime();
        int leftSeconds = (new Long(leftMiniSeconds).intValue()/1000);
        int seconds = AppConfigService.getConfigAsInteger(常量值.单个订单的超时时间,5 * 60) - leftSeconds;
        return seconds < 0?0:seconds;
    }
}
