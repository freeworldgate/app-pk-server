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






    public UserDynamicEntity queryUserDynamicEntity(String userId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                .compareFilter("userId", CompareTag.Equal,userId);
        UserDynamicEntity userkvEntity = daoService.querySingleEntity(UserDynamicEntity.class,filter);

        return userkvEntity;
    }






    public void 用户总打榜次数减一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setPostTimes(userDynamicEntity.getPostTimes()<1?0:userDynamicEntity.getPostTimes()-1);
        daoService.updateEntity(userDynamicEntity);
    }

    public void 用户总打榜次数加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setPostTimes(userDynamicEntity.getPostTimes()+1);
        daoService.updateEntity(userDynamicEntity);

    }

    public void 创建Dynamic表(String userId) {
        UserDynamicEntity userkvEntity = new UserDynamicEntity();
        userkvEntity.setUserId(userId);
        userkvEntity.setMygroups(0);
        userkvEntity.setPostTimes(0);
        userkvEntity.setPkTimes(0);
        userkvEntity.setUnLockTimes(0);
        userkvEntity.setCollectTimes(0);
        userkvEntity.setFindTimeLength(0);
        userkvEntity.setPk(0);
        daoService.insertEntity(userkvEntity);


    }

    public void 用户解锁群组加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setUnLockTimes(userDynamicEntity.getUnLockTimes()+1);
        daoService.updateEntity(userDynamicEntity);


    }
}
