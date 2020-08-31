package com.union.app.service.data;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.entity.pk.*;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class PkDataService {





    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    AppService appService;

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
    //最高一百页




    private static List<PkDetail> 遗传主页列表 = new ArrayList<>();
    private static List<PkDetail> 非遗传主页列表 = new ArrayList<>();
    private static List<PkDetail> 仅邀请的遗传主页列表 = new ArrayList<>();


    public List<PkDetail> 遗传主页列表(int page)
    {
        if((page-1) * 20 >= 遗传主页列表.size())
        {
            return new ArrayList<>();
        }
        else if(page * 20 >= 遗传主页列表.size())
        {
            return 遗传主页列表.subList((page-1) * 20,遗传主页列表.size());
        }
        else
        {
            return 遗传主页列表.subList((page-1) * 20,page * 20);
        }


    }
    public List<PkDetail> 非遗传主页列表(int page)
    {
        if((page-1) * 20 >= 非遗传主页列表.size())
        {
            return new ArrayList<>();
        }
        else if(page * 20 >= 非遗传主页列表.size())
        {
            return 非遗传主页列表.subList((page-1) * 20,非遗传主页列表.size());
        }
        else
        {
            return 非遗传主页列表.subList((page-1) * 20,page * 20);
        }


    }
    public List<PkDetail> 仅邀请的遗传主页列表(int page)
    {


        if((page-1) * 20 >= 仅邀请的遗传主页列表.size())
        {
            return new ArrayList<>();
        }
        else if(page * 20 >= 仅邀请的遗传主页列表.size())
        {
            return 仅邀请的遗传主页列表.subList((page-1) * 20,仅邀请的遗传主页列表.size());
        }
        else
        {
            return 仅邀请的遗传主页列表.subList((page-1) * 20,page * 20);
        }


    }

    public void 更新相册列表() throws IOException {
        List<HomePagePk> pkEntities = new ArrayList<>();
        int page = 1;
        List<HomePagePk> pks = null;
        while(!CollectionUtils.isEmpty(pks = this.查询HomePagePk(page)))
        {
            pkEntities.addAll(pks);
            page++;
        }




        List<PkDetail> 遗传主页列表Temp = new ArrayList<>();
        List<PkDetail> 非遗传主页列表Temp = new ArrayList<>();
        List<PkDetail> 仅邀请的遗传主页列表Temp = new ArrayList<>();

        for(HomePagePk pk:pkEntities)
        {
            PkEntity pkEntity = pkService.querySinglePkEntity(pk.getPkId());
            if(pkEntity.getPkType() == PkType.内置相册  && pkEntity.getIsInvite() == InviteType.邀请){
                dynamicService.更新内置相册参数(pkEntity.getPkId());
            }

            PkDetail pkDetail = pkService.querySinglePk(pk.getPkId());

            pkDetail.setGeneticPriority(pk.getGeneticPriority());
            pkDetail.setNonGeneticPriority(pk.getNonGeneticPriority());

            if(pk.getGeneticPriority() > -1){
                遗传主页列表Temp.add(pkDetail);
                if(pkDetail.getInviteType() == InviteType.邀请){仅邀请的遗传主页列表Temp.add(pkDetail);}

            }
            if(pk.getNonGeneticPriority() > -1){非遗传主页列表Temp.add(pkDetail);}


        }
        遗传主页列表 = 遗传主页列表Temp;
        非遗传主页列表 = 非遗传主页列表Temp;
        仅邀请的遗传主页列表 = 仅邀请的遗传主页列表Temp;
    }

    private List<HomePagePk> 查询HomePagePk(int page) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(HomePagePk.class)
                .pageLimitFilter(page,20);
        List<HomePagePk> pkEntities = daoService.queryEntities(HomePagePk.class,filter);
        return pkEntities;

    }


}