package com.union.app.api.pk.审核;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.ActivePk;
import com.union.app.domain.pk.PkActive;
import com.union.app.entity.pk.*;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 提交激活码 {


    @Autowired
    AppDaoService daoService;

    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @RequestMapping(path="/activeSinglePK",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 提交激活码(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("activeCode") String activeCode) throws AppException, IOException {



        PkActive pkActive = appService.提交激活码(pkId,userId,activeCode);

        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("pkActive", pkActive));
        dataSets.add(new DataSet("icon1", "/images/waiting.png"));
        dataSets.add(new DataSet("approveText", "审核中"));


        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/activeAgine",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse activeAgine(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {


        appService.重新激活PK(pkId,userId);

        List<DataSet> dataSets = new ArrayList<>();
        PkActive active = appService.查询激活信息(pkId);
        dataSets.add(new DataSet("pkActive", active));
        dataSets.add(new DataSet("icon1", "/images/waiting.png"));
        dataSets.add(new DataSet("approveText", "审核中"));


        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/publishPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse publishPk(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

//
//        appService.提交非遗传用户激活码(pkId,userId);
////
//        if(!userService.是否是遗传用户(userId))
//        {

            PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
//            pkEntity.setAlbumStatu(PkStatu.已审核);
//            PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
//            pkActiveEntity.setStatu(ActiveStatu.处理过);
            daoService.updateEntity(pkEntity);
//            daoService.updateEntity(pkActiveEntity);
            userService.确认开通PK次数加1(pkEntity.getUserId());
            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));

//        }



//        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/selectPker/selectPker?pkId=" + pkId,true));

    }


}
