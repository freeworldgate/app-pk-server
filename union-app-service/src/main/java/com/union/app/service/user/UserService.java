package com.union.app.service.user;


import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.domain.pk.客服消息.WxImage;
import com.union.app.domain.pk.客服消息.WxSendMessage;
import com.union.app.domain.pk.客服消息.WxText;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.卡点.UserCardEntity;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.UserDynamicEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserService {

    @Autowired
    AppDaoService appDaoService;


    @Autowired
    DynamicService dynamicService;


    @Autowired
    PkCacheService pkCacheService;

    @Autowired
    UserService userService;




    public UserEntity queryUserEntity(String userId){
        if(StringUtils.isBlank(userId)||StringUtils.equalsIgnoreCase("null",userId)||StringUtils.equalsIgnoreCase("undefined",userId)||StringUtils.equalsIgnoreCase("Nan",userId))
        {
            return null;
        }


        UserEntity userEntity = EntityCacheService.getUserEntity(userId);
        if(ObjectUtils.isEmpty(userEntity))
        {
            if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("undefined",userId)|| org.apache.commons.lang.StringUtils.equalsIgnoreCase("null",userId)|| org.apache.commons.lang.StringUtils.equalsIgnoreCase("Nan",userId))
            {
                return null;
            }
            EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                    .compareFilter("userId",CompareTag.Equal,userId);
            userEntity = appDaoService.querySingleEntity(UserEntity.class,filter);
            EntityCacheService.saveUser(userEntity);
        }

        return userEntity;


    }

    public UserDynamicEntity queryUserKvEntity(String userId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userkvEntity = appDaoService.querySingleEntity(UserDynamicEntity.class,filter);

        return userkvEntity;
    }


    /**
     * 查询用户
     * @param userId
     * @return
     */
    public User queryUser(String userId)
    {
        if(org.apache.commons.lang.StringUtils.isBlank(userId))
        {
            return null;
        }


        UserEntity result  = queryUserEntity(userId);
//        UserKvEntity kv  = queryUserKvEntity(userId);
        if(!ObjectUtils.isEmpty(result))
        {
            User user = new User();
            user.setUserName(new String(result.getNickName()));
//            user.setUserName(RandomUtil.getRandomName());
            user.setUserId(result.getUserId());
            user.setUserType(ObjectUtils.isEmpty(result.getUserType())?UserType.普通用户.getType():result.getUserType().getType());
//            user.setImgUrl(result.getAvatarUrl());
//            user.setPkTimes(kv.getPkTimes());
//            user.setPostTimes(kv.getPostTimes());
//            user.setInviteTimes(kv.getInviteTimes());
            user.setImgUrl(result.getAvatarUrl());

            return user;
        }
        return null;
    }


    private boolean isUserVip(String userId){

            if(!this.isUserExist(userId)){return false;}
            //运营模式
            UserEntity user = this.queryUserEntity(userId);

            if(UserType.重点用户 == user.getUserType()){return true;}
            else{return false;}



    }




    public boolean 是否是遗传用户(String userId)
    {
        return this.isUserVip(userId);
    }






    public boolean isUserExist(String userId) {
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(userId,"null")){
            return false;
        }
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(userId,"undefined")){
            return false;
        }
        UserEntity user = this.queryUserEntity(userId);
        if(!ObjectUtils.isEmpty(user)){
            return true;
        }
        return false;
    }

    public void processMessage(Map<String,Object> msg,Date date) {



        String fromUserName = ObjectUtils.isEmpty(msg.get("FromUserName"))?"":msg.get("FromUserName").toString();;
        String sessionFrom = ObjectUtils.isEmpty(msg.get("SessionFrom"))?"":msg.get("SessionFrom").toString();
        String event = ObjectUtils.isEmpty(msg.get("Event"))?"":msg.get("Event").toString();
        String msgType	 = ObjectUtils.isEmpty(msg.get("MsgType"))?"":msg.get("MsgType").toString();









        if(org.apache.commons.lang.StringUtils.equals(event,"user_enter_tempsession")){
            String[] sessions = sessionFrom.split(",");
            if(sessions.length != 3){return ;}
            dynamicService.当前拉取图片(fromUserName,sessions[2]);

            if(org.apache.commons.lang.StringUtils.equals(sessions[0],"1"))
            {

                WxSendMessage wxSendMessage1 = new WxSendMessage();
                wxSendMessage1.setTouser(fromUserName);
                wxSendMessage1.setMsgtype("text");
                WxText wxText1 = new WxText();
                wxText1.setContent("确认获取二维码.\n来自用户:" + userService.queryUserEntity(sessions[1]).getNickName() + ".\n确认下载     请回复 ：1");
                wxSendMessage1.setText(wxText1);
                sendMsToCustomer(wxSendMessage1);



            }
            else if(org.apache.commons.lang.StringUtils.equals(sessions[0],"2"))
            {
                WxSendMessage wxSendMessage1 = new WxSendMessage();
                wxSendMessage1.setTouser(fromUserName);
                wxSendMessage1.setMsgtype("text");
                WxText wxText1 = new WxText();
                wxText1.setContent("确认获取审核员图片消息.\n来自审核员:" + userService.queryUser(sessions[1]).getUserName() + ".\n确认拉取     请回复 ：1 ");
                wxSendMessage1.setText(wxText1);
                sendMsToCustomer(wxSendMessage1);

            }
            else
            {

            }
//
//            WxSendMessage wxSendMessage = new WxSendMessage();
//            wxSendMessage.setTouser(fromUserName);
//            wxSendMessage.setMsgtype("image");
//            WxImage wxImage = new WxImage();
//            wxImage.setMedia_id(sessions[1]);
//            wxSendMessage.setImage(wxImage);
//            boolean result = sendMsToCustomer(wxSendMessage);
//
//            if(result){
//                dynamicService.已经拉取过图片(fromUserName,sessions[1],date);
//            }


        }else if(org.apache.commons.lang.StringUtils.equals(msgType,"text")){
            String content = msg.get("Content").toString();
            if(org.apache.commons.lang.StringUtils.equals(content,"1")){


                String mediaId = dynamicService.获取当前拉取图片(fromUserName);

                WxSendMessage wxSendMessage = new WxSendMessage();
                wxSendMessage.setTouser(fromUserName);
                wxSendMessage.setMsgtype("image");
                WxImage wxImage = new WxImage();
                wxImage.setMedia_id(mediaId);
                wxSendMessage.setImage(wxImage);
                sendMsToCustomer(wxSendMessage);


            }


        }
        else
        {

        }





    }


    private boolean sendMsToCustomer(WxSendMessage wxSendMessage) {
        String access_token = WeChatUtil.getAccess_token();
        System.out.println("access_token:--------" + access_token);

        RestTemplate restTemplate = new RestTemplate();
//        String jsonStr = json.toJSONString();
        //access_token
        String result = restTemplate.postForEntity("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" +
                access_token,wxSendMessage, String.class).getBody();

        int errorcode = JSON.parseObject(result).getIntValue("errcode");
        if(errorcode == 0){
            return true;
        }
        else
        {
            return false;
        }
    }

    public void 新增注册用户(UserEntity userEntity, String appName, String pkId, String userId, String fromUser) {


        dynamicService.新增注册用户(appName,pkId,userEntity.getUserId(),fromUser,new Date());





    }




    public void 创建榜次数加1(String userId) {
        synchronized (userId) {
            UserDynamicEntity result = queryUserKvEntity(userId);
            result.setPkTimes(result.getPkTimes() + 1);
            appDaoService.updateEntity(result);
        }

    }



    public void 修改用户头像(String userId, String url) {
        UserEntity result  = queryUserEntity(userId);

        result.setAvatarUrl(url);

        appDaoService.updateEntity(result);



    }



    public boolean 是否可以创建主题(String userId) {
        //普通用户且不要求主题和榜帖数量绑定时返回可以创建
        if(!AppConfigService.getConfigAsBoolean(ConfigItem.主题和榜帖数量绑定) && !this.是否是遗传用户(userId))
        {

            return true;


        }
        else
        {

            UserDynamicEntity result = queryUserKvEntity(userId);
            if(result.getPostTimes() > result.getPkTimes())
            {
                return true;
            }
            else
            {
                return false;
            }


        }



    }





    public void 返还用户打捞时间(String userId, long endTime) {
        long left = endTime - System.currentTimeMillis();
        if(left>0){
            UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
            userDynamicEntity.setFindTimeLength(userDynamicEntity.getFindTimeLength() + left);
            appDaoService.updateEntity(userDynamicEntity);
        }

    }
    public void 返还用户打捞时间1(String userId, long length) {
            UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
            userDynamicEntity.setFindTimeLength(userDynamicEntity.getFindTimeLength() + length*24*3600*1000);
            appDaoService.updateEntity(userDynamicEntity);


    }

    public String 查询Pk创建者名片(String userId) {
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
        return userDynamicEntity.getUserCard();
    }

    public UserCardApply 查询用户名片留言(String userId, String queryer) {
        UserCardEntity userCardEntity = 查询UserCard记录(userId,queryer);
        if(ObjectUtils.isEmpty(userCardEntity))
        {
            return null;
        }
        else
        {
            UserCardApply userCardApply = new UserCardApply();
            userCardApply.setApplyId(userCardEntity.getId());
            userCardApply.setTarget(this.queryUser(userCardEntity.getUserId()));
            userCardApply.setApplyer(userService.queryUser(userCardEntity.getApplyerId()));
            userCardApply.setText(userCardEntity.getText());
            userCardApply.setLock(userCardEntity.isCardLock());
            return userCardApply;
        }

    }
    public UserCardEntity 查询UserCard记录(String userId,String queryerId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("applyerId",CompareTag.Equal,queryerId);
        UserCardEntity userCardEntity = appDaoService.querySingleEntity(UserCardEntity.class,filter);

        return userCardEntity;

    }

    public void 上传UserCard(String userId, String userCard) {
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
        userDynamicEntity.setUserCard(userCard);
        appDaoService.updateEntity(userDynamicEntity);
    }

    public void 申请UserCard(String targetId, String userId, String text) {
        UserCardEntity userCardEntity = 查询UserCard记录(targetId,userId);
        if(ObjectUtils.isEmpty(userCardEntity))
        {
            userCardEntity = new UserCardEntity();
            userCardEntity.setUserId(targetId);
            userCardEntity.setText(text);
            userCardEntity.setTime(System.currentTimeMillis());
            userCardEntity.setCardLock(false);
            userCardEntity.setApplyerId(userId);
            appDaoService.insertEntity(userCardEntity);
        }
        else
        {
            userCardEntity.setUserId(targetId);
            userCardEntity.setText(text);
            userCardEntity.setTime(System.currentTimeMillis());
            userCardEntity.setCardLock(userCardEntity.isCardLock());
            userCardEntity.setApplyerId(userId);
            appDaoService.updateEntity(userCardEntity);
        }

    }

    public List<UserCardApply> 查询UserCard申请列表(String targetUserId,int type, int page) {
        List<UserCardApply> userCardApplies = new ArrayList<>();
        List<UserCardEntity> userCardEntities = new ArrayList<>();
        if(type == 0)
        {
            userCardEntities = this.查询想认识我的人(targetUserId,page);
        }
        else
        {
            userCardEntities = this.查询我想认识的人(targetUserId,page);
        }
        for(UserCardEntity userCardEntity:userCardEntities)
        {
            UserCardApply userCardApply = new UserCardApply();
            userCardApply.setApplyId(userCardEntity.getId());
            userCardApply.setTarget(this.queryUser(userCardEntity.getUserId()));
            userCardApply.setApplyer(userService.queryUser(userCardEntity.getApplyerId()));
            userCardApply.setText(userCardEntity.getText());
            userCardApply.setLock(userCardEntity.isCardLock());
            userCardApply.setTime(TimeUtils.convertTime(userCardEntity.getTime()));
            userCardApplies.add(userCardApply);

        }



        return userCardApplies;

    }

    private List<UserCardEntity> 查询我想认识的人(String targetUserId, int page) {
        List<UserCardEntity> applys = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardEntity.class)
                .compareFilter("applyerId",CompareTag.Equal,targetUserId)
                .pageLimitFilter(page,20)
                .orderByFilter("time", OrderTag.DESC);

        List<UserCardEntity> entities = appDaoService.queryEntities(UserCardEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities)){applys.addAll(entities);}
        return applys;


    }

    private List<UserCardEntity> 查询想认识我的人(String targetUserId, int page) {
        List<UserCardEntity> applys = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardEntity.class)
                .compareFilter("userId",CompareTag.Equal,targetUserId)
                .pageLimitFilter(page,20)
                .orderByFilter("time", OrderTag.DESC);

        List<UserCardEntity> entities = appDaoService.queryEntities(UserCardEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities)){applys.addAll(entities);}
        return applys;
    }

    public void 删除申请留言(String userId, String applyId) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardEntity.class)
                .compareFilter("id",CompareTag.Equal,Integer.valueOf(applyId));
        UserCardEntity userCardEntity = appDaoService.querySingleEntity(UserCardEntity.class,filter);
        if (!ObjectUtils.isEmpty(userCardEntity))
        {
            if(StringUtils.equalsIgnoreCase(userId,userCardEntity.getUserId()) || StringUtils.equalsIgnoreCase(userId,userCardEntity.getApplyerId()))
            {
                appDaoService.deleteEntity(userCardEntity);
            }
        }

    }

    public void 修改锁状态(String userId, String applyId) throws AppException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardEntity.class)
                .compareFilter("id",CompareTag.Equal,Integer.valueOf(applyId));
        UserCardEntity userCardEntity = appDaoService.querySingleEntity(UserCardEntity.class,filter);
        if(!StringUtils.equalsIgnoreCase(userId,userCardEntity.getUserId())){ throw AppException.buildException(PageAction.信息反馈框("非法用户","非法用户"));}
        if(!ObjectUtils.isEmpty(userCardEntity)){
            userCardEntity.setCardLock(!userCardEntity.isCardLock());
            appDaoService.updateEntity(userCardEntity);
            return;
        }
        throw AppException.buildException(PageAction.信息反馈框("不存在的留言","不存在的留言"));


    }


}
