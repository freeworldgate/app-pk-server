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
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.*;
import com.union.app.domain.user.User;
import com.union.app.entity.ImgStatu;
import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.UserDynamicEntity;
import com.union.app.entity.pk.apply.OrderType;
import com.union.app.entity.pk.apply.PayOrderEntity;
import com.union.app.entity.pk.审核.ApproveCommentEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicItem;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class ApproveService {


    @Autowired
    AppDaoService daoService;

    @Autowired
    UserService userService;


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;


    public ApproveUser 查询帖子的审核用户(String pkId, String postId,Date date) throws UnsupportedEncodingException {

        String approveUserId = dynamicService.查询审核用户(pkId,postId,date);

        if(StringUtils.isBlank(approveUserId))
        {
            return null;
        }
        else
        {
            ApproveUser approveUser = new ApproveUser();
            User user = userService.queryUser(approveUserId);
            ApproveMessage approveMessage = this.获取审核人员消息(pkId,approveUserId,date);
            ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,approveUserId,date);
            approveUser.setUser(user);
            approveUser.setApproveDynamic(approveDynamic);
            approveUser.setApproveMessage(approveMessage);
            ApproveComplain approveComplain = this.获取投诉信息(pkId,postId,approveUserId);
            ApproveComment approveComment = this.获取留言信息(pkId,postId,approveUserId);

            approveUser.setApproveComment(approveComment);
            approveUser.setApproveComplain(approveComplain);

            return approveUser;
        }

    }


    public List<ApproveUser> 查询今日所有审核用户(String pkId, String postId,Date date) throws UnsupportedEncodingException {
        List<ApproveUser> approveUsers = new ArrayList<>();
        List<UserIntegral> approvers = dynamicService.查询今日审核用户列表(pkId,date);

        for(UserIntegral userIntegral:approvers)
        {
            ApproveUser approveUser = new ApproveUser();
            approveUser.setRole(userIntegral.isCreator()?1:0);
            User user = userService.queryUser(userIntegral.getUser().getUserId());
            ApproveMessage approveMessage = this.获取审核人员消息(pkId,userIntegral.getUser().getUserId(),date);
            ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,userIntegral.getUser().getUserId(),date);

            approveUser.setUser(user);
            approveUser.setApproveDynamic(approveDynamic);
            approveUser.setApproveMessage(approveMessage);
            ApproveComplain approveComplain = this.获取投诉信息(pkId,postId,userIntegral.getUser().getUserId());
            ApproveComment approveComment = this.获取留言信息(pkId,postId,userIntegral.getUser().getUserId());

            approveUser.setApproveComment(approveComment);
            approveUser.setApproveComplain(approveComplain);
            approveUsers.add(approveUser);

        }
        return approveUsers;
    }







    public ApproveComment 获取留言信息(String pkId, String postId, String approveUserId) throws UnsupportedEncodingException {
        ApproveComment approveComment = new ApproveComment();

        ApproveCommentEntity approveCommentEntity = this.查询留言(pkId,postId,approveUserId);
        if(org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity)){return null;}
        approveComment.setImgUrl(approveCommentEntity.getImgUrl());
        approveComment.setPkId(approveCommentEntity.getPkId());
        approveComment.setPostId(approveCommentEntity.getPostId());
        approveComment.setText(new String(approveCommentEntity.getText(),"UTF-8"));
        approveComment.setApproverId(approveCommentEntity.getApproverId());


        return approveComment;
    }

    private ApproveComplain 获取投诉信息(String pkId, String postId, String approveUserId) {

        return null;
    }

    private ApproveDynamic 获取审核人员动态信息(String pkId,String userId,Date date) {

        ApproveDynamic approveDynamic = new ApproveDynamic();

        UserIntegral userIntegral =  dynamicService.查询审核用户信息(pkId,userId,date);

        approveDynamic.setYesterdayRank(userIntegral.getIndex());

        approveDynamic.setYesterdayScore(userIntegral.getFollow() + userIntegral.getShare());

        approveDynamic.setApproving(userIntegral.getApproving());

        approveDynamic.setApproved(userIntegral.getApproved());

        return approveDynamic;
    }

    /**
     * 查询审核人消息
     * @param approveUserId
     * @return
     */
    private ApproveMessage 获取审核人员消息(String pkId, String approveUserId,Date date) {

        ApproveMessage approveMessage = dynamicService.查询审核用户的消息(pkId,approveUserId,date);

        return approveMessage;
    }




    public void 设置帖子的审核用户(String pkId, String postId, String approvorId,Date date) {

        dynamicService.设置帖子的审核用户(pkId,postId,approvorId,date);

    }

    public void 设置审核留言(String pkId, String postId, String approveUserId,String text,String imgUrl) throws UnsupportedEncodingException {

        ApproveCommentEntity approveCommentEntity = 查询留言(pkId,postId,approveUserId);

        if(org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity))
        {

            approveCommentEntity = new ApproveCommentEntity();
            approveCommentEntity.setApproverId(approveUserId);
            approveCommentEntity.setPkId(pkId);
            approveCommentEntity.setPostId(postId);
            approveCommentEntity.setText(text.getBytes("UTF-8"));
            approveCommentEntity.setImgUrl(this.getLegalImgUrl(imgUrl));
            approveCommentEntity.setTime(System.currentTimeMillis());
            daoService.insertEntity(approveCommentEntity);
        }
        else
        {
            approveCommentEntity.setApproverId(approveUserId);
            approveCommentEntity.setPkId(pkId);
            approveCommentEntity.setPostId(postId);
            approveCommentEntity.setText(text.getBytes("UTF-8"));
            approveCommentEntity.setImgUrl(this.getLegalImgUrl(imgUrl));
            approveCommentEntity.setTime(System.currentTimeMillis());
            daoService.updateEntity(approveCommentEntity);
        }

    }

    public ApproveCommentEntity 查询留言(String pkId, String postId, String approveUserId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ApproveCommentEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postId)
                .andFilter()
                .compareFilter("approverId",CompareTag.Equal,approveUserId);
        ApproveCommentEntity approveCommentEntity = daoService.querySingleEntity(ApproveCommentEntity.class,filter);
        return approveCommentEntity;
    }

    private String getLegalImgUrl(String image) {
        String reg2 = "]";
        String reg1 = "\\[";
        String reg3 = "\"";
        image = image.replaceAll(reg3,"").replaceAll(reg1,"").replaceAll(reg2,"").trim();
        return image;
    }

    public ApproveUser 查询审核用户ById(String pkId,String approverUserId,Date date) {
        ApproveUser approveUser = new ApproveUser();
        User user = userService.queryUser(approverUserId);
        ApproveMessage approveMessage = this.获取审核人员消息(pkId,approverUserId,date);
        ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,approverUserId,date);

        approveUser.setUser(user);
        approveUser.setApproveDynamic(approveDynamic);
        approveUser.setApproveMessage(approveMessage);
        return approveUser;
    }


    public ApproveUser 查询审核用户WidthCommentById(String pkId,String postId,String approverUserId,Date date) throws UnsupportedEncodingException {
        ApproveUser approveUser = new ApproveUser();
        User user = userService.queryUser(approverUserId);
        ApproveMessage approveMessage = this.获取审核人员消息(pkId,approverUserId,date);
        ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,approverUserId,date);
        ApproveComment approveComment = this.获取留言信息(pkId,postId,approverUserId);
        approveUser.setUser(user);
        approveUser.setApproveDynamic(approveDynamic);
        approveUser.setApproveMessage(approveMessage);
        approveUser.setApproveComment(approveComment);
        return approveUser;
    }


    public int 今日打榜总人数(String pkId,Date date) {

        return dynamicService.今日打榜总人数(pkId,date);

    }

    public int 计算管理员设置人数(String pkId) {
        return 10;
    }



}
