package com.union.app.api.pk.管理;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.ActivePk;
import com.union.app.domain.pk.ApprovePost;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.entity.pk.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 激活榜 {

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

    @RequestMapping(path="/activePks",method = RequestMethod.GET)
    public AppResponse 查询审核榜帖(@RequestParam("userId") String userId) throws AppException, IOException {
        List<ActivePk> activePks = new ArrayList<>();
        List<PkActiveEntity> pkActiveEntities = appService.查询需要激活的PK();

        for(PkActiveEntity pkActiveEntity:pkActiveEntities)
        {
            ActivePk activePk = new ActivePk();
            activePk.setPk(pkService.querySinglePk(pkActiveEntity.getPkId()));
            activePk.setApproveMessage(approveService.获取审核人员消息(pkActiveEntity.getPkId()));
            activePk.setCashierCommentUrl(pkActiveEntity.getScreenCutUrl());
            activePk.setPkCashier(appService.查询收款人(pkActiveEntity.getCashierId()));
            activePk.setCashierGroup(appService.查询激活的群组信息(pkActiveEntity.getGroupId()));
            activePk.setCashierFeeCode(appService.查询激活的收款码信息(pkActiveEntity.getFeeCodeId()));
            activePks.add(activePk);
        }


        return AppResponse.buildResponse(PageAction.前端数据更新("pks",activePks));

    }


    @RequestMapping(path="/activePk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse activePk(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId) throws AppException, IOException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        pkEntity.setAlbumStatu(PkStatu.已审核);
        PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
        pkActiveEntity.setStatu(ActiveStatu.处理过);
        daoService.updateEntity(pkEntity);
        daoService.updateEntity(pkActiveEntity);
        appService.收款码确认次数加一(pkActiveEntity.getFeeCodeId());


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


    @RequestMapping(path="/hiddenPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse hiddenPk(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId) throws AppException, IOException {


        PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
        pkActiveEntity.setStatu(ActiveStatu.处理过);
        daoService.updateEntity(pkActiveEntity);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


}
