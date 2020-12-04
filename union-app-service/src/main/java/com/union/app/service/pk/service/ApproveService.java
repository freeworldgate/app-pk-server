package com.union.app.service.pk.service;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.ApproveButton;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.*;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.审核.ApproveCommentEntity;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.domain.pk.审核.ApproveMessage;
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

    @Autowired
    PkCacheService pkCacheService;




//
//    public ApproveComment 获取留言信息(String pkId, String postId) throws UnsupportedEncodingException {
//        ApproveComment approveComment = new ApproveComment();
//        approveComment.setImgBack(appService.查询背景(4));
//
//
//        ApproveCommentEntity approveCommentEntity = this.查询留言(pkId,postId);
//        if(org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity)){return null;}
//        approveComment.setImgUrl(approveCommentEntity.getImgUrl());
//        approveComment.setPkId(approveCommentEntity.getPkId());
//        approveComment.setPostId(approveCommentEntity.getPostId());
//        approveComment.setText(approveCommentEntity.getText());
//        approveComment.setCreator(pkService.queryPkCreator(pkId));
//        approveComment.setUser(userService.queryUser(approveCommentEntity.getUserId()));
////        approveComment.setTime("今天23:00");
//        approveComment.setDate(TimeUtils.convertTime(approveCommentEntity.getTime()));
//        if(!org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity.getPostStatu()))
//        {
//            approveComment.setStatu(new KeyNameValue(approveCommentEntity.getPostStatu().getStatu(), approveCommentEntity.getPostStatu().getStatuStr()));
//        }
//        else
//        {
//            approveComment.setStatu(new KeyNameValue(PostStatu.审核中.getStatu(), PostStatu.审核中.getStatuStr()));
//        }
//
//        return approveComment;
//    }

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






//    public void 设置审核留言(String pkId, String postId,String userId,String text,String imgUrl) throws UnsupportedEncodingException {
//
//        ApproveCommentEntity approveCommentEntity = 查询留言(pkId,postId);
//
//        if(org.springframework.util.ObjectUtils.isEmpty(approveCommentEntity))
//        {
//            approveCommentEntity = new ApproveCommentEntity();
//            approveCommentEntity.setPkId(pkId);
//            approveCommentEntity.setPostId(postId);
//            approveCommentEntity.setUserId(userId);
//            approveCommentEntity.setText(text);
//            approveCommentEntity.setImgUrl(this.getLegalImgUrl(imgUrl));
//            approveCommentEntity.setTime(System.currentTimeMillis());
//
//            approveCommentEntity.setPostStatu(PostStatu.审核中);
//            daoService.insertEntity(approveCommentEntity);
//        }
//        else
//        {
//            approveCommentEntity.setText(text);
//            approveCommentEntity.setImgUrl(this.getLegalImgUrl(imgUrl));
//            approveCommentEntity.setUserId(userId);
//            approveCommentEntity.setTime(System.currentTimeMillis());
//
//            approveCommentEntity.setPostStatu(PostStatu.审核中);
//            daoService.updateEntity(approveCommentEntity);
//        }
//
//    }

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


    public ApproveMessage translate(ApproveMessageEntity approveMessageEntity) throws UnsupportedEncodingException {
        ApproveMessage approveMessage = new ApproveMessage();
//        approveMessage.setUser(userService.queryUser(approveMessageEntity.getApproverUserId()));
        approveMessage.setText(approveMessageEntity.getText());
        approveMessage.setImgeUrl(ImageUtils.添加水印(approveMessageEntity.getImgUrl()));
        approveMessage.setTime(TimeUtils.convertTime(approveMessageEntity.getTime()));
        approveMessage.setMediaId(approveMessageEntity.getMediaId());
        return approveMessage;

    }

    public ApproveMessageEntity 获取审核人员消息Entity(String pkId) throws UnsupportedEncodingException {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(ApproveMessageEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
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

        boolean policy1 = AppConfigService.getConfigAsBoolean(ConfigItem.遗传相册);
        boolean policy2 = AppConfigService.getConfigAsBoolean(ConfigItem.内置相册);
        boolean policy3 = AppConfigService.getConfigAsBoolean(ConfigItem.普通相册);

//        boolean policy4 = AppConfigService.getConfigAsBoolean(ConfigItem.VIP用户);
//        boolean policy5 = AppConfigService.getConfigAsBoolean(ConfigItem.普通用户);



//        if((pkEntity.getPkType()==PkType.内置相册 && policy2) || (pkEntity.getPkType()==PkType.运营相册 && policy1) || (pkEntity.getPkType()==PkType.审核相册 && policy3) )
//        {
//
//            return ApproveButton.转发审核群;
//        }




        PostEntity postEntity = postService.查询帖子ById(postId);
//        if(postEntity.getApproveStatu() == ApproveStatu.请求审核 ){
//            return ApproveButton.请求审核无效 ;
//        }
        return ApproveButton.请求审核有效;

    }
}
