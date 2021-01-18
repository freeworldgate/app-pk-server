package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.entity.评论.PkCommentEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class CommentService {




    @Autowired
    PayService payService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    CommentService appService;

    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    PostCacheService postCacheService;

    @Autowired
    ApproveService approveService;

    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    MediaService mediaService;

    @Autowired
    PkDataService pkDataService;

    @Autowired
    ComplainService complainService;


    public List<Comment> 查询评论(String id, int type, String userId, int page) {
        //type 用来区分Post还是Pk的评论,目前只支持PK评论
        if(type == 0 || type == 1)
        {
            List<Comment> comments = 查询PK评论(id,page);
            return comments;
        }

        return new ArrayList<>();

    }

    private List<Comment> 查询PK评论(String pkId, int page) {
        List<Comment> comments = new ArrayList<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCommentEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .pageLimitFilter(page,AppConfigService.getConfigAsInteger(ConfigItem.单个PK页面的帖子数))
                .orderByFilter("time",OrderTag.DESC);
        List<PkCommentEntity> pkCommentEntities = daoService.queryEntities(PkCommentEntity.class,filter);
        if(CollectionUtils.isEmpty(pkCommentEntities)){return comments;}

        pkCommentEntities.forEach(entity->{
            Comment comment1 = new Comment();
            comment1.setCommentId(entity.getCommentId());
            comment1.setId(entity.getPkId());
            comment1.setText(entity.getText());
            comment1.setUser(userService.queryUser(entity.getUserId()));
            comment1.setTime(TimeUtils.convertTime(entity.getTime()));
            comments.add(comment1);
        });

        return comments;

    }

    public Comment 添加评论(String id, int type, String userId, String text) throws AppException {

        if(type == 0 || type == 1)
        {
            PkCommentEntity entity = 添加PK评论(id,userId,text,type);
            Comment comment1 = new Comment();
            comment1.setCommentId(entity.getCommentId());
            comment1.setId(entity.getPkId());
            comment1.setText(entity.getText());
            comment1.setUser(userService.queryUser(entity.getUserId()));
            comment1.setTime(TimeUtils.convertTime(entity.getTime()));
            return comment1;
        }

        throw AppException.buildException(PageAction.消息级别提示框(Level.警告消息,"咱不吃评论类型"));

    }

    private PkCommentEntity 添加PK评论(String pkId, String userId, String text,int type) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkCommentEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkCommentEntity commentEntity = daoService.querySingleEntity(PkCommentEntity.class,filter);
        if(ObjectUtils.isEmpty(commentEntity))
        {
            commentEntity = new PkCommentEntity();
            commentEntity.setCommentId(IdGenerator.getCommentId());
            commentEntity.setPkId(pkId);
            commentEntity.setType(type);
            commentEntity.setText(text);
            commentEntity.setUserId(userId);
            commentEntity.setTime(System.currentTimeMillis());
            daoService.insertEntity(commentEntity);

            dynamicService.valueIncr(CacheKeyName.评论,pkId);
            dynamicService.valueIncr(CacheKeyName.用户评论,userId);



        }
        else
        {

            commentEntity.setPkId(pkId);
            commentEntity.setText(text);
            commentEntity.setUserId(userId);
            commentEntity.setTime(System.currentTimeMillis());
            daoService.updateEntity(commentEntity);
        }

        return commentEntity;

    }
}
