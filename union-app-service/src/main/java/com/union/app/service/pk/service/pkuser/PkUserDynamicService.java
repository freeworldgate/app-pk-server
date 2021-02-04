package com.union.app.service.pk.service.pkuser;

import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.排名.UserSort;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PkUserDynamicService {



    @Autowired
    AppDaoService daoService;


    @Autowired
    UserService userService;




    public List<UserSort> 查询卡点打卡排名(String pkId, int page)
    {
        List<UserSort> sorts = new ArrayList<>();
        List<PkUserDynamicEntity> dynamicEntities = this.查询排名信息Entity(pkId,page);
        dynamicEntities.forEach(dynamic->{
            UserSort userSort = new UserSort();
            userSort.setPkId(dynamic.getPkId());
            userSort.setPostTimes(dynamic.getPostTimes());
            userSort.setUser(userService.queryUser(dynamic.getUserId()));
            sorts.add(userSort);
        });
        return sorts;
    }


    public PkUserDynamicEntity initEntity(String pkId, String userId){
        PkUserDynamicEntity pkUserEntity = new PkUserDynamicEntity();
        pkUserEntity.setPkId(pkId);
        pkUserEntity.setUserId(userId);
        pkUserEntity.setPostTimes(0);
        pkUserEntity.setUnLockGroups(0);
        daoService.insertEntity(pkUserEntity);
        return pkUserEntity;
    }

    public PkUserDynamicEntity 初始化Entity(String pkId, String userId){
        PkUserDynamicEntity pkUserEntity = new PkUserDynamicEntity();
        pkUserEntity.setPkId(pkId);
        pkUserEntity.setUserId(userId);
        pkUserEntity.setPostTimes(0);
        pkUserEntity.setUnLockGroups(0);
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



    public void 卡点用户解锁群组加一(String pkId,String userId) {
        PkUserDynamicEntity pkUserEntity = 查询卡点用户动态表(pkId,userId);
        if(!ObjectUtils.isEmpty(pkUserEntity))
        {

            Map<String,Object> map = new HashMap<>();
            map.put("unLockGroups",pkUserEntity.getUnLockGroups()+1);
            daoService.updateColumById(pkUserEntity.getClass(),"dynamicId",pkUserEntity.getDynamicId(),map);

        }
        else
        {
            pkUserEntity = 初始化Entity(pkId,userId);
            pkUserEntity.setUnLockGroups(1);
            daoService.insertEntity(pkUserEntity);
        }
    };



    public void 卡点用户打卡次数加一(String pkId,String userId) {
        PkUserDynamicEntity pkUserEntity = 查询卡点用户动态表(pkId,userId);
        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            Map<String,Object> map = new HashMap<>();
            map.put("postTimes",pkUserEntity.getPostTimes()+1);
            daoService.updateColumById(pkUserEntity.getClass(),"dynamicId",pkUserEntity.getDynamicId(),map);
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
            Map<String,Object> map = new HashMap<>();
            map.put("postTimes",pkUserEntity.getPostTimes()-1<0?0:pkUserEntity.getPostTimes()-1);
            daoService.updateColumById(PkUserDynamicEntity.class,"dynamicId",pkUserEntity.getDynamicId(),map);
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
            Map<String,Object> map = new HashMap<>();
            map.put("lastPublishPostTime",System.currentTimeMillis());
            daoService.updateColumById(pkUserEntity.getClass(),"dynamicId",pkUserEntity.getDynamicId(),map);
        }
        else
        {
            pkUserEntity = 初始化Entity(pkId,userId);
            pkUserEntity.setLastPublishPostTime(System.currentTimeMillis());
            daoService.insertEntity(pkUserEntity);
        }


    }


    public int 计算打卡次数(String pkId, String userId) {
        PkUserDynamicEntity pkUserEntity = 查询卡点用户动态表(pkId,userId);

        if(!ObjectUtils.isEmpty(pkUserEntity))
        {
            return pkUserEntity.getPostTimes();
        }
        else
        {
            pkUserEntity = 初始化Entity(pkId,userId);
            daoService.insertEntity(pkUserEntity);
            return 0;
        }

    }
}
