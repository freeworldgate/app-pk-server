package com.union.app.api.pk.管理;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.HomePagePk;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkType;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 设置内置PK参数 {

    @Autowired
    AppService appService;

    @Autowired
    AppDaoService daoService;

    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();


    @RequestMapping(path="/setAlbumType",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置预置PK参数(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("type") int type,@RequestParam("value") int value) throws AppException, IOException {



        EntityFilterChain filter2 = EntityFilterChain.newFilterChain(HomePagePk.class)
                .compareFilter("pkId",CompareTag.Equal,pkId);
        HomePagePk pk = daoService.querySingleEntity(HomePagePk.class,filter2);

        if(ObjectUtils.isEmpty(pk))
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("相册未添加到预览","相册未添加到预览"));
        }
        if(pk.getPkType() != PkType.内置相册)
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("非遗传相册","非遗传相册不能设置"));
        }



        if(type == 1)
        {
            pk.setApproved(value);
        }
        if(type == 2)
        {
            pk.setApproving(value);
        }
        if(type == 3)
        {
            pk.setGroupStatu(pk.getGroupStatu() == 1?0:1);
        }
        daoService.updateEntity(pk);

        PkDetail pkDetail = pkService.querySinglePk(pkId);
        appService.vip包装(pkDetail,userId,"");
        pkDetail.setPriority(appService.查询优先级(pkId));


        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetail));
    }


}
