package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.*;
import com.union.app.entity.pk.*;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();

    @RequestMapping(path="/activePks",method = RequestMethod.GET)
    public AppResponse 查询审核榜帖(@RequestParam("password") String password) throws AppException, IOException {
        appService.验证Password(password);
        ActivePk activePk = appService.查询需要激活的PK();
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("pk",activePk));
        if(!ObjectUtils.isEmpty(activePk))
        {

            List<ActiveTip> tips = appService.查询所有标签信息();
            dataSets.add(new DataSet("tips",tips));
            dataSets.add(new DataSet("tipId",activePk.getTipId()));
        }



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(path="/activePk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse activePk(@RequestParam("password") String password,@RequestParam("pkId") String pkId) throws AppException, IOException {
        appService.验证Password(password);
        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
//        pkEntity.setAlbumStatu(PkStatu.已审核);
        PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
        pkActiveEntity.setStatu(ActiveStatu.处理过);
        daoService.updateEntity(pkEntity);
        daoService.updateEntity(pkActiveEntity);
        userService.确认开通PK次数加1(pkEntity.getUserId());

        ActivePk activePk = appService.查询需要激活的PK();
        return AppResponse.buildResponse(PageAction.前端数据更新("pk",activePk));

    }


    @RequestMapping(path="/hiddenPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse hiddenPk(@RequestParam("password") String password,@RequestParam("pkId") String pkId,@RequestParam("tipId") String tipId) throws AppException, IOException {

        appService.验证Password(password);
        PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
        pkActiveEntity.setStatu(ActiveStatu.处理过);
        pkActiveEntity.setTipId(tipId);
//        pkActiveEntity.setRejectTime(pkActiveEntity.getRejectTime() + 1);
        daoService.updateEntity(pkActiveEntity);

        ActivePk activePk = appService.查询需要激活的PK();
        return AppResponse.buildResponse(PageAction.前端数据更新("pk",activePk));

    }


}
