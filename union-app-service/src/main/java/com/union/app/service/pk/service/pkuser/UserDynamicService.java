package com.union.app.service.pk.service.pkuser;

import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.PkDynamic.UserDynamic;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.KeyService;
import com.union.app.service.pk.service.LockService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDynamicService {



    @Autowired
    AppDaoService daoService;


    @Autowired
    UserService userService;



    @Autowired
    LockService lockService;

    @Autowired
    AppService appService;

    @Autowired
    KeyService keyService;


    public UserDynamic queryUserDynamic(String userId)
    {
        UserDynamicEntity userDynamicEntity = this.queryUserDynamicEntity(userId);
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setUserId(userDynamicEntity.getUserId());
        userDynamic.setUserBack(userDynamicEntity.getUserBack());
        userDynamic.setCollectTimes(userDynamicEntity.getCollectTimes());
        userDynamic.setFans(keyService.queryKey(userId, KeyType.用户粉丝));
        userDynamic.setFollow(userDynamicEntity.getFollow());
        userDynamic.setMygroups(userDynamicEntity.getMygroups());
        userDynamic.setPk(userDynamicEntity.getPk());
        userDynamic.setPkTimes(userDynamicEntity.getPkTimes());
        userDynamic.setPostTimes(userDynamicEntity.getPostTimes());
        userDynamic.setUnLockTimes(userDynamicEntity.getUnLockTimes());
        userDynamic.setFindTimeLength(userDynamicEntity.getFindTimeLength());

        return userDynamic;

    }

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
        userkvEntity.setUserBack(appService.查询背景(9));
        userkvEntity.setMygroups(0);
        userkvEntity.setPostTimes(0);
        userkvEntity.setPkTimes(0);
        userkvEntity.setUnLockTimes(0);
        userkvEntity.setCollectTimes(0);
        userkvEntity.setFindTimeLength(0);
        userkvEntity.setFans(0);
        userkvEntity.setFollow(0);
        userkvEntity.setPk(0);
        daoService.insertEntity(userkvEntity);


    }

    public void 用户解锁群组加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setUnLockTimes(userDynamicEntity.getUnLockTimes()+1);
        daoService.updateEntity(userDynamicEntity);


    }


    public void 邀请次数加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setCollectTimes(userDynamicEntity.getCollectTimes()+1);
        daoService.updateEntity(userDynamicEntity);
    }

    public void 邀请次数减一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setCollectTimes(userDynamicEntity.getCollectTimes()-1);
        daoService.updateEntity(userDynamicEntity);
    }

    public void 用户卡点加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setPkTimes(userDynamicEntity.getPkTimes()+1);
        daoService.updateEntity(userDynamicEntity);

    }


    public void 用户关注数量加一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setFollow(userDynamicEntity.getFollow()+1);
        daoService.updateEntity(userDynamicEntity);


    }
    public void 用户关注数量减一(String userId) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setFollow(userDynamicEntity.getFollow()-1);
        daoService.updateEntity(userDynamicEntity);
    }




    public void 用户粉丝数量加一(String userId) throws AppException {

        keyService.用户粉丝加一(userId);

//        if(lockService.getLock(userId, LockType.用户粉丝数量)) {
//            UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
//            userDynamicEntity.setFans(userDynamicEntity.getFans() + 1);
//            daoService.updateColumById(UserDynamicEntity.class,"userId",userDynamicEntity.getUserId(),"fans",userDynamicEntity.getFans());
//            lockService.releaseLock(userId,LockType.用户粉丝数量);
//        }
//        else
//        {
//            throw AppException.buildException(PageAction.信息反馈框("系统错误","系统错误"));
//        }
    }


    public void 用户粉丝数量减一(String userId) throws AppException {
        keyService.用户粉丝减一(userId);


    }

    public void 修改背景(String userId, String url) {
        UserDynamicEntity userDynamicEntity = queryUserDynamicEntity(userId);
        userDynamicEntity.setUserBack(url);
        daoService.updateEntity(userDynamicEntity);


    }
}
