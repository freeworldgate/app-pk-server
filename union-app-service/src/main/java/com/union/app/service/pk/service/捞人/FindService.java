package com.union.app.service.pk.service.捞人;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.common.dao.PkCacheService;
import com.union.app.common.redis.RedisMapService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.pk.捞人.CreateUserFind;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.kadian.捞人.FindStatu;
import com.union.app.entity.pk.kadian.捞人.FindUserEntity;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

@Service
public class FindService {


    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    RedisMapService redisMapService;


    @Autowired
    AppDaoService daoService;

    @Autowired
    FindService pkService;

    @Autowired
    AppService appService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    LocationService locationService;

    @Autowired
    KeyService keyService;

    @Autowired
    UserDynamicService userDynamicService;

    public FindUser 查询用户捞人记录(String pkId, String userId) throws IOException {
            FindUserEntity findUserEntity = this.查询用户捞人Entity(pkId,userId);

            return translate(findUserEntity);




    }
    public FindUser translate(FindUserEntity findUserEntity)
    {
        if(ObjectUtils.isEmpty(findUserEntity)){return null;}
        FindUser findUser = new FindUser();
//            PkDetail pk = locationService.querySinglePk(pkId);
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(findUserEntity.getUserId());
        User user = userService.queryUser(findUserEntity.getUserId());
        findUser.setUser(user);
        findUser.setLeftTime(TimeUtils.剩余可打捞时间(userDynamicEntity.getFindTimeLength()));
        findUser.setPkId(findUserEntity.getPkId());

        findUser.setPkName(findUserEntity.getPkName());


        findUser.setFindLength(findUserEntity.getFindLength());
        findUser.setImg1(findUserEntity.getImg1());
        findUser.setImg2(findUserEntity.getImg2());
        findUser.setImg3(findUserEntity.getImg3());
        findUser.setText(findUserEntity.getText());
        findUser.setStartTime(TimeUtils.全局时间(findUserEntity.getStartTime()));
        findUser.setEndTime(TimeUtils.全局时间(findUserEntity.getEndTime()));
        long current = System.currentTimeMillis();
        if(findUserEntity.getEndTime() > 0 && current > findUserEntity.getEndTime())
        {
            findUser.setStatu(new KeyValuePair(FindStatu.已过期.getStatu(),FindStatu.已过期.getStatuStr()));
        }
        else
        {
            findUser.setStatu(new KeyValuePair(findUserEntity.getFindStatu().getStatu(),findUserEntity.getFindStatu().getStatuStr()));
        }

        findUser.setTimeExpire(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
        return findUser;


    }


//
//    public void 保存捞人信息(CreateUserFind createUserFind) throws AppException {
//        FindUserEntity findUserEntity = this.查询用户捞人Entity(createUserFind.getPkId(),createUserFind.getUserId());
//        if(ObjectUtils.isEmpty(findUserEntity)){
//            findUserEntity = new FindUserEntity();
//            findUserEntity.setPkId(createUserFind.getPkId());
//            PkEntity pkEntity = locationService.querySinglePkEntity(createUserFind.getPkId());
//            findUserEntity.setPkName(pkEntity.getName());
//            findUserEntity.setUserId(createUserFind.getUserId());
//            findUserEntity.setFindLength(createUserFind.getFindLength());
//            findUserEntity.setText(createUserFind.getText());
//            findUserEntity.setImg1(createUserFind.getImg1());
//            findUserEntity.setImg2(createUserFind.getImg2());
//            findUserEntity.setImg3(createUserFind.getImg3());
//            findUserEntity.setApproverId(null);
//            findUserEntity.setStartTime(0);
//            findUserEntity.setEndTime(0);
//            findUserEntity.setFindStatu(FindStatu.新创建);
//            findUserEntity.setCreateTime(System.currentTimeMillis());
//            daoService.insertEntity(findUserEntity);
//
//        }
//        else {
//            if(findUserEntity.getFindStatu() != FindStatu.新创建)
//            {
//                throw AppException.buildException(PageAction.信息反馈框("无法保存","当前状态不支持修改"));
//            }
//            findUserEntity.setFindLength(createUserFind.getFindLength());
//            findUserEntity.setText(createUserFind.getText());
//            findUserEntity.setImg1(createUserFind.getImg1());
//            findUserEntity.setImg2(createUserFind.getImg2());
//            findUserEntity.setImg3(createUserFind.getImg3());
//            findUserEntity.setApproverId(null);
//            findUserEntity.setStartTime(0);
//            findUserEntity.setEndTime(0);
//            findUserEntity.setFindStatu(FindStatu.新创建);
//            findUserEntity.setCreateTime(System.currentTimeMillis());
//            daoService.updateEntity(findUserEntity);
//        }
//
//    }

    public void 开始捞人(CreateUserFind createUserFind) throws AppException {

            PkEntity pkEntity = locationService.querySinglePkEntity(createUserFind.getPkId());
            FindUserEntity findUserEntity = new FindUserEntity();
            findUserEntity.setPkId(pkEntity.getPkId());
            findUserEntity.setPkName(pkEntity.getName());
            findUserEntity.setUserId(createUserFind.getUserId());
            findUserEntity.setFindLength(createUserFind.getFindLength());
            findUserEntity.setText(createUserFind.getText());
            findUserEntity.setImg1(createUserFind.getImg1());
            findUserEntity.setImg2(createUserFind.getImg2());
            findUserEntity.setImg3(createUserFind.getImg3());
            findUserEntity.setApproverId(null);
            findUserEntity.setStartTime(0);
            findUserEntity.setEndTime(0);
            findUserEntity.setFindStatu(FindStatu.审核中);

            daoService.insertEntity(findUserEntity);

    }

    public void 校验时间(int findLength, String userId) throws AppException {


        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);

        if(userDynamicEntity.getFindTimeLength() >= findLength * 24 * 3600*1000)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("findTimeLength",userDynamicEntity.getFindTimeLength() - findLength * 24 * 3600*1000);
            daoService.updateColumById(userDynamicEntity.getClass(),"userId",userDynamicEntity.getUserId(),map);

            return;
        }
        else
        {
            throw AppException.buildException(PageAction.执行处理器("timePay",TimeUtils.剩余可打捞时间(userDynamicEntity.getFindTimeLength())));
        }













    }
    public FindUserEntity 放弃捞人(int findId) {
        FindUserEntity findUserEntity = this.查询用户捞人EntityById(findId);

        if(!ObjectUtils.isEmpty(findUserEntity) && userService.是否是内置用户(findUserEntity.getUserId()))
        {
            daoService.deleteEntity(findUserEntity);
        }
        return  findUserEntity;
    }
    public void 放弃捞人(String pkId, String userId) {
        FindUserEntity findUserEntity = this.查询用户捞人Entity(pkId,userId);

        if(findUserEntity.getFindStatu() == FindStatu.打捞中)
        {
            userService.返还用户打捞时间(userId,findUserEntity.getEndTime());
            userDynamicService.添加用户已打捞时间(userId,findUserEntity.getStartTime());
        }

        if(findUserEntity.getFindStatu() == FindStatu.审核中)
        {
            userService.返还用户打捞时间1(userId,findUserEntity.getFindLength());
        }
        daoService.deleteEntity(findUserEntity);


    }

    public List<FindUser> 查询审核捞人记录(int page) throws IOException {
        List<FindUser> findUsers = new ArrayList<>();
        List<FindUserEntity> findUserEntities = this.查询要审核捞人Entity(page);

        for(FindUserEntity findUserEntity:findUserEntities){

            FindUser findUser = new FindUser();
            PkDetail pk = locationService.querySinglePk(findUserEntity.getPkId());
            UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(findUserEntity.getUserId());
            User user = userService.queryUser(findUserEntity.getUserId());
            findUser.setUser(user);
            findUser.setFindId(findUserEntity.getFindId());
            findUser.setTimeExpire(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
            findUser.setLeftTime(TimeUtils.剩余可打捞时间(userDynamicEntity.getFindTimeLength()));
            findUser.setPkId(findUserEntity.getPkId());
            findUser.setPk(pk);
            findUser.setPkName(pk.getName());
            findUser.setFindLength(findUserEntity.getFindLength());
            findUser.setImg1(findUserEntity.getImg1());
            findUser.setImg2(findUserEntity.getImg2());
            findUser.setImg3(findUserEntity.getImg3());
            findUser.setText(findUserEntity.getText());
            findUser.setStatu(new KeyValuePair(findUserEntity.getFindStatu().getStatu(),findUserEntity.getFindStatu().getStatuStr()));
//            findUser.setTimeLength(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
            findUsers.add(findUser);
        };
        return findUsers;
    }

    private List<FindUserEntity> 查询要审核捞人Entity(int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(FindUserEntity.class)
                .compareFilter("findStatu",CompareTag.Equal,FindStatu.审核中)
                .orderByFilter("createTime",OrderTag.DESC)
                .pageLimitFilter(page,20);
        List<FindUserEntity> findUserEntities = daoService.queryEntities(FindUserEntity.class,filter);
        return findUserEntities;

    }

    public void 审批(int findId) {
        FindUserEntity findUserEntity = 查询用户捞人EntityById(findId);
        UserDynamicEntity userDynamicEntity = userDynamicService.queryUserDynamicEntity(findUserEntity.getUserId());
        Map<String,Object> map = new HashMap<>();
        if(findUserEntity.getFindStatu() == FindStatu.审核中)
        {
            findUserEntity.setFindStatu(FindStatu.打捞中);
            long startTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis() + findUserEntity.getFindLength()*24*3600*1000;
            findUserEntity.setStartTime(startTime);
            findUserEntity.setEndTime(endTime);
            //审批通过，用户打捞次数加1
            map.put("findTimes",userDynamicEntity.getFindTimes()+1);

        }
        else
        {
            findUserEntity.setFindStatu(FindStatu.审核中);
            findUserEntity.setStartTime(0);
            findUserEntity.setEndTime(0);
            //审批不通过，用户打捞次数减1
            map.put("findTimes",userDynamicEntity.getFindTimes()-1);
        }
        daoService.updateEntity(findUserEntity);
        daoService.updateColumById(userDynamicEntity.getClass(),"userId",userDynamicEntity.getUserId(),map);



    }

    public FindUser 查询ByFindId(int findId) throws IOException {
        FindUserEntity findUserEntity = 查询用户捞人EntityById(findId);

        if(ObjectUtils.isEmpty(findUserEntity)){return null;}

        FindUser findUser = new FindUser();
        PkDetail pk = locationService.querySinglePk(findUserEntity.getPkId());
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(findUserEntity.getUserId());
        User user = userService.queryUser(findUserEntity.getUserId());
        findUser.setUser(user);
        findUser.setFindId(findUserEntity.getFindId());
        findUser.setTimeExpire(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
        findUser.setLeftTime(TimeUtils.剩余可打捞时间(userDynamicEntity.getFindTimeLength()));
        findUser.setPkId(findUserEntity.getPkId());
        findUser.setPk(pk);
        findUser.setPkName(pk.getName());
        findUser.setFindLength(findUserEntity.getFindLength());
        findUser.setImg1(findUserEntity.getImg1());
        findUser.setImg2(findUserEntity.getImg2());
        findUser.setImg3(findUserEntity.getImg3());
        findUser.setText(findUserEntity.getText());
        findUser.setStatu(new KeyValuePair(findUserEntity.getFindStatu().getStatu(),findUserEntity.getFindStatu().getStatuStr()));
//        findUser.setTimeLength(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
        return findUser;
    }
    private FindUserEntity 查询用户捞人EntityById(int findId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(FindUserEntity.class)
                .compareFilter("findId",CompareTag.Equal,findId);
        FindUserEntity findUserEntity = daoService.querySingleEntity(FindUserEntity.class,filter);
        return findUserEntity;
    }
    public FindUserEntity 查询用户捞人Entity(String pkId, String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(FindUserEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId",CompareTag.Equal,userId);
        FindUserEntity findUserEntity = daoService.querySingleEntity(FindUserEntity.class,filter);
        return findUserEntity;
    }

    public List<FindUser> 查询卡点捞人列表(String pkId) throws IOException {
        List<FindUser> findUsers = new ArrayList<>();
        List<FindUserEntity> findUserEntities = this.查询打捞中Entity(pkId);
        List<Object> userIds = 获取UserIds(findUserEntities);
        Map<String,UserDynamicEntity> userDynamicEntities = userService.批量查询用户Dynamic(userIds);
        for(FindUserEntity findUserEntity:findUserEntities){
            FindUser findUser = new FindUser();
            User user = userService.queryUser(findUserEntity.getUserId());
            findUser.setUser(user);
            findUser.setFindId(findUserEntity.getFindId());
            findUser.setTimeExpire(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
            findUser.setLeftTime(TimeUtils.剩余可打捞时间(userDynamicEntities.get(findUserEntity.getUserId()).getFindTimeLength()));
            findUser.setPkId(findUserEntity.getPkId());
            findUser.setPkName(findUserEntity.getPkName());
            findUser.setFindLength(findUserEntity.getFindLength());
            findUser.setImg1(findUserEntity.getImg1());
            findUser.setImg2(findUserEntity.getImg2());
            findUser.setImg3(findUserEntity.getImg3());
            findUser.setText(findUserEntity.getText());
            findUser.setStartTime(TimeUtils.全局时间(findUserEntity.getStartTime()));
            findUser.setEndTime(TimeUtils.全局时间(findUserEntity.getEndTime()));
            findUser.setStatu(new KeyValuePair(findUserEntity.getFindStatu().getStatu(),findUserEntity.getFindStatu().getStatuStr()));
            findUser.setTimeExpire(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
            findUsers.add(findUser);
        };
        return findUsers;




    }

    private List<Object> 获取UserIds(List<FindUserEntity> findUserEntities) {

        List<Object> ids = new ArrayList<>();

        findUserEntities.forEach(findUserEntity -> {
            ids.add(findUserEntity.getUserId());
        });
        return ids;
    }


    private List<FindUserEntity> 查询打捞中Entity(String pkId) {
        //每个卡点缓存20组
        long current = System.currentTimeMillis();


        EntityFilterChain filter = EntityFilterChain.newFilterChain(FindUserEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("endTime",CompareTag.Bigger,current)
                    .andFilter()
                    .compareFilter("startTime",CompareTag.Small,current)
                    .pageLimitFilter(1,10)
                    .orderByRandomFilter();
        List<FindUserEntity> findUserEntities = daoService.queryEntities(FindUserEntity.class,filter);

        return findUserEntities;

    }


    public void 清除UserFind(int findId) throws AppException {
        FindUserEntity findUserEntity = this.查询用户捞人EntityById(findId);
        if(!ObjectUtils.isEmpty(findUserEntity) && userService.是否是内置用户(findUserEntity.getUserId()))
        {
            daoService.deleteEntity(findUserEntity);
        }


    }

    public void 清除UserFind(String pkId, String userId) throws AppException {
        FindUserEntity findUserEntity = this.查询用户捞人Entity(pkId,userId);
        if(!ObjectUtils.isEmpty(findUserEntity))
        {

            boolean isExpired = (FindStatu.打捞中 == findUserEntity.getFindStatu()) && (findUserEntity.getEndTime() > 0 && System.currentTimeMillis() > findUserEntity.getEndTime());
            if(isExpired)
            {
                daoService.deleteEntity(findUserEntity);
            }
            else
            {
                throw AppException.buildException(PageAction.信息反馈框("","非法操作"));
            }




        }


    }

    public List<FindUser> 查询我的捞人记录(String userId, int page) {
        List<FindUser> findUsers = new ArrayList<>();
        List<FindUserEntity> findUserEntities = this.查询我的捞人Entity(userId,page);

        for(FindUserEntity findUserEntity:findUserEntities)
        {
            FindUser findUser = new FindUser();

            findUser.setPkId(findUserEntity.getPkId());
            findUser.setPkName(findUserEntity.getPkName());
            findUser.setFindLength(findUserEntity.getFindLength());
            findUser.setImg1(findUserEntity.getImg1());
            findUser.setImg2(findUserEntity.getImg2());
            findUser.setImg3(findUserEntity.getImg3());
            findUser.setText(findUserEntity.getText());
            findUser.setStartTime(TimeUtils.全局时间(findUserEntity.getStartTime()));
            findUser.setEndTime(TimeUtils.全局时间(findUserEntity.getEndTime()));
            long current = System.currentTimeMillis();
            if(findUserEntity.getEndTime() > 0 && current > findUserEntity.getEndTime())
            {
                findUser.setStatu(new KeyValuePair(FindStatu.已过期.getStatu(),FindStatu.已过期.getStatuStr()));
            }
            else
            {
                findUser.setStatu(new KeyValuePair(findUserEntity.getFindStatu().getStatu(),findUserEntity.getFindStatu().getStatuStr()));
            }
            findUser.setTimeExpire(TimeUtils.已打捞时间(findUserEntity.getStartTime()));
            findUsers.add(findUser);
        }
        return findUsers;
    }

    private List<FindUserEntity> 查询我的捞人Entity(String userId, int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(FindUserEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId)
                .pageLimitFilter(page, 20)
                .orderByFilter("createTime",OrderTag.DESC);
        List<FindUserEntity> pkEntities = daoService.queryEntities(FindUserEntity.class,filter);
        return pkEntities;

    }



}
