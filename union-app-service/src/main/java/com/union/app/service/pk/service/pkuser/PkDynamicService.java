package com.union.app.service.pk.service.pkuser;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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


    public void 卡点打卡人数更新(String pkId,String userId) {
        int time = 0;
        PkUserDynamicEntity pkUserDynamicEntity = pkUserDynamicService.查询卡点用户动态表(pkId,userId);
        if(!ObjectUtils.isEmpty(pkUserDynamicEntity) && pkUserDynamicEntity.getPostTimes() > 1)
        {
            //第一次打卡，所以人数加一

        }

    }

}
