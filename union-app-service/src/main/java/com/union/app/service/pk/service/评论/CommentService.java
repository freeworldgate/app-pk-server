package com.union.app.service.pk.service.评论;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.Restore;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostColumEntity;
import com.union.app.entity.pk.post.CommentRestoreEntity;
import com.union.app.entity.pk.post.IdGreateEntity;
import com.union.app.entity.pk.post.LikeStatu;
import com.union.app.entity.pk.post.PostCommentEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.MessageService;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.LockService;
import com.union.app.service.pk.service.PayService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {




    @Autowired
    PayService payService;

    @Autowired
    AppDaoService daoService;


    @Autowired
    DynamicService dynamicService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    LockService lockService;

    @Autowired
    MessageService messageService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    KeyService keyService;




    public List<Comment> 查询用户评论(String userId, int page) {
        List<Comment> comments = new ArrayList<>();
        List<PostCommentEntity> postCommentEntities = this.查询用户评论Entity(userId,page);
        if(CollectionUtils.isEmpty(postCommentEntities)){return comments;}
        List<Object> ids = collectPostIds(postCommentEntities);
        Map<String, PostColumEntity> postColumEntityMap = 查询PostColums(ids);
        postCommentEntities.forEach(postCommentEntity -> {
            Comment comment = translateComment(postCommentEntity);
            comment.setTime(TimeUtils.详细时间(postCommentEntity.getTime()));
            comment.setPostColum(postColumEntityMap.get(postCommentEntity.getPostId()));
            comments.add(comment);
        });
        return comments;
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

    private List<PostCommentEntity> 查询用户评论Entity(String userId, int page) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostCommentEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page,10)
                .orderByFilter("time", OrderTag.DESC);
        List<PostCommentEntity> postCommentEntities = daoService.queryEntities(PostCommentEntity.class,filter1);
        return postCommentEntities;
    }


    private List<Object> collectPostIds(List<PostCommentEntity> commentEntities) {
        List<Object> ids = new ArrayList<>();
        if(CollectionUtils.isEmpty(commentEntities)){return ids;}
        commentEntities.forEach(post -> {
            ids.add(post.getPostId());
        });
        return ids;
    }

    private Comment translateComment(PostCommentEntity postCommentEntity) {
        Comment comment = new Comment();
        comment.setCommentId(postCommentEntity.getCommentId());
        comment.setUser(userService.queryUser(postCommentEntity.getUserId()));
        comment.setPostId(postCommentEntity.getPostId());
        comment.setComment(postCommentEntity.getComment());
        comment.setDislikes(postCommentEntity.getDislikes());
        comment.setLikes(postCommentEntity.getLikes());
        comment.setTime(TimeUtils.convertTime(postCommentEntity.getTime()));
        comment.setRestores(postCommentEntity.getRestores());
        return comment;

    }


    public Comment 添加评论(String postId, String userId, String comment) {
        PostCommentEntity postCommentEntity = new PostCommentEntity();
        postCommentEntity.setCommentId(IdGenerator.getCommentId());
        postCommentEntity.setUserId(userId);
        postCommentEntity.setPostId(postId);
        postCommentEntity.setComment(comment);
        postCommentEntity.setDislikes(0);
        postCommentEntity.setRestores(0);
        postCommentEntity.setLikes(0);
        postCommentEntity.setTime(System.currentTimeMillis());
        daoService.insertEntity(postCommentEntity);
        keyService.Post评论数量加一(postId);
        userDynamicService.用户评论加一(userId);
        messageService.添加动态信息(postCommentEntity);
        return this.translateComment(postCommentEntity);
    }



    public List<Comment> 查询评论(String postId, int page) {
        List<Comment> comments = new ArrayList<>();
        List<PostCommentEntity> commentEntities = this.查询Post评论(postId,page);
        commentEntities.forEach(postCommentEntity -> {
            comments.add(translateComment(postCommentEntity));
        });
        return comments;
    }

    private List<PostCommentEntity> 查询Post评论(String postId, int page) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostCommentEntity.class)
                .compareFilter("postId",CompareTag.Equal,postId)
                .pageLimitFilter(page,12)
                .orderByFilter("time",OrderTag.DESC);
        List<PostCommentEntity> postCommentEntities = daoService.queryEntities(PostCommentEntity.class,filter1);
        return postCommentEntities;
    }

    public String 删除评论(String userId, String commentId) throws AppException {
        PostCommentEntity postCommentEntity = this.查询CommentById(commentId);
        if(!org.apache.commons.lang.StringUtils.equals(userId,postCommentEntity.getUserId()))
        {
            throw AppException.buildException(PageAction.信息反馈框("","非法用户"));
        }
        if(!ObjectUtils.isEmpty(postCommentEntity))
        {
            daoService.deleteEntity(postCommentEntity);
            messageService.删除评论动态信息(postCommentEntity);
            keyService.Post评论数量减一(postCommentEntity.getPostId());
            userDynamicService.用户评论减一(userId);
            return postCommentEntity.getPostId();
        }
        return null;
    }
    public Comment 查询评论ById(String commentId) {
        PostCommentEntity postCommentEntity = 查询CommentById(commentId);
        if(!ObjectUtils.isEmpty(postCommentEntity))
        {
            return this.translateComment(postCommentEntity);
        }
        return null;
    }
    public PostCommentEntity 查询CommentById(String commentId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(PostCommentEntity.class)
                .compareFilter("commentId",CompareTag.Equal,commentId);
        PostCommentEntity postCommentEntity = daoService.querySingleEntity(PostCommentEntity.class,filter1);
        return postCommentEntity;
    }


    public List<Restore> 查询回复(String commentId, int page) {
        List<Restore> restores = new ArrayList<>();
        List<CommentRestoreEntity> restoreEntities = this.查询Comment回复(commentId,page);
        restoreEntities.forEach(commentRestoreEntity -> {
            restores.add(translateRestore(commentRestoreEntity));
        });
        return restores;
    }

    private Restore translateRestore(CommentRestoreEntity commentRestoreEntity) {
        Restore restore = new Restore();
        restore.setRestoreId(commentRestoreEntity.getRestoreId());
        restore.setCommentId(commentRestoreEntity.getCommentId());
        restore.setUser(userService.queryUser(commentRestoreEntity.getUserId()));
        restore.setTargetUser(userService.queryUser(commentRestoreEntity.getTargetUserId()));
        restore.setComment(commentRestoreEntity.getComment());
        restore.setLikes(commentRestoreEntity.getLikes());
        restore.setDislikes(commentRestoreEntity.getDislikes());
        restore.setTime(TimeUtils.convertTime(commentRestoreEntity.getTime()));

        return restore;


    }

    private List<CommentRestoreEntity> 查询Comment回复(String commentId, int page) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(CommentRestoreEntity.class)
                .compareFilter("commentId",CompareTag.Equal,commentId)
                .pageLimitFilter(page,12)
                .orderByFilter("likes",OrderTag.DESC);
        List<CommentRestoreEntity> commentRestoreEntities = daoService.queryEntities(CommentRestoreEntity.class,filter1);
        return commentRestoreEntities;
    }


    public Restore 添加回复(String commentId, String userId,String targetUserId, String comment) {
        CommentRestoreEntity commentRestoreEntity = new CommentRestoreEntity();
        commentRestoreEntity.setRestoreId(IdGenerator.getRestoreId());
        commentRestoreEntity.setCommentId(commentId);
        commentRestoreEntity.setUserId(userId);
        commentRestoreEntity.setTargetUserId(org.apache.commons.lang.StringUtils.isBlank(targetUserId)?null:targetUserId);
        commentRestoreEntity.setComment(comment);
        commentRestoreEntity.setLikes(0);
        commentRestoreEntity.setDislikes(0);
        commentRestoreEntity.setTime(System.currentTimeMillis());
        daoService.insertEntity(commentRestoreEntity);
        if(org.apache.commons.lang.StringUtils.isBlank(targetUserId))
        {
            keyService.comment回复数量加一(commentId);
            keyService.通知同步队列(commentId, TimeSyncType.COMMENTRESTORE.getScene());
        }
        messageService.添加动态信息(commentRestoreEntity);
        return this.translateRestore(commentRestoreEntity);
    }


    public void 删除回复(String userId, String restoreId) throws AppException {
        CommentRestoreEntity restoreEntity = this.查询RestoreById(restoreId);
        if(!org.apache.commons.lang.StringUtils.equals(userId,restoreEntity.getUserId()))
        {
            throw AppException.buildException(PageAction.信息反馈框("","非法用户"));
        }
        if(!ObjectUtils.isEmpty(restoreEntity))
        {
            daoService.deleteEntity(restoreEntity);
            messageService.删除回复动态信息(restoreEntity);
            if(org.apache.commons.lang.StringUtils.isBlank(restoreEntity.getTargetUserId()))
            {
                keyService.comment回复数量减一(restoreEntity.getCommentId());
                keyService.通知同步队列(restoreEntity.getCommentId(), TimeSyncType.COMMENTRESTORE.getScene());
            }
        }
    }

    private CommentRestoreEntity 查询RestoreById(String restoreId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(CommentRestoreEntity.class)
                .compareFilter("restoreId",CompareTag.Equal,restoreId);
        CommentRestoreEntity commentRestoreEntity = daoService.querySingleEntity(CommentRestoreEntity.class,filter1);
        return commentRestoreEntity;
    }

}
