package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.ActiveTip;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.Restore;
import com.union.app.domain.pk.捞人.ScaleRange;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.city.CityEntity;
import com.union.app.entity.pk.kadian.label.ActiveTipEntity;
import com.union.app.entity.pk.kadian.label.RangeEntity;
import com.union.app.entity.pk.post.IdGreateEntity;
import com.union.app.entity.pk.post.LikeStatu;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class LikeService {




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
    LocationService locationService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    KeyService keyService;


    public void 点赞或踩(String id, String userId, int statu) {
        IdGreateEntity idGreateEntity = this.查询点赞信息ById(id,userId);
        if(ObjectUtils.isEmpty(idGreateEntity))
        {
            idGreateEntity = new IdGreateEntity();
            idGreateEntity.setId(IdGenerator.getGreateId());
            idGreateEntity.setStatu(statu);
            idGreateEntity.setTargetId(id);
            idGreateEntity.setUserId(userId);
            idGreateEntity.setTime(System.currentTimeMillis());
            daoService.insertEntity(idGreateEntity);
            if(statu == LikeStatu.LIKE.getStatu()){keyService.点赞数量加一(id);}
            if(statu == LikeStatu.DISLIKE.getStatu()){keyService.踩数量加一(id);}
        }
        else
        {
            if(statu == 1 || statu == 2)
            {
                Map<String,Object> map = new HashMap<>();
                map.put("statu",statu);
                daoService.updateColumById(IdGreateEntity.class,"id",idGreateEntity.getId(),map);
                if(idGreateEntity.getStatu() != statu && statu == LikeStatu.LIKE.getStatu())
                {
                    keyService.点赞数量加一(id);
                }
                if(idGreateEntity.getStatu() != statu && statu == LikeStatu.DISLIKE.getStatu())
                {
                    keyService.踩数量加一(id);
                }


                if(idGreateEntity.getStatu() == LikeStatu.DISLIKE.getStatu() && statu == LikeStatu.LIKE.getStatu()){
                    keyService.踩数量减一(id);
                }
                if(idGreateEntity.getStatu() == LikeStatu.LIKE.getStatu() && statu == LikeStatu.DISLIKE.getStatu()){

                    keyService.点赞数量减一(id);
                }







            }
            else
            {
                Map<String,Object> map = new HashMap<>();
                map.put("statu",0);
                daoService.updateColumById(IdGreateEntity.class,"id",idGreateEntity.getId(),map);
                if(idGreateEntity.getStatu() == LikeStatu.LIKE.getStatu()){keyService.点赞数量减一(id);}
                if(idGreateEntity.getStatu() == LikeStatu.DISLIKE.getStatu()){keyService.踩数量减一(id);}
            }
        }

    }

    private IdGreateEntity 查询点赞信息ById(String id, String userId) {
        EntityFilterChain filter1 = EntityFilterChain.newFilterChain(IdGreateEntity.class)
                .compareFilter("targetId",CompareTag.Equal,id)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        IdGreateEntity greateEntity = daoService.querySingleEntity(IdGreateEntity.class,filter1);
        return greateEntity;

    }


    public void 查询Post点赞记录(List<Post> posts, String userId) {
        User user = userService.queryUser(userId);
        if(ObjectUtils.isEmpty(user)|| CollectionUtils.isEmpty(posts)){return;}
        List<Object> ids = collectIds(posts);
        Map<String, IdGreateEntity> postGreateEntityMap = 查询用户点赞记录(ids,userId);
        posts.forEach(post -> {
            post.setStatu(ObjectUtils.isEmpty(postGreateEntityMap.get(post.getPostId()))?0:postGreateEntityMap.get(post.getPostId()).getStatu());
        });
    }

    private Map<String, IdGreateEntity> 查询用户点赞记录(List<Object> ids, String userId) {
        Map<String,IdGreateEntity> postGreateEntityMap = new HashMap<>();
        if(CollectionUtils.isEmpty(ids)){return postGreateEntityMap;}

        EntityFilterChain filter = EntityFilterChain.newFilterChain(IdGreateEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .andFilter()
                .inFilter("targetId",ids);
        List<IdGreateEntity> postGreateEntityList = daoService.queryEntities(IdGreateEntity.class,filter);
        postGreateEntityList.forEach(postGreateEntity -> {
            postGreateEntityMap.put(postGreateEntity.getTargetId(),postGreateEntity);
        });
        return postGreateEntityMap;


    }

    private List<Object> collectIds(List<Post> posts) {

        List<Object> ids = new ArrayList<>();
        posts.forEach(post -> {
            ids.add(post.getPostId());
        });
        return ids;
    }

    public void 查询评论点赞记录(List<Comment> comments, String userId) {
        User user = userService.queryUser(userId);
        if(ObjectUtils.isEmpty(user) || CollectionUtils.isEmpty(comments)){return;}
        List<Object> ids = collectCommentIds(comments);
        Map<String, IdGreateEntity> postGreateEntityMap = 查询用户点赞记录(ids,userId);
        comments.forEach(comment -> {
            comment.setStatu(ObjectUtils.isEmpty(postGreateEntityMap.get(comment.getCommentId()))?0:postGreateEntityMap.get(comment.getCommentId()).getStatu());
        });

    }

    private List<Object> collectCommentIds(List<Comment> comments) {
        List<Object> ids = new ArrayList<>();
        comments.forEach(post -> {
            ids.add(post.getCommentId());
        });
        return ids;
    }

    public void 查询回复点赞记录(List<Restore> restores, String userId) {
        User user = userService.queryUser(userId);
        if(ObjectUtils.isEmpty(user) || CollectionUtils.isEmpty(restores)){return;}
        List<Object> ids = collectRestoreIds(restores);
        Map<String, IdGreateEntity> postGreateEntityMap = 查询用户点赞记录(ids,userId);
        restores.forEach(restore -> {
            restore.setStatu(ObjectUtils.isEmpty(postGreateEntityMap.get(restore.getRestoreId()))?0:postGreateEntityMap.get(restore.getRestoreId()).getStatu());
        });

    }

    private List<Object> collectRestoreIds(List<Restore> restores) {
        List<Object> ids = new ArrayList<>();
        restores.forEach(post -> {
            ids.add(post.getRestoreId());
        });
        return ids;
    }
}
