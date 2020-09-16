package com.union.app.service.user;


import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.客服消息.WxImage;
import com.union.app.domain.pk.客服消息.WxSendMessage;
import com.union.app.domain.pk.客服消息.WxText;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.domain.user.User;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.service.pk.dynamic.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private Map<String,User> users = new HashMap<>();


    public UserEntity queryUserEntity(String userId){
        UserEntity userEntity = pkCacheService.get(userId,UserEntity.class);
        if(ObjectUtils.isEmpty(userEntity))
        {
            EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                    .compareFilter("userId",CompareTag.Equal,userId);
            userEntity = appDaoService.querySingleEntity(UserEntity.class,filter);
        }
        return userEntity;
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
//        User user1 = users.get(userId);
//        if(!ObjectUtils.isEmpty(user1)){return user1;}



        UserEntity result  = queryUserEntity(userId);
        if(!ObjectUtils.isEmpty(result))
        {
            User user = new User();
            user.setUserName(new String(result.getNickName()));
//            user.setUserName(RandomUtil.getRandomName());
            user.setUserId(result.getUserId());
            user.setUserType(ObjectUtils.isEmpty(result.getUserType())?UserType.普通用户.getType():result.getUserType().getType());
//            user.setImgUrl(result.getAvatarUrl());
            user.setPkTimes(result.getPkTimes());
            user.setPostTimes(result.getPostTimes());
            user.setInviteTimes(result.getInviteTimes());
            user.setImgUrl(result.getAvatarUrl());

            users.put(user.getUserId(),user);

            return user;
        }
        return null;
    }


    private boolean isUserVip(String userId){

            if(!this.isUserExist(userId)){return false;}
            //运营模式
            User user = this.queryUser(userId);

            if(UserType.重点用户.getType() == user.getUserType()){return true;}
            else{return false;}



    }


    public boolean canUserView(String userId,String fromUserId)
    {
        if(this.isUserExist(userId))
        {
            return this.isUserVip(userId);
        }
        else
        {
            return this.isUserVip(fromUserId);
        }
    }

    public boolean 是否是遗传用户(String userId)
    {
        return this.isUserVip(userId);

    }

    public boolean canUserView(String userId)
    {

        return this.isUserVip(userId);


    }





    public boolean isUserExist(String userId) {
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(userId,"null")){
            return false;
        }
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(userId,"undifined")){
            return false;
        }
        User user = this.queryUser(userId);
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
                wxText1.setContent("确认获取用户图片.\n来自榜主:" + userService.queryUser(sessions[1]).getUserName() + ".\n确认拉取     请回复 ：1");
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

    public void 用户已打榜(String userId) {
        UserEntity result  = queryUserEntity(userId);
        result.setPostTimes(result.getPostTimes() + 1);
            appDaoService.updateEntity(result);











    }


    public int queryUserPkTimes(String userId) {
        UserEntity result  = queryUserEntity(userId);

        return result.getPkTimes();
    }

    public void 创建榜次数加1(String userId) {
        UserEntity result  = queryUserEntity(userId);
        result.setPkTimes(result.getPkTimes() + 1);
        appDaoService.updateEntity(result);

    }

    public void 邀请次数加一(String userId) {

        UserEntity result  = queryUserEntity(userId);
        result.setInviteTimes(result.getInviteTimes() + 1);
        appDaoService.updateEntity(result);

    }

    public int 查询用户剩余榜单(String userId) {
        UserEntity result  = queryUserEntity(userId);
        return result.getPostTimes();
    }

    public int 查询邀请次数(String userId) {
        UserEntity result  = queryUserEntity(userId);
        return result.getInviteTimes();
    }

    public int 查询建榜次数(String userId) {
        UserEntity result  = queryUserEntity(userId);
        return result.getPkTimes();
    }


    public void 确认开通PK次数加1(String userId) {
        UserEntity result  = queryUserEntity(userId);
        result.setActivePkTimes(result.getActivePkTimes() + 1);

    }

    public boolean 用户未激活榜单超限(String userId) {

        UserEntity result  = queryUserEntity(userId);
        int noActivePks = result.getPkTimes() - result.getActivePkTimes();
        if(noActivePks >= AppConfigService.getConfigAsInteger(ConfigItem.用户最多未激活榜单数量))
        {
            return true;
        }
        return false;



    }

    public boolean 是否已经打榜(String userId) {
        if(org.apache.commons.lang.StringUtils.isBlank(userId)){return false;}
        UserEntity result  = queryUserEntity(userId);
        if(!ObjectUtils.isEmpty(result))
        {
            return result.getPostTimes() > 0;
        }
        return false;


    }

    public void 删除一个未激活榜单(String userId) {
        UserEntity result  = queryUserEntity(userId);

        result.setPkTimes(result.getPkTimes() - 1);
        appDaoService.updateEntity(result);


    }

    public void 修改用户头像(String userId, String url) {
        UserEntity result  = queryUserEntity(userId);

        result.setAvatarUrl(url);

        appDaoService.updateEntity(result);



    }

    public void 修改PKUser(String pkId, String userId) {
    }
}
