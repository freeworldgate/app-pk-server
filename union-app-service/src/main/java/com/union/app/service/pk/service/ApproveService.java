package com.union.app.service.pk.service;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.ApproveButton;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.*;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.审核.ApproveCommentEntity;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.entity.用户.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.ImageUtils;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Autowired
    PostService postService;

    @Autowired
    AppService appService;

    @Autowired
    MediaService mediaService;

    public ApproveUser 查询帖子的审核用户(String pkId, String postId,Date date) throws IOException {

        String approveUserId = dynamicService.查询审核用户(pkId,postId);

        if(StringUtils.isBlank(approveUserId))
        {
            return null;
        }
        else
        {
            ApproveUser approveUser = new ApproveUser();
            User user = userService.queryUser(approveUserId);
            ApproveMessage approveMessage = this.获取审核人员消息(pkId);
            ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,approveUserId,date);
            approveUser.setUser(user);
            approveUser.setApproveDynamic(approveDynamic);
            approveUser.setApproveMessage(approveMessage);
            ApproveComplain approveComplain = this.获取投诉信息(pkId,postId,approveUserId);
            ApproveComment approveComment = this.获取留言信息(pkId,postId);

            approveUser.setApproveComment(approveComment);
            approveUser.setApproveComplain(approveComplain);

            return approveUser;
        }

    }

//
//    public List<ApproveUser> 查询当前可用户审核用户列表(String pkId, String postId,Date date) throws UnsupportedEncodingException {
//        List<ApproveUser> approveUsers = new ArrayList<>();
//        List<UserIntegral> approvers = dynamicService.查询今日审核用户列表(pkId);
//
//        for(UserIntegral userIntegral:approvers)
//        {
//            ApproveUser approveUser = new ApproveUser();
//            approveUser.setRole(userIntegral.isCreator()?1:0);
//            User user = userService.queryUser(userIntegral.getUser().getUserId());
//            ApproveMessage approveMessage = this.获取审核人员消息(pkId,userIntegral.getUser().getUserId(),date);
//            ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,userIntegral.getUser().getUserId(),date);
//
//            approveUser.setUser(user);
//            approveUser.setApproveDynamic(approveDynamic);
//            approveUser.setApproveMessage(approveMessage);
//            ApproveComplain approveComplain = this.获取投诉信息(pkId,postId,userIntegral.getUser().getUserId());
//            ApproveComment approveComment = this.获取留言信息(pkId,postId,userIntegral.getUser().getUserId());
//
//            approveUser.setApproveComment(approveComment);
//            approveUser.setApproveComplain(approveComplain);
//            if(!org.springframework.util.ObjectUtils.isEmpty(approveMessage))
//            {
//                approveUsers.add(approveUser);
//            }
//        }
//        return approveUsers;
//    }

//
//    public List<ApproveUser> 查询当前户审核用户列表(String pkId,Date date) throws UnsupportedEncodingException {
//        List<ApproveUser> approveUsers = new ArrayList<>();
//        List<UserIntegral> userIntegrals = dynamicService.查询今日审核用户列表(pkId);
//
//        for(UserIntegral userIntegral:userIntegrals)
//        {
//            ApproveUser approveUser = new ApproveUser();
//            approveUser.setUserIntegral(userIntegral);
//            ApproveMessage approveMessage = this.获取审核人员消息(pkId,userIntegral.getUser().getUserId(),date);
//            ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,userIntegral.getUser().getUserId(),date);
//            approveUser.setApproveDynamic(approveDynamic);
//            approveUser.setUser(userIntegral.getUser());
//            approveUser.setApproveMessage(approveMessage);
//            approveUsers.add(approveUser);
//
//        }
//        return approveUsers;
//    }
//






    public ApproveComment 获取留言信息(String pkId, String postId) throws UnsupportedEncodingException {
        ApproveComment approveComment = new ApproveComment();
        approveComment.setImgBack(appService.查询背景(4));


        ApproveCommentEntity approveCommentEntity = this.查询留言(pkId,postId);
        if(org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity)){return null;}
        approveComment.setImgUrl(approveCommentEntity.getImgUrl());
        approveComment.setPkId(approveCommentEntity.getPkId());
        approveComment.setPostId(approveCommentEntity.getPostId());
        approveComment.setText(approveCommentEntity.getText());
        approveComment.setUser(userService.queryUser(approveCommentEntity.getUserId()));
        approveComment.setTime("今天23:00");
        approveComment.setDate(approveCommentEntity.getDate());
        if(!org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity.getPostStatu()))
        {
            approveComment.setStatu(new KeyNameValue(approveCommentEntity.getPostStatu().getStatu(), approveCommentEntity.getPostStatu().getStatuStr()));
        }
        else
        {
            approveComment.setStatu(new KeyNameValue(PostStatu.审核中.getStatu(), PostStatu.审核中.getStatuStr()));
        }

        return approveComment;
    }

    private ApproveComplain 获取投诉信息(String pkId, String postId, String approveUserId) {

        return null;
    }

    private ApproveDynamic 获取审核人员动态信息(String pkId,String userId,Date date) {

        ApproveDynamic approveDynamic = new ApproveDynamic();

//        UserIntegral userIntegral =  dynamicService.查询审核用户信息(pkId,userId,date);
//
//        approveDynamic.setYesterdayRank(userIntegral.getIndex());
//
//        approveDynamic.setYesterdayScore(userIntegral.getFollow() + userIntegral.getShare());
//
//        approveDynamic.setApproving(userIntegral.getApproving());
//
//        approveDynamic.setApproved(userIntegral.getApproved());

        return approveDynamic;
    }






    public void 设置审核留言(String pkId, String postId,String userId,String text,String imgUrl) throws UnsupportedEncodingException {

        ApproveCommentEntity approveCommentEntity = 查询留言(pkId,postId);

        if(org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity))
        {
            approveCommentEntity = new ApproveCommentEntity();
            approveCommentEntity.setPkId(pkId);
            approveCommentEntity.setPostId(postId);
            approveCommentEntity.setUserId(userId);
            approveCommentEntity.setText(text);
            approveCommentEntity.setImgUrl(this.getLegalImgUrl(imgUrl));
            approveCommentEntity.setTime(System.currentTimeMillis());
            approveCommentEntity.setDate(TimeUtils.currentDateTime());
            approveCommentEntity.setPostStatu(PostStatu.审核中);
            daoService.insertEntity(approveCommentEntity);
        }
        else
        {
            approveCommentEntity.setText(text);
            approveCommentEntity.setImgUrl(this.getLegalImgUrl(imgUrl));
            approveCommentEntity.setUserId(userId);
            approveCommentEntity.setTime(System.currentTimeMillis());
            approveCommentEntity.setDate(TimeUtils.currentDateTime());
            approveCommentEntity.setPostStatu(PostStatu.审核中);
            daoService.updateEntity(approveCommentEntity);
        }

    }

    public ApproveCommentEntity 查询留言(String pkId, String postId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ApproveCommentEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("postId",CompareTag.Equal,postId)
                ;
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

    public ApproveUser 查询审核用户ById(String pkId,String approverUserId,Date date) throws IOException {
        ApproveUser approveUser = new ApproveUser();
        User user = userService.queryUser(approverUserId);
        ApproveMessage approveMessage = this.获取审核人员消息(pkId);
        ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,approverUserId,date);

        approveUser.setUser(user);
        approveUser.setApproveDynamic(approveDynamic);
        approveUser.setApproveMessage(approveMessage);
        return approveUser;
    }


    public ApproveUser 查询审核用户WidthCommentById(String pkId,String postId,String approverUserId,Date date) throws IOException {
        ApproveUser approveUser = new ApproveUser();
        User user = userService.queryUser(approverUserId);
        ApproveMessage approveMessage = this.获取审核人员消息(pkId);
        ApproveDynamic approveDynamic = this.获取审核人员动态信息(pkId,approverUserId,date);
        ApproveComment approveComment = this.获取留言信息(pkId,postId);
        approveUser.setUser(user);
        approveUser.setApproveDynamic(approveDynamic);
        approveUser.setApproveMessage(approveMessage);
        approveUser.setApproveComment(approveComment);
        return approveUser;
    }


    public int 今日打榜总人数(String pkId,Date date) {

//        return dynamicService.今日打榜总人数(pkId,date);
        return 0;
    }



    /**
     * 查询审核人消息
     * @return
     */
    public ApproveMessage 获取审核人员消息(String pkId) throws IOException {



        ApproveMessageEntity approveMessageEntity = 获取审核人员消息Entity(pkId);
        if(!org.springframework.util.ObjectUtils.isEmpty(approveMessageEntity)) {

            return translate(approveMessageEntity);
        }
        else
        {
            return null;
        }
    }

//    /**
//     * 查询审核人消息
//     * @param approveUserId
//     * @return
//     */
//    public ApproveMessage 获取审核人员消息(String pkId, String approveUserId, Date date) throws UnsupportedEncodingException {
//
//
////        dynamicService.查询审核用户的消息(pkId,approveUserId,date);
//
//        ApproveMessageEntity approveMessageEntity = 获取审核人员消息Entity(pkId);
//        if(!org.springframework.util.ObjectUtils.isEmpty(approveMessageEntity)) {
//            return translate(approveMessageEntity);
//        }
//        else
//        {
//            return null;
//        }
//    }
    public ApproveMessage translate(ApproveMessageEntity approveMessageEntity) throws UnsupportedEncodingException {
        ApproveMessage approveMessage = new ApproveMessage();
        approveMessage.setUser(userService.queryUser(approveMessageEntity.getApproverUserId()));
        approveMessage.setText(approveMessageEntity.getText());
        approveMessage.setImgeUrl(ImageUtils.添加水印(approveMessageEntity.getImgUrl()));
        approveMessage.setTime(TimeUtils.convertTime(approveMessageEntity.getTime()));
        approveMessage.setMediaId(approveMessageEntity.getMediaId());
//        approveMessage.setDate(approveMessageEntity.getDate());
        return approveMessage;

    }

    public ApproveMessageEntity 获取审核人员消息Entity(String pkId) throws UnsupportedEncodingException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(ApproveMessageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
//                .andFilter()
//                .compareFilter("date",CompareTag.Equal,TimeUtils.dateStr(date));
        ApproveMessageEntity approveMessageEntity = daoService.querySingleEntity(ApproveMessageEntity.class,filter);
        return approveMessageEntity;
    }




    public ApproveMessage 发布审核员消息(String pkId, String userId, String text, String imgUrl) throws AppException, IOException {

        if(!pkService.isPkCreator(pkId,userId)){
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }


        ApproveMessageEntity approveMessageEntity = this.获取审核人员消息Entity(pkId);

        if(org.springframework.util.ObjectUtils.isEmpty(approveMessageEntity)){

            String messageId = UUID.randomUUID().toString();


            String mediaId = WeChatUtil.uploadImg2Wx(imgUrl);
            approveMessageEntity = new ApproveMessageEntity();
            approveMessageEntity.setPkId(pkId);
            approveMessageEntity.setMsgId(messageId);
            approveMessageEntity.setApproverUserId(userId);
//            approveMessageEntity.setDate(TimeUtils.dateStr(currentDate));
            approveMessageEntity.setTime(System.currentTimeMillis());
            approveMessageEntity.setImgUrl(imgUrl);
            approveMessageEntity.setText(text);
            approveMessageEntity.setMediaId(mediaId);

            daoService.insertEntity(approveMessageEntity);

        }
        else
        {

            String mediaId = WeChatUtil.uploadImg2Wx(imgUrl);
            approveMessageEntity.setMsgId(StringUtils.isBlank(approveMessageEntity.getMsgId())?UUID.randomUUID().toString():approveMessageEntity.getMsgId());
            approveMessageEntity.setImgUrl(imgUrl);
            approveMessageEntity.setText(text);
            approveMessageEntity.setMediaId(mediaId);
//            appService.修改激活处理的状态(pkId);
            daoService.updateEntity(approveMessageEntity);
        }







        return translate(approveMessageEntity);

    }


    public String 计算预审核员排名边界(int sortNum) {
//            根据打榜总人数来确定前100%的边界值
            int range = sortNum / 10;

            if(range == 0)
            {
                return "仅榜主";
            }

            if(range == 1)
            {
                return "top-1";
            }
            return "1~" + range;
    }



    public List<ApproveUser> 查询当前可用户审核用户列表(String pkId, String postId) {

        return new ArrayList<>();
    }
    public ApproveMessage 查询PK公告消息(String pkId) throws IOException {

        ApproveMessage approveMessage = this.获取审核人员消息(pkId);
        return approveMessage;

    }

    public boolean 是否已发布审核消息(String pkId) throws UnsupportedEncodingException {
        ApproveMessageEntity approveMessageEntity = this.获取审核人员消息Entity(pkId);
        return !org.springframework.util.ObjectUtils.isEmpty(approveMessageEntity);
    }

    public ApproveButton 获取审核按钮(String pkId,String postId, String userId) {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        int policy2 = AppConfigService.getConfigAsInteger(ConfigItem.内置相册公开);
        int policy3 = AppConfigService.getConfigAsInteger(ConfigItem.内置相册邀请);
        int policy1 = AppConfigService.getConfigAsInteger(ConfigItem.遗传相册);
        boolean pkType = false;
        if((pkEntity.getPkType() == PkType.运营相册 && policy1==0) || (pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.公开 && policy2 == 0) ||(pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.邀请 && policy3 == 0)  )
        {
            pkType = true;
        }


        boolean userType = false;


        boolean isVipUser = userService.是否是遗传用户(userId);
        int policy4 = AppConfigService.getConfigAsInteger(ConfigItem.VIP用户);
        int policy5 = AppConfigService.getConfigAsInteger(ConfigItem.普通用户);

        if((isVipUser && policy4==0) || (!isVipUser && policy5==0) )
        {
            userType = true;
        }
        if(pkType && userType){return ApproveButton.转发审核群;}

        if(StringUtils.isBlank(dynamicService.查询审核用户(pkId,postId))){
            return ApproveButton.请求审核有效;
        }
        return ApproveButton.请求审核无效;

    }
}
