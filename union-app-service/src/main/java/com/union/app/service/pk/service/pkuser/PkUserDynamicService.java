package com.union.app.service.pk.service.pkuser;

import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.排名.PkDynamic;
import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PkUserDynamicService {



    @Autowired
    AppDaoService daoService;


    @Autowired
    UserService userService;




    public List<PkDynamic> 查询卡点打卡排名(String pkId, int page)
    {
        List<PkDynamic> sorts = new ArrayList<>();
        List<PkUserDynamicEntity> dynamicEntities = this.查询排名信息Entity(pkId,page);
        dynamicEntities.forEach(dynamic->{
            PkDynamic pkDynamic = new PkDynamic();
            pkDynamic.setPkId(dynamic.getPkId());
            pkDynamic.setPostTimes(dynamic.getPostTimes());
            pkDynamic.setUser(userService.queryUser(dynamic.getUserId()));
            sorts.add(pkDynamic);
        });
        return sorts;
    }




    public PkUserDynamicEntity 初始化Entity(String pkId, String userId){
        PkUserDynamicEntity pkUserEntity = new PkUserDynamicEntity();
        pkUserEntity.setPostTimes(0);
        pkUserEntity.setPkId(pkId);
        pkUserEntity.setUserId(userId);
        return pkUserEntity;
    }


    public PkUserDynamicEntity 查询卡点用户动态表(String pkId, String userId) {

        EntityFilterChain cfilter = EntityFilterChain.newFilterChain(PkUserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        PkUserDynamicEntity pkUserEntity = daoService.querySingleEntity(PkUserDynamicEntity.class,cfilter);
        return pkUserEntity;

    }






    private List<PkUserDynamicEntity> 查询排名信息Entity(String pkId, int page) {

        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkUserDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .pageLimitFilter(page,20)
                .orderByFilter("postTimes",OrderTag.DESC);
        List<PkUserDynamicEntity> userFollowEntities = daoService.queryEntities(PkUserDynamicEntity.class,filter);

        return userFollowEntities;
    }




    public void 卡点用户打卡次数加一(String pkId,String userId) {

        PkUserDynamicEntity pkUserEntity = 查询卡点用户动态表(pkId,userId);

        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            pkUserEntity.setPostTimes(pkUserEntity.getPostTimes()+1);
            daoService.updateEntity(pkUserEntity);
        }
        else
        {
            pkUserEntity = 初始化Entity(pkId,userId);
            pkUserEntity.setPostTimes(1);
            daoService.insertEntity(pkUserEntity);
        }




    };


    public void 卡点用户打卡次数减一(String pkId, String userId) {

        PkUserDynamicEntity pkUserEntity = 查询卡点用户动态表(pkId,userId);

        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            pkUserEntity.setPostTimes(pkUserEntity.getPostTimes()-1<0?0:pkUserEntity.getPostTimes()-1);
            daoService.updateEntity(pkUserEntity);
        }
        else
        {
            pkUserEntity = 初始化Entity(pkId,userId);
            daoService.insertEntity(pkUserEntity);
        }


    }






    public void 记录用户卡点打卡时间(String pkId, String userId) {

        PkUserDynamicEntity pkUserEntity = 查询卡点用户动态表(pkId,userId);

        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            pkUserEntity.setLastPublishPostTime(System.currentTimeMillis());
            daoService.updateEntity(pkUserEntity);
        }
        else
        {
            pkUserEntity = 初始化Entity(pkId,userId);
            pkUserEntity.setLastPublishPostTime(System.currentTimeMillis());
            daoService.insertEntity(pkUserEntity);
        }


    }

    public PkDynamic 查询卡点用户动态(String pkId, String userId) {
        PkUserDynamicEntity dynamic = 查询卡点用户动态表(pkId,userId);
        if(ObjectUtils.isEmpty(dynamic)){return null;}
        PkDynamic pkDynamic = new PkDynamic();
        pkDynamic.setPkId(dynamic.getPkId());
        pkDynamic.setPostTimes(dynamic.getPostTimes());
        pkDynamic.setUser(userService.queryUser(dynamic.getUserId()));

        return pkDynamic;
    }
}
