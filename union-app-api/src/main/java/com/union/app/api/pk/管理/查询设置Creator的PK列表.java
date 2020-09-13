package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkCreator;
import com.union.app.domain.pk.PkDetail;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 查询设置Creator的PK列表 {

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








    @RequestMapping(path="/queryPkCreators",method = RequestMethod.GET)
    public AppResponse 查询内置PK() throws AppException, IOException {

        List<PkCreator> pks = appService.查询Creator设置(1);



        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

    @RequestMapping(path="/morePkCreators",method = RequestMethod.GET)
    public AppResponse 查询内置PK(@RequestParam("page") int page) throws AppException, IOException {


        List<PkCreator> pks = appService.查询Creator设置(page+1);


        if(pks.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

    @RequestMapping(path="/switchBit",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse switchBit(@RequestParam("pkId") String pkId) throws AppException, IOException {

        PkCreator pk = appService.设置PK开关(pkId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",pk));

    }




}
