package com.union.app.service.pk.dynamic.imp;

import com.union.app.domain.pk.PkDynamic.PostDynamic;
import com.union.app.plateform.storgae.RedisUtil;
import com.union.app.service.pk.dynamic.DynamicKeyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PostDynamicService {




    @Autowired
    RedisUtil redisUtil;


    /**
     * Map数据结构
     * @param pkId
     * @return
     */
    public PostDynamic getPostDynamic(String pkId, String postId) {
        long follow = this.getPostDynamic_follow(pkId,postId);
        long view = this.getPostDynamic_view(pkId,postId);

        PostDynamic postDynamic = new PostDynamic();

        postDynamic.setFollow(follow);
        postDynamic.setView(view);

        return postDynamic;
    }

    private long getPostDynamic_follow(String pkId, String postId){ long follow = (Long)redisUtil.hget(DynamicKeyName.post_Follow_KeyName(pkId),postId);return follow; }
    private long getPostDynamic_view(String pkId, String postId){ long view = (Long)redisUtil.hget(DynamicKeyName.post_View_KeyName(pkId),postId);return view; }

    public long 递增Follow(String pkId, String userId){redisUtil.hincr(DynamicKeyName.post_Follow_KeyName(pkId),userId,1);return this.getPostDynamic_follow(pkId,userId);}
    public long 递增View(String pkId, String userId){redisUtil.hincr(DynamicKeyName.post_View_KeyName(pkId),userId,1);return this.getPostDynamic_view(pkId,userId);}

    public long 递减Follow(String pkId, String userId){redisUtil.hdecr(DynamicKeyName.post_Follow_KeyName(pkId),userId,-1);return this.getPostDynamic_follow(pkId,userId);}





}
