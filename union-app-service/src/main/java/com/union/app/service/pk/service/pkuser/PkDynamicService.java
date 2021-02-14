package com.union.app.service.pk.service.pkuser;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.PkDynamic.PkDynamic;
import com.union.app.entity.pk.PkDynamicEntity;
import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PkDynamicService {



    @Autowired
    AppDaoService daoService;

    @Autowired
    RedisMapService redisMapService;

    @Autowired
    UserService userService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @Autowired
    KeyService keyService;


    public void 卡点打卡人数更新(String pkId,String userId) {
        int time = 0;
        PkUserDynamicEntity pkUserDynamicEntity = pkUserDynamicService.查询卡点用户动态表(pkId,userId);
        if(!ObjectUtils.isEmpty(pkUserDynamicEntity) && pkUserDynamicEntity.getTotalPostTimes() == 0)
        {
            //第一次打卡，所以人数加一
            keyService.卡点打卡人数加一(pkId);
        }
    }

    public PkDynamic queryPkDynamic(String pkId) {
        PkDynamicEntity pkDynamicEntity = queryPkDynamicEntity(pkId);
        if(ObjectUtils.isEmpty(pkDynamicEntity)){return null;}
        return translate(pkDynamicEntity);
    }

    public PkDynamic translate(PkDynamicEntity pkDynamicEntity) {
        PkDynamic pkDynamic = new PkDynamic();
        pkDynamic.setPkId(pkDynamicEntity.getPkId());
        pkDynamic.setTotalImages(pkDynamicEntity.getTotalImages());
        pkDynamic.setPkFinds(pkDynamicEntity.getPkFinds());
        pkDynamic.setPkGroups(pkDynamicEntity.getPkGroups());
        pkDynamic.setTotalPosts(keyService.queryKey(pkDynamicEntity.getPkId(), KeyType.卡点POST));
        pkDynamic.setTotalUsers(keyService.queryKey(pkDynamicEntity.getPkId(), KeyType.卡点人数));
        return pkDynamic;
    }


    public PkDynamicEntity queryPkDynamicEntity(String pkId) {
        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkDynamicEntity.class)
                .compareFilter("pkId", CompareTag.Equal,pkId);
        PkDynamicEntity pkUserEntity = daoService.querySingleEntity(PkDynamicEntity.class,cfilter);
        return pkUserEntity;

    }


    public void 创建DynamicEntity(String pkId) {
        PkDynamicEntity pkUserEntity = queryPkDynamicEntity(pkId);
        if(ObjectUtils.isEmpty(pkUserEntity))
        {
            pkUserEntity = new PkDynamicEntity();
            pkUserEntity.setPkId(pkId);
            pkUserEntity.setPkFinds(0);
            pkUserEntity.setPkGroups(0);
            pkUserEntity.setTotalImages(0);
            daoService.insertEntity(pkUserEntity);
        }


    }


    public void 批量查询动态表(List<Object> pkIds, List<PkDetail> pkDetails) {
        Map<String,PkDynamic> pkDynamicEntityMap = new HashMap<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkDynamicEntity.class)
                .inFilter("pkId",pkIds);
        List<PkDynamicEntity> pkDynamicEntities = daoService.queryEntities(PkDynamicEntity.class,filter);
        pkDynamicEntities.forEach(pkDynamicEntity -> {
            pkDynamicEntityMap.put(pkDynamicEntity.getPkId(),this.translate(pkDynamicEntity));
        });
        pkDetails.forEach(pkDetail -> {
            pkDetail.setPkDynamic(pkDynamicEntityMap.get(pkDetail.getPkId()));
        });
    }
}
