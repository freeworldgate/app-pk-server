package com.union.app.service.user;


import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.user.UserCardApply;
import com.union.app.domain.pk.名片.UserCard;
import com.union.app.domain.pk.客服消息.WxImage;
import com.union.app.domain.pk.客服消息.WxSendMessage;
import com.union.app.domain.pk.客服消息.WxText;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.kadian.UserCardApplyEntity;
import com.union.app.entity.pk.名片.UserCardEntity;
import com.union.app.entity.pk.名片.UserCardMemberEntity;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.LockService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
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
    UserDynamicService userDynamicService;

    @Autowired
    PkCacheService pkCacheService;

    @Autowired
    UserService userService;


    @Autowired
    KeyService keyService;

    @Autowired
    LockService lockService;


    public UserEntity queryUserEntity(String userId){
        if(StringUtils.isBlank(userId)||StringUtils.equalsIgnoreCase("null",userId)||StringUtils.equalsIgnoreCase("undefined",userId)||StringUtils.equalsIgnoreCase("Nan",userId))
        {
            return null;
        }
        UserEntity userEntity = keyService.queryUserEntity(userId);
        return userEntity;
    }

    public UserDynamicEntity queryUserKvEntity(String userId)
    {
        return userDynamicService.queryUserDynamicEntity(userId);
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
            user.setUserId(result.getUserId());
            user.setUserType(ObjectUtils.isEmpty(result.getUserType())?UserType.普通用户.getType():result.getUserType().getType());
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
        if(StringUtils.isBlank(userId)){return false;}
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





    public void 创建榜次数加1(String userId) {
        synchronized (userId) {
            UserDynamicEntity result = queryUserKvEntity(userId);
            Map<String,Object> map = new HashMap<>();
            map.put("pkTimes",result.getPkTimes() + 1);
            appDaoService.updateColumById(result.getClass(),"userId",result.getUserId(),map);

        }

    }



    public void 修改用户头像(String userId, String url) {
        UserEntity result  = queryUserEntity(userId);

        result.setAvatarUrl(url);

        appDaoService.updateEntity(result);



    }



    public void 返还用户打捞时间(String userId, long endTime) {
        long left = endTime - System.currentTimeMillis();
        if(left>0){
            UserDynamicEntity userDynamicEntity =  userService.queryUserKvEntity(userId);
            Map<String,Object> map = new HashMap<>();
            map.put("findTimeLength",userDynamicEntity.getFindTimeLength() + left);
            appDaoService.updateColumById(userDynamicEntity.getClass(),"userId",userDynamicEntity.getUserId(),map);

        }

    }
    public void 返还用户打捞时间1(String userId, long length) {
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
        Map<String,Object> map = new HashMap<>();
        map.put("findTimeLength",userDynamicEntity.getFindTimeLength() + length*24*3600*1000L);
        appDaoService.updateColumById(UserDynamicEntity.class,"userId",userId,map);

    }


    public UserCardApply 查询用户名片留言(String userId, String queryer) {
        UserCardApplyEntity userCardApplyEntity = 查询UserCard记录(userId,queryer);
        if(ObjectUtils.isEmpty(userCardApplyEntity))
        {
            return null;
        }
        else
        {
            UserCardApply userCardApply = new UserCardApply();
            userCardApply.setApplyId(userCardApplyEntity.getId());
            userCardApply.setTarget(this.queryUser(userCardApplyEntity.getUserId()));
            userCardApply.setApplyer(userService.queryUser(userCardApplyEntity.getApplyerId()));
            userCardApply.setText(userCardApplyEntity.getText());
            userCardApply.setLock(userCardApplyEntity.isCardLock());
            return userCardApply;
        }

    }
    public UserCardApplyEntity 查询UserCard记录(String userId, String queryerId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardApplyEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("applyerId",CompareTag.Equal,queryerId);
        UserCardApplyEntity userCardApplyEntity = appDaoService.querySingleEntity(UserCardApplyEntity.class,filter);

        return userCardApplyEntity;

    }

    public void 上传UserCard(String userId, String userCard) {

        Map<String,Object> map = new HashMap<>();
        map.put("userCard",userCard);
        appDaoService.updateColumById(UserCardEntity.class,"userId",userId,map);

    }



    public UserCard 查询UserCard(String userId) {
        UserCard userCard = new UserCard();
        userCard.setUser(this.queryUser(userId));
        UserCardEntity userCardEntity = userService.queryUserCardEntity(userId);

        if(ObjectUtils.isEmpty(userCardEntity)){
            userCard.setUnLock(0);
            userCard.setLikeMe(0);
            userCard.setMeLike(0);
            userCard.setUserCard(null);
            userCard.setMember1(null);
            userCard.setMember2(null);
            userCard.setMember3(null);
        }
        else
        {
            userCard.setUnLock(userCardEntity.getUnLock());
            userCard.setLikeMe(keyService.queryKey(userId, KeyType.想认识我的人));
            userCard.setMeLike(userCardEntity.getMeLike());
            userCard.setUserCard(userCardEntity.getUserCard());
            userCard.setMember1(this.queryUser(userCardEntity.getMember1()));
            userCard.setMember2(this.queryUser(userCardEntity.getMember2()));
            userCard.setMember3(this.queryUser(userCardEntity.getMember3()));
        }



        return userCard;

    }







    public void 申请UserCard(String targetId, String userId, String text) throws AppException {
        UserCardApplyEntity userCardApplyEntity = 查询UserCard记录(targetId,userId);
        if(ObjectUtils.isEmpty(userCardApplyEntity))
        {
            userCardApplyEntity = new UserCardApplyEntity();
            userCardApplyEntity.setUserId(targetId);
            userCardApplyEntity.setText(text);
            userCardApplyEntity.setTime(System.currentTimeMillis());
            userCardApplyEntity.setCardLock(false);
            userCardApplyEntity.setApplyerId(userId);
            //添加一条解锁请求
            appDaoService.insertEntity(userCardApplyEntity);

            //更新用户名下他想认识和想认识他的人数量。
            this.我想认识的人加一(userId);

            this.想认识我的人加一(targetId);



        }
        else
        {

            Map<String,Object> map = new HashMap<>();
            map.put("text",text);
            if(!userCardApplyEntity.isCardLock()){
                map.put("time",System.currentTimeMillis());
            }
            appDaoService.updateColumById(userCardApplyEntity.getClass(),"id",userCardApplyEntity.getId(),map);


        }

    }

    private void 想认识我的人加一(String userId) throws AppException {

        keyService.想认识我的人加一(userId);
//        if(lockService.getLock(userId, LockType.用户名片锁))
//        {
//            UserCardEntity userCardEntity = userService.queryUserCardEntity(userId);
//            userCardEntity.setLikeMe(userCardEntity.getLikeMe()+1);
//            appDaoService.updateEntity(userCardEntity);
//            lockService.releaseLock(userId, LockType.用户名片锁);
//        }
//        else
//        {
//            throw AppException.buildException(PageAction.信息反馈框("系统错误","系统错误"));
//        }
    }
    private void 想认识我的人减一(String userId) throws AppException {
        keyService.想认识我的人减一(userId);
//        if(lockService.getLock(userId, LockType.用户名片锁))
//        {
//            UserCardEntity userCardEntity = userService.queryUserCardEntity(userId);
//            userCardEntity.setLikeMe(userCardEntity.getLikeMe()-1);
//            appDaoService.updateEntity(userCardEntity);
//            lockService.releaseLock(userId, LockType.用户名片锁);
//        }
//        else
//        {
//            throw AppException.buildException(PageAction.信息反馈框("系统错误","系统错误"));
//        }
    }
    private void 我想认识的人加一(String userId) throws AppException {

            UserCardEntity userCardEntity = userService.queryUserCardEntity(userId);

            Map<String,Object> map = new HashMap<>();
            map.put("meLike",userCardEntity.getMeLike()+1);
            appDaoService.updateColumById(userCardEntity.getClass(),"userId",userCardEntity.getUserId(),map);






    }
    private void 我想认识的人减一(String userId) throws AppException {

        UserCardEntity userCardEntity = userService.queryUserCardEntity(userId);
        Map<String,Object> map = new HashMap<>();
        map.put("meLike",userCardEntity.getMeLike()-1);
        appDaoService.updateColumById(userCardEntity.getClass(),"userId",userCardEntity.getUserId(),map);



    }
    public UserCardEntity queryUserCardEntity(String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        UserCardEntity userCardEntity = appDaoService.querySingleEntity(UserCardEntity.class,filter);

        return userCardEntity;
    }

    public List<UserCardApply> 查询UserCard申请列表(String targetUserId,int type, int page) {
        List<UserCardApply> userCardApplies = new ArrayList<>();
        List<UserCardApplyEntity> userCardEntities = new ArrayList<>();
        if(type == 0)
        {
            userCardEntities = this.查询想认识我的人(targetUserId,page);
        }
        else
        {
            userCardEntities = this.查询我想认识的人(targetUserId,page);
        }
        for(UserCardApplyEntity userCardApplyEntity :userCardEntities)
        {
            UserCardApply userCardApply = new UserCardApply();
            userCardApply.setApplyId(userCardApplyEntity.getId());
            userCardApply.setTarget(this.queryUser(userCardApplyEntity.getUserId()));
            userCardApply.setApplyer(userService.queryUser(userCardApplyEntity.getApplyerId()));
            userCardApply.setText(userCardApplyEntity.getText());
            userCardApply.setLock(userCardApplyEntity.isCardLock());
            userCardApply.setTime(TimeUtils.convertTime(userCardApplyEntity.getTime()));
            userCardApplies.add(userCardApply);

        }



        return userCardApplies;

    }

    private List<UserCardApplyEntity> 查询我想认识的人(String targetUserId, int page) {
        List<UserCardApplyEntity> applys = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardApplyEntity.class)
                .compareFilter("applyerId",CompareTag.Equal,targetUserId)
                .pageLimitFilter(page,20)
                .orderByFilter("time", OrderTag.DESC);

        List<UserCardApplyEntity> entities = appDaoService.queryEntities(UserCardApplyEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities)){applys.addAll(entities);}
        return applys;


    }

    private List<UserCardApplyEntity> 查询想认识我的人(String targetUserId, int page) {
        List<UserCardApplyEntity> applys = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardApplyEntity.class)
                .compareFilter("userId",CompareTag.Equal,targetUserId)
                .pageLimitFilter(page,20)
                .orderByFilter("time", OrderTag.DESC);

        List<UserCardApplyEntity> entities = appDaoService.queryEntities(UserCardApplyEntity.class,filter);
        if(!CollectionUtils.isEmpty(entities)){applys.addAll(entities);}
        return applys;
    }

    public void 删除申请留言(String userId, String applyId) throws AppException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardApplyEntity.class)
                .compareFilter("id",CompareTag.Equal,Integer.valueOf(applyId));
        UserCardApplyEntity userCardApplyEntity = appDaoService.querySingleEntity(UserCardApplyEntity.class,filter);
        if (!ObjectUtils.isEmpty(userCardApplyEntity) && !userCardApplyEntity.isCardLock())
        {
            if(StringUtils.equalsIgnoreCase(userId, userCardApplyEntity.getUserId()) || StringUtils.equalsIgnoreCase(userId, userCardApplyEntity.getApplyerId()))
            {
                appDaoService.deleteEntity(userCardApplyEntity);
                this.我想认识的人减一(userId);
                this.想认识我的人减一(userCardApplyEntity.getUserId());
            }
        }
        else
        {
            throw AppException.buildException(PageAction.信息反馈框("已审批留言不能删除","已审批留言不能删除"));
        }

    }



    public void 修改锁状态(String userId, String applyId) throws AppException {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardApplyEntity.class)
                .compareFilter("id",CompareTag.Equal,Integer.valueOf(applyId));
        UserCardApplyEntity userCardApplyEntity = appDaoService.querySingleEntity(UserCardApplyEntity.class,filter);

        if(!StringUtils.equalsIgnoreCase(userId, userCardApplyEntity.getUserId())){ throw AppException.buildException(PageAction.信息反馈框("非法用户","非法用户"));}

        if(!ObjectUtils.isEmpty(userCardApplyEntity)){
            if(userCardApplyEntity.isCardLock())
            {
                禁止用户查看UserCard(userId,userCardApplyEntity.getApplyerId());
            }
            else
            {
                允许用户查看UserCard(userId,userCardApplyEntity.getApplyerId());
            }
            userCardApplyEntity.setCardLock(!userCardApplyEntity.isCardLock());
            appDaoService.updateEntity(userCardApplyEntity);
            return;
        }
        throw AppException.buildException(PageAction.信息反馈框("不存在的留言","不存在的留言"));


    }

    private void 允许用户查看UserCard(String userId, String applyId) {
        新增一个可见UserCard成员(userId,applyId);
        UserCardEntity userCardEntity = this.queryUserCardEntity(userId);

        添加Member(userCardEntity,applyId);

        Map<String,Object> map = new HashMap<>();
        map.put("unLock",userCardEntity.getUnLock()+1);
        appDaoService.updateColumById(userCardEntity.getClass(),"userId",userCardEntity.getUserId(),map);


    }
    private UserCardMemberEntity queryUserCardEntity(String userId,String memberId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserCardMemberEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("memberUserId",CompareTag.Equal,memberId);
        UserCardMemberEntity userCardMemberEntity = appDaoService.querySingleEntity(UserCardMemberEntity.class,filter);
        return userCardMemberEntity;
    }
    private void 删除一个可见UserCard成员(String userId, String applyId) {
        UserCardMemberEntity userCardMemberEntity = queryUserCardEntity(userId,applyId);
        if(!ObjectUtils.isEmpty(userCardMemberEntity)){
            appDaoService.deleteEntity(userCardMemberEntity);
        }




    }
    private void 新增一个可见UserCard成员(String userId, String applyId) {
        UserCardMemberEntity userCardMemberEntity = queryUserCardEntity(userId,applyId);
        if(ObjectUtils.isEmpty(userCardMemberEntity))
        {
            userCardMemberEntity = new UserCardMemberEntity();
            userCardMemberEntity.setUserId(userId);
            userCardMemberEntity.setMemberUserId(applyId);
            userCardMemberEntity.setTime(System.currentTimeMillis());
            appDaoService.insertEntity(userCardMemberEntity);
        }

    }

    private void 添加Member(UserCardEntity userCardEntity, String applyId) {
        if(StringUtils.isBlank(userCardEntity.getMember1()))
        {
            userCardEntity.setMember1(applyId);
        }
        else if(StringUtils.isBlank(userCardEntity.getMember2()))
        {
            userCardEntity.setMember2(applyId);
        }
        else if(StringUtils.isBlank(userCardEntity.getMember3()))
        {
            userCardEntity.setMember3(applyId);
        }
        else
        {
            
        }

    }

    public void 创建UserCardEntity(String userId) {
        UserCardEntity userCardEntity = new UserCardEntity();
        userCardEntity.setUserId(userId);
        userCardEntity.setUnLock(0);
        userCardEntity.setMeLike(0);
        userCardEntity.setLikeMe(0);
        userCardEntity.setMember1(null);
        userCardEntity.setMember2(null);
        userCardEntity.setMember3(null);
        userCardEntity.setUserCard(null);
        userCardEntity.setTime(0);
        appDaoService.insertEntity(userCardEntity);

    }
    public void 创建内置UserCardEntity(String userId) {
        UserCardEntity userCardEntity = new UserCardEntity();
        userCardEntity.setUserId(userId);
        userCardEntity.setMeLike(new Random().nextInt(30));
        userCardEntity.setLikeMe(new Random().nextInt(30));
        userCardEntity.setUnLock(userCardEntity.getLikeMe()>3?new Random().nextInt(userCardEntity.getLikeMe()):0);
        keyService.setMapKey(userId,KeyType.想认识我的人,userCardEntity.getLikeMe()*1L);
        userCardEntity.setMember1(userService.随机选择内置用户());
        userCardEntity.setMember2(userService.随机选择内置用户());
        userCardEntity.setMember3(userService.随机选择内置用户());
        userCardEntity.setUserCard(null);
        userCardEntity.setTime(0);
        appDaoService.insertEntity(userCardEntity);

    }
    private void 禁止用户查看UserCard(String userId, String applyId) {

        删除一个可见UserCard成员(userId,applyId);
        UserCardEntity userCardEntity = this.queryUserCardEntity(userId);
        userCardEntity.setUnLock(userCardEntity.getUnLock()>0?userCardEntity.getUnLock()-1:0);
        删除Member(userCardEntity,applyId);
        appDaoService.updateEntity(userCardEntity);



    }



    private void 删除Member(UserCardEntity userCardEntity, String userId) {
        if(StringUtils.equals(userCardEntity.getMember1(),userId))
        {
            userCardEntity.setMember1("");
        }
        else if(StringUtils.equals(userCardEntity.getMember2(),userId))
        {
            userCardEntity.setMember2("");
        }
        else if(StringUtils.equals(userCardEntity.getMember3(),userId))
        {
            userCardEntity.setMember3("");
        }
        else
        {

        }
    }


    public Map<String, UserDynamicEntity> 批量查询用户Dynamic(List<Object> userIds) {
        Map<String,UserDynamicEntity> dynamicEntityMap = new HashMap<>();
        if(CollectionUtils.isEmpty(userIds)){return dynamicEntityMap;}
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .inFilter("userId",userIds);
        List<UserDynamicEntity> userDynamicEntites = appDaoService.queryEntities(UserDynamicEntity.class,filter);
        userDynamicEntites.forEach(userDynamicEntity -> {
            dynamicEntityMap.put(userDynamicEntity.getUserId(),userDynamicEntity);
        });
        return dynamicEntityMap;
    }

    public void 更换头像(String userId, String imgUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("avatarUrl",imgUrl);
        appDaoService.updateColumById(UserEntity.class,"userId",userId,map);
        keyService.刷新用户User缓存(userId);
    }

    public void 更换昵称(String userId, String userName) throws AppException {

//        checkUserName(userName);

        Map<String,Object> map = new HashMap<>();
        map.put("nickName",userName);
        appDaoService.updateColumById(UserEntity.class,"userId",userId,map);
        keyService.刷新用户User缓存(userId);
    }

    private void checkUserName(String userName) throws AppException {
        String regex = "^[a-z0-9A-Z]+$";
        if(userName.matches(regex))
        {
            if(userName.length() > 6){
                throw AppException.buildException(PageAction.信息反馈框("","英文字符和数字不得超过6位"));
            }
        }
        else
        {
            if(userName.length() > 3){
                throw AppException.buildException(PageAction.信息反馈框("","中文字符不得超过3位"));
            }
        }
    }


    public String 随机选择内置用户() {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                .compareFilter("userType",CompareTag.Equal,UserType.重点用户)
                .orderByRandomFilter();
        UserEntity userEntity = appDaoService.querySingleEntity(UserEntity.class,filter);
        return userEntity.getUserId();
    }

    public void 内置用户新增打捞时间(String userId) {
        Map<String,Object> map = new HashMap<>();
        map.put("findTimeLength",Long.valueOf(365 * 24 *3600 *1000L));
        appDaoService.updateColumById(UserDynamicEntity.class,"userId",userId,map);

    }

    public boolean 管理员用户(String userId) {
        UserEntity userEntity = this.queryUserEntity(userId);
        if(ObjectUtils.isEmpty(userEntity)){return false;}
        if(userEntity.getUserType() == UserType.管理用户){return true;}
        return false;
    }

    public boolean 是否是内置用户(String userId) {
        UserEntity userEntity = this.queryUserEntity(userId);
        if(ObjectUtils.isEmpty(userEntity)){return false;}
        if(userEntity.getUserType() == UserType.重点用户){return true;}
        return false;
    }


}
