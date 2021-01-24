package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.pk.交友.PkGroup;
import com.union.app.entity.pk.社交.GroupStatu;
import com.union.app.entity.pk.社交.PkGroupEntity;
import com.union.app.entity.pk.社交.PkGroupMemberEntity;
import com.union.app.entity.用户.UserDynamicEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {




    @Autowired
    PayService payService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    GroupService appService;

    @Autowired
    PkService pkService;

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
    PostCacheService postCacheService;

    @Autowired
    ApproveService approveService;

    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    MediaService mediaService;

    @Autowired
    PkDataService pkDataService;

    @Autowired
    LocationService locationService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @Autowired
    UserDynamicService userDynamicService;

    @Autowired
    LockService lockService;

    @Autowired
    KeyService keyService;

    public void 创建群组(String pkId, String userId, String url, String groupName, String groupDesc) throws AppException {
        PkGroupEntity pkGroupEntity = this.查询用户群组Entity(pkId,userId);
        if(!ObjectUtils.isEmpty(pkGroupEntity)){return;}
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(userId);
        if(userDynamicEntity.getMygroups()<1)
        {
            throw AppException.buildException(PageAction.执行处理器("groupPay",""));
        }
        userDynamicEntity.setMygroups(userDynamicEntity.getMygroups()-1);
        daoService.updateEntity(userDynamicEntity);
        pkGroupEntity = new PkGroupEntity();
        pkGroupEntity.setPkId(pkId);
        pkGroupEntity.setPkName(locationService.querySinglePkEntity(pkId).getName());
        pkGroupEntity.setUserId(userId);
        pkGroupEntity.setGroupName(groupName);
        pkGroupEntity.setGroupDesc(groupDesc);
        pkGroupEntity.setGroupCode(url);
        pkGroupEntity.setMembers(1);
        pkGroupEntity.setGroupStatu(GroupStatu.审核中);
        pkGroupEntity.setTime(System.currentTimeMillis());
        pkGroupEntity.setLastUpdateTime(System.currentTimeMillis());
        daoService.insertEntity(pkGroupEntity);
    }

    public PkGroup 查询用户群组(String pkId, String userId) {
        PkGroupEntity pkGroupEntity = this.查询用户群组Entity(pkId,userId);
        if(ObjectUtils.isEmpty(pkGroupEntity)){return null;}
        PkGroup pkGroup = this.translate(pkGroupEntity);
        return pkGroup;
    }

    private PkGroup translate(PkGroupEntity pkGroupEntity) {
        PkGroup pkGroup = new PkGroup();
        pkGroup.setPkId(pkGroupEntity.getPkId());
        pkGroup.setPkName(pkGroupEntity.getPkName());
        pkGroup.setGroupCard(pkGroupEntity.getGroupCode());
        pkGroup.setGroupName(pkGroupEntity.getGroupName());
        pkGroup.setGroupDesc(pkGroupEntity.getGroupDesc());
        pkGroup.setUser(userService.queryUser(pkGroupEntity.getUserId()));
        pkGroup.setGroupId(pkGroupEntity.getGroupId());
        pkGroup.setMembers(keyService.queryKey(String.valueOf(pkGroupEntity.getGroupId()), KeyType.群组成员));
        pkGroup.setMember1(userService.queryUser(pkGroupEntity.getUser1()));
        pkGroup.setMember2(userService.queryUser(pkGroupEntity.getUser2()));
        pkGroup.setStatu(getStatu(pkGroupEntity));
        pkGroup.setTime(TimeUtils.convertTime(pkGroupEntity.getTime()));
        pkGroup.setLastUpdateTime(TimeUtils.convertTime(pkGroupEntity.getLastUpdateTime()));
        pkGroup.setTip(getTip(pkGroupEntity.getMembers(),pkGroupEntity.getLastUpdateTime()));
        return pkGroup;

    }

    private KeyValuePair getStatu(PkGroupEntity pkGroupEntity) {
        if(pkGroupEntity.getMembers() >=200)
        {
            return new KeyValuePair(GroupStatu.已过期.getStatu(),GroupStatu.已过期.getStatuStr());
        }
        return new KeyValuePair(pkGroupEntity.getGroupStatu().getStatu(),pkGroupEntity.getGroupStatu().getStatuStr());

    }

    private String getTip(int members, long lastUpdateTime) {
        if(members >= 200)
        {
            return "群组已满";
        }
        if((lastUpdateTime < System.currentTimeMillis() - 5*24*3600*1000)&&(lastUpdateTime > System.currentTimeMillis() - 7*24*3600*1000))
        {
            return "二维码即将过期，请更新";
        }
        if(lastUpdateTime < System.currentTimeMillis() - 7*24*3600*1000)
        {
            return "二维码已过期，请更新";
        }
        return "";



    }

    private PkGroupEntity 查询用户群组Entity(String pkId, String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupEntity.class)
                .compareFilter("pkId", CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("userId", CompareTag.Equal,userId);
        PkGroupEntity pkGroupEntity = daoService.querySingleEntity(PkGroupEntity.class,filter);
        return pkGroupEntity;
    }
    private List<PkGroupEntity> 查询有效群组Entities(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupEntity.class)
                .compareFilter("pkId", CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("members",CompareTag.Small,200)
                .andFilter()
                .compareFilter("groupStatu",CompareTag.Equal,GroupStatu.已通过)
                .andFilter()
                .compareFilter("lastUpdateTime",CompareTag.Bigger,System.currentTimeMillis()-5*24*3600*1000)
                .pageLimitFilter(1,5)
                .orderByFilter("time", OrderTag.ASC);

        List<PkGroupEntity> pkGroupEntities = daoService.queryEntities(PkGroupEntity.class,filter);
        return pkGroupEntities;
    }
    public void 删除群组(String pkId, String userId) throws AppException {
        PkGroupEntity pkGroupEntity = 查询用户群组Entity(pkId,userId);
        if(pkGroupEntity.getGroupStatu() != GroupStatu.审核中){
            throw AppException.buildException(PageAction.信息反馈框("当前状态不支持删除操作","当前状态不支持删除操作"));
        }
        daoService.deleteEntity(pkGroupEntity);
    }

    public List<PkGroup> 查询有效群组(String pkId,String userId) {
        List<PkGroup> pkGroups = new ArrayList<>();
        List<Object> groupIds = new ArrayList<>();
        //选择要展示的群组
        List<PkGroupEntity> pkGroupEntities = 查询有效群组Entities(pkId);
        for(PkGroupEntity pkGroupEntity:pkGroupEntities)
        {
            groupIds.add(pkGroupEntity.getGroupId());
            pkGroups.add(this.translate(pkGroupEntity));
        }
        if(!CollectionUtils.isEmpty(groupIds)){
            //查询用户已解锁的群组
            Map<Integer,PkGroupMemberEntity>  pkGroupMemberEntityMap = 查询已解锁群(groupIds,userId);
            for(PkGroup pkGroup:pkGroups)
            {
                //添加解锁标志位。
                pkGroup.setQueryerMemberEntity(pkGroupMemberEntityMap.get(pkGroup.getGroupId()));
            }
        }

        return pkGroups;
    }

    private Map<Integer,PkGroupMemberEntity> 查询已解锁群(List<Object> groupIds, String userId) {
        Map<Integer,PkGroupMemberEntity> pkGroupMemberEntityHashMap = new HashMap<>();
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupMemberEntity.class)
                .compareFilter("userId", CompareTag.Equal,userId)
                .andFilter()
                .inFilter("groupId", groupIds);
        List<PkGroupMemberEntity> pkGroupMemberEntities = daoService.queryEntities(PkGroupMemberEntity.class,filter);
        pkGroupMemberEntities.forEach(pkGroupMemberEntity -> {
            pkGroupMemberEntityHashMap.put(pkGroupMemberEntity.getGroupId(),pkGroupMemberEntity);
        });
        return pkGroupMemberEntityHashMap;


    }

    public void 更新群二维码(String pkId, String userId, String groupCard) throws AppException {
        PkGroupEntity pkGroupEntity = 查询用户群组Entity(pkId,userId);
        if(pkGroupEntity.getMembers()>=200)
        {
            throw AppException.buildException(PageAction.信息反馈框("满员200后不再更新","满员200后不再更新"));
        }
        if(pkGroupEntity.getGroupStatu() != GroupStatu.已通过)
        {
            throw AppException.buildException(PageAction.信息反馈框("当前状态不支持更新二维码","当前状态不支持更新二维码"));
        }
        if(pkGroupEntity.getLastUpdateTime() > System.currentTimeMillis() - 3 * 24 * 3600 * 1000 )
        {
            //每次更新间隔至少三天
            throw AppException.buildException(PageAction.信息反馈框("每次更新间隔至少三天","每次更新间隔至少三天"));
        }
        pkGroupEntity.setGroupCode(groupCard);
        pkGroupEntity.setLastUpdateTime(System.currentTimeMillis());
        daoService.updateEntity(pkGroupEntity);


    }

    public List<PkGroup> 查询我的Groups(String userId, int page) {
        List<PkGroup> pkGroups = new ArrayList<>();
        List<PkGroupEntity> pkGroupEntities = this.查询我的群组Entities(userId,page);
        pkGroupEntities.forEach(pkGroupEntity -> {
            pkGroups.add(this.translate(pkGroupEntity));
        });
        return pkGroups;
    }
    private List<PkGroupEntity> 查询我的群组Entities(String userId,int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupEntity.class)
                .compareFilter("userId", CompareTag.Equal,userId)
                .pageLimitFilter(page,10)
                .orderByFilter("lastUpdateTime", OrderTag.DESC);
        List<PkGroupEntity> pkGroupEntities = daoService.queryEntities(PkGroupEntity.class,filter);
        return pkGroupEntities;
    }

    public List<PkGroup> 查询已解锁Groups(String userId, int page) {

        List<Object> groupIds = 查询Member所在Group(userId,page);
        List<PkGroup> pkGroups = 查询GroupList(groupIds);
        return pkGroups;




    }

    private List<PkGroup> 查询GroupList(List<Object> groupIds) {
        List<PkGroup> pkGroups = new ArrayList<>();
        if(CollectionUtils.isEmpty(groupIds)){return pkGroups;}
        List<PkGroupEntity> pkGroupEntities = this.查询GroupListEntity(groupIds);
        pkGroupEntities.forEach(pkGroupEntity -> {
            pkGroups.add(this.translate(pkGroupEntity));
        });
        return pkGroups;
    }

    private List<PkGroupEntity> 查询GroupListEntity(List<Object> groupIds) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupEntity.class)
                .inFilter("groupId", groupIds);
        List<PkGroupEntity> pkGroupEntities = daoService.queryEntities(PkGroupEntity.class,filter);
        return pkGroupEntities;


    }

    private List<Object> 查询Member所在Group(String userId, int page) {
        List<Object> groupIds = new ArrayList<>();
        List<PkGroupMemberEntity>  pkGroupMemberEntities = this.查询所在群组PkGroupMemberEntity(userId,page);
        pkGroupMemberEntities.forEach(pkGroupMemberEntity -> {
            groupIds.add(pkGroupMemberEntity.getGroupId());
        });
        return groupIds;
    }

    private List<PkGroupMemberEntity> 查询所在群组PkGroupMemberEntity(String userId, int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupMemberEntity.class)
                .compareFilter("userId", CompareTag.Equal,userId)
                .pageLimitFilter(page,10)
                .orderByFilter("time", OrderTag.DESC);
        List<PkGroupMemberEntity> pkGroupMemberEntities = daoService.queryEntities(PkGroupMemberEntity.class,filter);
        return pkGroupMemberEntities;


    }
    private List<PkGroupMemberEntity> 查询所在群组PkGroupMemberEntity(String pkId, String userId, int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupMemberEntity.class)
                .compareFilter("userId", CompareTag.Equal,userId)
                .andFilter()
                .compareFilter("pkId", CompareTag.Equal,pkId)
                .pageLimitFilter(page,10)
                .orderByFilter("time", OrderTag.DESC);
        List<PkGroupMemberEntity> pkGroupMemberEntities = daoService.queryEntities(PkGroupMemberEntity.class,filter);
        return pkGroupMemberEntities;

    }
    public List<PkGroup> 查询已解锁PkGroups(String pkId,String userId, int page) {
        List<Object> groupIds = 查询Member所在Group(pkId,userId,page);
        List<PkGroup> pkGroups = 查询GroupList(groupIds);
        return pkGroups;
    }

    private List<Object> 查询Member所在Group(String pkId, String userId, int page) {
        List<Object> groupIds = new ArrayList<>();
        List<PkGroupMemberEntity>  pkGroupMemberEntities = this.查询所在群组PkGroupMemberEntity(pkId,userId,page);
        pkGroupMemberEntities.forEach(pkGroupMemberEntity -> {
            groupIds.add(pkGroupMemberEntity.getGroupId());
        });
        return groupIds;
    }


    public PkGroupMemberEntity 解锁群组(int groupId, String userId) throws AppException {

        PkGroupMemberEntity  pkGroupMemberEntity = this.查询用户所在群组PkGroupMemberEntity(groupId,userId);
        if(ObjectUtils.isEmpty(pkGroupMemberEntity))
        {

                PkGroupEntity pkGroupEntity = 查询用户群组EntityById(groupId);
                添加Member(pkGroupEntity,userId);
                pkGroupMemberEntity = new PkGroupMemberEntity();
                pkGroupMemberEntity.setGroupId(pkGroupEntity.getGroupId());
                pkGroupMemberEntity.setPkId(pkGroupEntity.getPkId());
                pkGroupMemberEntity.setUserId(userId);
                pkGroupMemberEntity.setTime(System.currentTimeMillis());
                pkUserDynamicService.卡点用户解锁群组加一(pkGroupEntity.getPkId(),userId);
                userDynamicService.用户解锁群组加一(userId);
                keyService.群组成员加一(groupId);
                daoService.insertEntity(pkGroupMemberEntity);



        }
        return pkGroupMemberEntity;
    }
    private void 添加Member(PkGroupEntity pkGroupEntity, String userId) {
        if(StringUtils.isBlank(pkGroupEntity.getUser1()))
        {
            pkGroupEntity.setUser1(userId);
        }
        else if(StringUtils.isBlank(pkGroupEntity.getUser2()))
        {
            pkGroupEntity.setUser2(userId);
        }
        else
        {

        }

    }
    private PkGroupMemberEntity 查询用户所在群组PkGroupMemberEntity(Integer groupId, String userId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupMemberEntity.class)
                .compareFilter("groupId", CompareTag.Equal,groupId)
                .andFilter()
                .compareFilter("userId", CompareTag.Equal,userId);
        PkGroupMemberEntity pkGroupMemberEntity = daoService.querySingleEntity(PkGroupMemberEntity.class,filter);
        return pkGroupMemberEntity;

    }

    public PkGroupEntity 查询用户群组EntityById(int groupId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkGroupEntity.class)
                .compareFilter("groupId", CompareTag.Equal,groupId);
        PkGroupEntity pkGroupEntity = daoService.querySingleEntity(PkGroupEntity.class,filter);
        return pkGroupEntity;
    }



}
