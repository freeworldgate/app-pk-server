package com.union.app.service.pk.service;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.OSS存储.SceneType;
import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.task.PkTaskEntity;
import com.union.app.entity.pk.task.TaskStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyName;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisMapService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PkService {


    @Autowired
    RedisSortSetService redisSortSetService;

    @Autowired
    RedisMapService redisMapService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    PkService pkService;

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
    PostCacheService postCacheService;

    @Autowired
    ApproveService approveService;

    public static Map<String,List<FeeTask>> taskCache = new ConcurrentHashMap<>();

    public static Map<String,List<FactualInfo>> factualCache = new ConcurrentHashMap<>();



    public List<Post> queryPkPost(String userId,String pkId,int page) throws IOException {
        List<Post> posts = postCacheService.getPostPage(userId,pkId,page);

        return posts;
    }


    public PkDetail querySinglePk(String pkId) throws IOException {
        PkDetail pkDetail = new PkDetail();
        PkEntity pk = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pk)){return null;}
        pkDetail.setPkId(pk.getPkId());
        pkDetail.setUserBack(appService.查询背景(8));
        pkDetail.setPkTypeValue(pk.getPkType().getType());
        pkDetail.setPkType(pk.getPkType().getDesc());
//        pkDetail.setTopic(new String(ArrayUtils.isEmpty(pk.getTopic())?"...".getBytes("UTF-8"):pk.getTopic(),"UTF-8"));
        pkDetail.setTopic(pk.getTopic());
        pkDetail.setUser(userService.queryUser(pk.getUserId()));
        pkDetail.setWatchWord(pk.getWatchWord());
        pkDetail.setTime(TimeUtils.translateTime(pk.getCreateTime()));
        pkDetail.setInvite(new KeyNameValue(pk.getIsInvite().getStatu(),pk.getIsInvite().getStatuStr()));
        pkDetail.setInviteType(pk.getIsInvite());
        if(!ObjectUtils.isEmpty(pk.getMessageType())){pkDetail.setCharge(new KeyNameValue(pk.getMessageType().getStatu(),pk.getMessageType().getStatuStr()));}
        pkDetail.setPkStatu(ObjectUtils.isEmpty(pk.getAlbumStatu())?new KeyNameValue(PkStatu.审核中.getStatu(),PkStatu.审核中.getStatuStr()):new KeyNameValue(pk.getAlbumStatu().getStatu(),pk.getAlbumStatu().getStatuStr()));
        pkDetail.setApproveMessage(approveService.查询PK公告消息(pkId));
        if(pk.getPkType()==PkType.内置相册 && pk.getIsInvite() == InviteType.邀请 )
        {
                pkDetail.setApproved(dynamicService.查看内置相册已审核榜帖(pkId) + "已审核");
                pkDetail.setApproving(dynamicService.查看内置相册审核中榜帖(pkId) + "审核中");
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setName("管理群");
                if(dynamicService.查看内置相册群组状态(pkId))
                {
                    groupInfo.setMode(1);
                    groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
                }
                else
                {
                    groupInfo.setMode(2);
                    groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
                }
                pkDetail.setGroupInfo(groupInfo);

        }
        else if(pk.getPkType()==PkType.内置相册 && pk.getIsInvite() == InviteType.公开 )
        {
            pkDetail.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
            pkDetail.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + "审核中");

            GroupInfo groupInfo = new GroupInfo();
            boolean hasGroup = !StringUtils.isBlank(dynamicService.查询内置公开PK群组二维码MediaId(pkDetail.getPkId() ));
            groupInfo.setName("管理群");
            if(hasGroup)
            {
                groupInfo.setMode(1);
                groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
            }
            else
            {
                groupInfo.setMode(2);
                groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
            }
            pkDetail.setGroupInfo(groupInfo);

        }
        else
        {
            pkDetail.setApproved(redisSortSetService.size(CacheKeyName.榜主已审核列表(pkDetail.getPkId())) + "已审核");
            pkDetail.setApproving(redisSortSetService.size(CacheKeyName.榜主审核中列表(pkDetail.getPkId())) + "审核中");

                GroupInfo groupInfo = new GroupInfo();
                boolean hasGroup = !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkDetail.getPkId(),new Date() ));
                groupInfo.setName("管理群");
                if(hasGroup)
                {
                    groupInfo.setMode(1);
                    groupInfo.setIconUrl(IconUrl.有效微信标识.getUrl());
                }
                else
                {
                    groupInfo.setMode(2);
                    groupInfo.setIconUrl(IconUrl.无效微信标识.getUrl());
                }
                pkDetail.setGroupInfo(groupInfo);

        }

        return pkDetail;
    }



    public User queryPkCreator(String pkId){
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkEntity pkEntity = daoService.querySingleEntity(PkEntity.class,filter);

        User user = userService.queryUser(pkEntity.getUserId());
        return user;
    }







    public PkEntity querySinglePkEntity(String pkId)
    {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkEntity pk = daoService.querySingleEntity(PkEntity.class,filter);
        return pk;
    }





    public PkDynamicEntity 查询PK动态表(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkDynamicEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkDynamicEntity pkDynamicEntity = daoService.querySingleEntity(PkDynamicEntity.class,filter);



        return pkDynamicEntity;
    }

    public int 查询Pk打赏金额(String pkId) {

        return AppConfigService.getConfigAsInteger(常量值.收款码金额,3);
    }


    private PkTaskEntity 获取指定类型任务Entity(String pkId, int type, int page) {
        EntityFilterChain filter = null;
        if(type == TaskStatu.打赏中.getStatu()){
            filter = EntityFilterChain.newFilterChain(PkTaskEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("statu",CompareTag.Equal,TaskStatu.打赏中)
                    .pageLimitFilter(page,1);;
        }
        else
        {
            filter = EntityFilterChain.newFilterChain(PkTaskEntity.class)
                    .compareFilter("pkId",CompareTag.Equal,pkId)
                    .andFilter()
                    .compareFilter("statu",CompareTag.Equal,  TaskStatu.已打赏)
                    .pageLimitFilter(page,1);
        }

        List<PkTaskEntity> postEntities = daoService.queryEntities(PkTaskEntity.class,filter);

        return CollectionUtils.isEmpty(postEntities)?null:postEntities.get(0);


    }


    public int 查询PK购买价格(String pkId) {
        return 100;
    }


    public boolean isPkCreator(String pkId, String userId) {
        User creator = this.queryPkCreator(pkId);
        return org.apache.commons.lang.StringUtils.equals(userId,creator.getUserId())? Boolean.TRUE:Boolean.FALSE;
    }

    public String 创建PK(String userId, String topic, String watchWord,boolean invite) throws UnsupportedEncodingException {
        String pkId = UUID.randomUUID().toString();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(TimeUtils.currentDateTime());
        pkEntity.setPkType(PkType.运营相册);
        pkEntity.setMessageType(MessageType.不收费);
        pkEntity.setTopic(topic);
        pkEntity.setWatchWord(watchWord);
        pkEntity.setIsInvite(invite?InviteType.邀请:InviteType.公开);
        pkEntity.setUserId(userId);
        pkEntity.setAlbumStatu(PkStatu.审核中);
        daoService.insertEntity(pkEntity);
        userService.创建榜次数加1(userId);
        return pkId;
    }

    public PkEntity 创建预置PK(String topic, String watchWord,boolean isCharge,int type) throws UnsupportedEncodingException {
        String pkId = UUID.randomUUID().toString();
        PkEntity pkEntity = new PkEntity();
        pkEntity.setPkId(pkId);
        pkEntity.setCreateTime(TimeUtils.currentDateTime());
        pkEntity.setPkType(PkType.内置相册);
        pkEntity.setTopic(topic);
        pkEntity.setWatchWord(watchWord);
        pkEntity.setIsInvite(type != 2 ?InviteType.邀请:InviteType.公开);
        pkEntity.setMessageType(isCharge?MessageType.收费:MessageType.不收费);
        pkEntity.setUserId(appService.随机用户());
        pkEntity.setAlbumStatu(PkStatu.已审核);
        daoService.insertEntity(pkEntity);

        BuildInPkEntity buildInPkEntity = new BuildInPkEntity();
        buildInPkEntity.setPkId(pkId);
        buildInPkEntity.setIsInvite(type != 2 ?InviteType.邀请:InviteType.公开);
        buildInPkEntity.setMessageType(isCharge?MessageType.收费:MessageType.不收费);
        buildInPkEntity.setCreateTime(System.currentTimeMillis());
        daoService.insertEntity(buildInPkEntity);






        return pkEntity;
    }







    public boolean 是否更新今日审核群(PkEntity pkEntity) {
        if(pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.公开)
        {
            return !StringUtils.isBlank(dynamicService.查询内置公开PK群组二维码MediaId(pkEntity.getPkId()));
        }
        return !StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkEntity.getPkId(),new Date()));
    }

    public boolean 是否更新今日公告(String pkId) throws UnsupportedEncodingException {
//        return !StringUtils.isBlank(dynamicService.查询PK公告消息Id(pkId));
        return !ObjectUtils.isEmpty(approveService.获取审核人员消息Entity(pkId));

    }

    public void 删除预置的PK(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId)
                .andFilter()
                .compareFilter("pkType",CompareTag.Equal,PkType.内置相册);
        PkEntity pkEntity = daoService.querySingleEntity(PkEntity.class,filter);
        daoService.deleteEntity(pkEntity);





    }

    public void checkPk(String pkId) throws AppException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(ObjectUtils.isEmpty(pkEntity)){throw AppException.buildException(PageAction.执行处理器("error","榜单不存在，是否返回主页?"));}
        PkStatu pkStatu = pkEntity.getAlbumStatu();
        if(pkStatu == PkStatu.已关闭){
            throw AppException.buildException(PageAction.执行处理器("error","榜单关闭，是否返回主页?"));
        }






    }

    public void 删除PK(String userId, String pkId) throws AppException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(pkEntity.getAlbumStatu() == PkStatu.审核中)
        {
            daoService.deleteEntity(pkEntity);
            this.删除激活表(pkEntity.getPkId());
            userService.删除一个未激活榜单(userId);
            return ;
        }
        if(pkEntity.getAlbumStatu() == PkStatu.已关闭)
        {
            daoService.deleteEntity(pkEntity);
            this.删除激活表(pkEntity.getPkId());
            return ;
        }
        throw AppException.buildException(PageAction.信息反馈框("无法删除","已流通榜单无法删除"));






    }

    private void 删除激活表(String pkId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(PkActiveEntity.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        PkActiveEntity pkActiveEntity = daoService.querySingleEntity(PkActiveEntity.class,filter);
        if(!ObjectUtils.isEmpty(pkActiveEntity)){daoService.deleteEntity(pkActiveEntity);}


    }


    public void 修改PK(String pkId,String topic, String watchWord) throws AppException, UnsupportedEncodingException {
        PkEntity pkEntity = this.querySinglePkEntity(pkId);
        if(pkEntity.getAlbumStatu() == PkStatu.审核中)
        {
            pkEntity.setTopic(topic);
            pkEntity.setWatchWord(watchWord);
            daoService.updateEntity(pkEntity);
            return ;
        }

        throw AppException.buildException(PageAction.信息反馈框("修改失败","榜单当前状态不支持修改榜帖内容..."));


    }
}
