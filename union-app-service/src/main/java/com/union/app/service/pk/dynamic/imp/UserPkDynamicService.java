package com.union.app.service.pk.dynamic.imp;

import com.union.app.domain.pk.PkDynamic.*;
import com.union.app.plateform.storgae.RedisUtil;
import com.union.app.service.pk.dynamic.DynamicKeyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserPkDynamicService {




    @Autowired
    RedisUtil redisUtil;


    /**
     * Map数据结构
     * @param pkId
     * @param userId
     * @return
     */
    public UserPkDynamic getUserPkDynamic(String pkId, String userId) {
        long follow = this.getUserPkDynamic_follow(pkId,userId);
        long share = this.getUserPkDynamic_share(pkId,userId);
        long totalShare = this.getUserPkDynamic_totalShare(pkId,userId);

        UserPkDynamic userPkDynamic = new UserPkDynamic();

        userPkDynamic.setFollow(follow);
        userPkDynamic.setShare(share);
        userPkDynamic.setTotalShare(totalShare);

        return userPkDynamic;
    }

    private long getUserPkDynamic_follow(String pkId, String userId){ long follow = (Long)redisUtil.hget(DynamicKeyName.userPk_Follow_KeyName(pkId),userId);return follow; }
    private long getUserPkDynamic_share(String pkId, String userId){ long share = (Long)redisUtil.hget(DynamicKeyName.userPk_Share_KeyName(pkId),userId);return share; }
    private long getUserPkDynamic_totalShare(String pkId, String userId){ long totalShare = (Long)redisUtil.hget(DynamicKeyName.userPk_TotalShare_KeyName(pkId),userId);return totalShare; }


    public long 递增Follow(String pkId, String userId){redisUtil.hincr(DynamicKeyName.userPk_Follow_KeyName(pkId),userId,1);return this.getUserPkDynamic_follow(pkId,userId);}
    public long 递增Share(String pkId, String userId){redisUtil.hincr(DynamicKeyName.userPk_Share_KeyName(pkId),userId,1);return this.getUserPkDynamic_share(pkId,userId);}
    public long 递增TotalShare(String pkId, String userId){redisUtil.hincr(DynamicKeyName.userPk_TotalShare_KeyName(pkId),userId,1);return this.getUserPkDynamic_totalShare(pkId,userId);}


    public long 递减Follow(String pkId, String userId){redisUtil.hdecr(DynamicKeyName.userPk_Follow_KeyName(pkId),userId,-1);return this.getUserPkDynamic_follow(pkId,userId);}





}
