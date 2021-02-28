package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.PubType;
import com.union.app.domain.pk.comment.Restore;
import com.union.app.domain.pk.message.Message;
import com.union.app.entity.pk.PostColumEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.msg.MessageEntity;
import com.union.app.entity.pk.msg.MessageType;
import com.union.app.entity.pk.post.CommentRestoreEntity;
import com.union.app.entity.pk.post.PostCommentEntity;
import com.union.app.plateform.constant.常量值;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.评论.CommentService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    AppDaoService daoService;

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    public void 添加动态信息(PostCommentEntity postCommentEntity)
    {
        PostEntity postEntity = postService.查询帖子ById(postCommentEntity.getPostId());
        if(ObjectUtils.isEmpty(postEntity) || StringUtils.equals(postCommentEntity.getUserId(),postEntity.getUserId())){return;}
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setPkId(postEntity.getPkId());
        messageEntity.setPostId(postCommentEntity.getPostId());
        messageEntity.setCommentId(postCommentEntity.getCommentId());
        messageEntity.setFromUser(postCommentEntity.getUserId());
        messageEntity.setToUser(postEntity.getUserId());
        messageEntity.setMessage(postCommentEntity.getComment());
        messageEntity.setType(MessageType.留言);
        messageEntity.setTime(System.currentTimeMillis());
        if(!StringUtils.equals(messageEntity.getToUser(),messageEntity.getFromUser())){
            daoService.insertEntity(messageEntity);
        }
    }
    public void 添加动态信息(CommentRestoreEntity commentRestoreEntity)
    {
        PostCommentEntity postCommentEntity = commentService.查询CommentById(commentRestoreEntity.getCommentId());
        PostEntity postEntity = postService.查询帖子ById(postCommentEntity.getPostId());
        if(ObjectUtils.isEmpty(postCommentEntity)|| ObjectUtils.isEmpty(postEntity)|| StringUtils.equals(postCommentEntity.getUserId(),commentRestoreEntity.getUserId())){return;}
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setPkId(postEntity.getPkId());
        messageEntity.setPostId(postCommentEntity.getPostId());
        messageEntity.setCommentId(postCommentEntity.getCommentId());
        messageEntity.setRestoreId(commentRestoreEntity.getRestoreId());
        messageEntity.setFromUser(commentRestoreEntity.getUserId());
        messageEntity.setToUser(StringUtils.isBlank(commentRestoreEntity.getTargetUserId())?postCommentEntity.getUserId():commentRestoreEntity.getTargetUserId());
        messageEntity.setMessage(commentRestoreEntity.getComment());
        messageEntity.setType(MessageType.回复);
        messageEntity.setTime(System.currentTimeMillis());
        if(!StringUtils.equals(messageEntity.getToUser(),messageEntity.getFromUser())){
            daoService.insertEntity(messageEntity);
        }
    }
    public void 删除评论动态信息(PostCommentEntity postCommentEntity)
    {
        MessageEntity messageEntity = this.查询动态评论消息ById(postCommentEntity.getCommentId());
        if(!ObjectUtils.isEmpty(messageEntity)){daoService.deleteEntity(messageEntity);}
    }
    public void 删除回复动态信息(CommentRestoreEntity commentRestoreEntity)
    {
        MessageEntity messageEntity = this.查询动态评论消息ById(commentRestoreEntity.getRestoreId());
        if(!ObjectUtils.isEmpty(messageEntity)){daoService.deleteEntity(messageEntity);}


    }

    private MessageEntity 查询动态评论消息ById(String commentId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(MessageEntity.class)
                .compareFilter("type", CompareTag.Equal,MessageType.留言)
                .andFilter()
                .compareFilter("commentId", CompareTag.Equal,commentId);
        MessageEntity messageEntity = daoService.querySingleEntity(MessageEntity.class,filter1);
        return messageEntity;


    }
    private MessageEntity 查询动态回复消息ById(String restoreId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(MessageEntity.class)
                .compareFilter("type", CompareTag.Equal,MessageType.回复)
                .andFilter()
                .compareFilter("restoreId", CompareTag.Equal,restoreId);
        MessageEntity messageEntity = daoService.querySingleEntity(MessageEntity.class,filter1);
        return messageEntity;
    }





    public Message translate(MessageEntity messageEntity){
        Message message = new Message();
        message.setMessageId(messageEntity.getMsgId());
        message.setMessage(messageEntity.getMessage());
        message.setFromUser(userService.queryUser(messageEntity.getFromUser()));
        message.setToUser(userService.queryUser(messageEntity.getToUser()));
        message.setType(messageEntity.getType().getStatu());
        message.setPkId(messageEntity.getPkId());
        message.setPostId(messageEntity.getPostId());
        message.setRestoreId(messageEntity.getRestoreId());
        message.setCommentId(messageEntity.getCommentId());
        message.setTime(TimeUtils.全局时间(messageEntity.getTime()));
        return message;
    }





    public List<Message> 查询用户消息(String userId, int page) {
        List<Message> messages = new ArrayList<>();
        List<MessageEntity> messageEntities = this.查询用户消息Entity(userId,page);
        if(CollectionUtils.isEmpty(messageEntities)){return messages;}
        List<Object> ids = collectPostIds(messageEntities);
        Map<String, PostColumEntity> postColumEntityMap = 查询PostColums(ids);
        messageEntities.forEach(messageEntity -> {
            Message message = translate(messageEntity);
            message.setPostColum(postColumEntityMap.get(messageEntity.getPostId()));
            messages.add(message);
        });
        return messages;
    }

    private Map<String, PostColumEntity> 查询PostColums(List<Object> ids) {
        Map<String, PostColumEntity> map = new HashMap<>();
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostColumEntity.class)
                .inFilter("postId",ids);
        List<PostColumEntity> postCommentEntities = daoService.queryEntities(PostColumEntity.class,filter1);
        if(CollectionUtils.isEmpty(postCommentEntities)){return map;}
        postCommentEntities.forEach(postColumEntity -> {
            map.put(postColumEntity.getPostId(),postColumEntity);
        });
        return map;
    }

    private List<MessageEntity> 查询用户消息Entity(String userId, int page) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(MessageEntity.class)
                .compareFilter("toUser",CompareTag.Equal,userId)
                .pageLimitFilter(page,10)
                .orderByFilter("time", OrderTag.DESC);
        List<MessageEntity> messageEntities = daoService.queryEntities(MessageEntity.class,filter1);
        return messageEntities;
    }


    private List<Object> collectPostIds(List<MessageEntity> messageEntities) {
        List<Object> ids = new ArrayList<>();
        if(CollectionUtils.isEmpty(messageEntities)){return ids;}
        messageEntities.forEach(messageEntity -> {
            ids.add(messageEntity.getPostId());
        });
        return ids;
    }







}
