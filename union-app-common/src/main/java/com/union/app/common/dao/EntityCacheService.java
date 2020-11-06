package com.union.app.common.dao;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PostImage;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.用户.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.LRUMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class EntityCacheService {


    private  static Map<String,List<PostImage>> imageEntityMap = new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.POSTIMAGEEntity缓存数量));
    private  static Map<String,UserEntity> userEntityMap = new LRUMap(AppConfigService.getConfigAsInteger(ConfigItem.USEREntity缓存数量));

    private  static Map<String,PkEntity> pkEntityMap = new HashMap<>(AppConfigService.getConfigAsInteger(ConfigItem.PKEntity缓存数量));

    private static Map<String,Long> pkCacheLock = new ConcurrentHashMap<>();

    public static void lockPkEntity(String pkId)
    {
        pkCacheLock.put(pkId,System.currentTimeMillis());
    }
    public static void unlockPkEntity(String pkId)
    {
        pkCacheLock.remove(pkId);
    }
    public static PkEntity getPkEntity(String pkId)
    {
        Long lockTime = pkCacheLock.get(pkId);
        if(lockTime == null)
        {
            return pkEntityMap.get(pkId);
        }
        else
        {
            if(System.currentTimeMillis() - lockTime > 1000 * 60 * 3)
            {
                pkCacheLock.remove(pkId);
                return pkEntityMap.get(pkId);
            }
            else
            {
                pkEntityMap.remove(pkId);
                return null;
            }

        }


    }

    public static void savePk(PkEntity pkEntity)
    {
        Long lockTime = pkCacheLock.get(pkEntity.getPkId());
        if(lockTime == null)
        {
            pkEntityMap.put(pkEntity.getPkId(),pkEntity);
        }
        else
        {
            if(System.currentTimeMillis() - lockTime > 1000 * 60 * 3)
            {
                pkCacheLock.remove(pkEntity.getPkId());
                pkEntityMap.put(pkEntity.getPkId(),pkEntity);
            }

        }
    }





    public static void saveUser(UserEntity userEntity)
    {
        userEntityMap.put(userEntity.getUserId(),userEntity);

    }



    public static UserEntity getUserEntity(String userId)
    {
        return userEntityMap.get(userId);
    }


    public static List<PostImage> 查询榜帖图片(String pkId, String postId) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(pkId);
        stringBuffer.append("-");
        stringBuffer.append(postId);

        List<PostImage> postImages = imageEntityMap.get(stringBuffer.toString());
        return !CollectionUtils.isEmpty(postImages)?postImages:new ArrayList<>();

    }

    public static void 保存图片(String pkId, String postId, List<PostImage> postImages) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(pkId);
        stringBuffer.append("-");
        stringBuffer.append(postId);

        imageEntityMap.put(stringBuffer.toString(),postImages);
    }
}
