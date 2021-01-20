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
import com.union.app.entity.pk.BackImgEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.社交.GroupStatu;
import com.union.app.entity.pk.社交.PkGroupEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

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
    ComplainService complainService;

    @Autowired
    LocationService locationService;


    public void 创建群组(String pkId, String userId, String url, String groupName, String groupDesc) {
        PkGroupEntity pkGroupEntity = this.查询用户群组Entity(pkId,userId);
        if(!ObjectUtils.isEmpty(pkGroupEntity)){return;}
        pkGroupEntity = new PkGroupEntity();
        pkGroupEntity.setPkId(pkId);
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
        pkGroup.setGroupCard(pkGroupEntity.getGroupCode());
        pkGroup.setGroupName(pkGroupEntity.getGroupName());
        pkGroup.setGroupDesc(pkGroupEntity.getGroupDesc());
        pkGroup.setUser(userService.queryUser(pkGroupEntity.getUserId()));
        pkGroup.setGroupId(pkGroupEntity.getGroupId());
        pkGroup.setMembers(pkGroupEntity.getMembers());
        pkGroup.setMember1(userService.queryUser(pkGroupEntity.getUser1()));
        pkGroup.setMember2(userService.queryUser(pkGroupEntity.getUser2()));
        pkGroup.setStatu(new KeyValuePair(pkGroupEntity.getGroupStatu().getStatu(),pkGroupEntity.getGroupStatu().getStatuStr()));
        pkGroup.setTime(TimeUtils.convertTime(pkGroupEntity.getTime()));
        pkGroup.setLastUpdateTime(TimeUtils.convertTime(pkGroupEntity.getLastUpdateTime()));
        return pkGroup;

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
        List<PkGroupEntity> pkGroupEntities = 查询有效群组Entities(pkId);
        for(PkGroupEntity pkGroupEntity:pkGroupEntities)
        {
            pkGroups.add(this.translate(pkGroupEntity));
        }
        return pkGroups;
    }
}
