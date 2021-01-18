package com.union.app.service.pk.service.pkuser;

import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.entity.用户.UserDynamicEntity;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserDynamicService {



    @Autowired
    AppDaoService daoService;


    @Autowired
    UserService userService;












    private UserDynamicEntity 初始化表(String userId){
        UserDynamicEntity userDynamicEntity = new UserDynamicEntity();
        userDynamicEntity.setUserId(userId);
        userDynamicEntity.setPostTimes(0);
        userDynamicEntity.setCollectTimes(0);
        userDynamicEntity.setFindTimeLength(0);
        userDynamicEntity.setPkTimes(0);
        userDynamicEntity.setUserCard(null);
        return userDynamicEntity;
    }

    private UserDynamicEntity queryUserDynamicEntity(String userId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("userId", CompareTag.Equal,userId);
        UserDynamicEntity userkvEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);

        return userkvEntity;
    }






    public void 用户总打榜次数减一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);

        if(!ObjectUtils.isEmpty(userDynamicEntity))
        {
            userDynamicEntity.setPostTimes(userDynamicEntity.getPostTimes()<1?0:userDynamicEntity.getPostTimes()-1);
            daoService.updateEntity(userDynamicEntity);
        }
        else
        {
            userDynamicEntity = 初始化表(userId);
            daoService.insertEntity(userDynamicEntity);
        }


    }

    public void 用户总打榜次数加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);

        if(!ObjectUtils.isEmpty(userDynamicEntity))
        {
            userDynamicEntity.setPostTimes(userDynamicEntity.getPostTimes()+1);
            daoService.updateEntity(userDynamicEntity);
        }
        else
        {
            userDynamicEntity = 初始化表(userId);
            userDynamicEntity.setPostTimes(1);
            daoService.insertEntity(userDynamicEntity);
        }
    }



}
