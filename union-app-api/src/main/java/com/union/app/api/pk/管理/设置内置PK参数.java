package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.entity.pk.InviteType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();


    @RequestMapping(path="/setAlbumType",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置预置PK参数(@RequestParam("pkId") String pkId,@RequestParam("password") String password,@RequestParam("type") int type,@RequestParam("value") int value) throws AppException, IOException {

//        appService.验证Password(password);
//
//
//        PkEntity pk = pkService.querySinglePkEntity(pkId);
//
//        if(!(pk.getPkType() == PkType.内置相册 && pk.getIsInvite() == InviteType.邀请))
//        {
//            return AppResponse.buildResponse(PageAction.信息反馈框("非内置相册","非内置仅邀请相册不能设置"));
//        }
//
//
//
//        if(type == 1)
//        {
//            dynamicService.更新内置相册已审核数量(pkId,value);
//        }
//        if(type == 2)
//        {
//            dynamicService.更新内置相册审核中数量(pkId,value);
//        }
//        if(type == 3)
//        {
//            dynamicService.更新内置相册群组状态(pkId);
//        }
//
//
//        PkDetail pkDetail = pkService.querySinglePk(pkId);
//        pkDetail.setGeneticPriority(appService.查询优先级(pkId,1));
//        pkDetail.setNonGeneticPriority(appService.查询优先级(pkId,2));
//



        return AppResponse.buildResponse(PageAction.执行处理器("success",null));
    }


}
