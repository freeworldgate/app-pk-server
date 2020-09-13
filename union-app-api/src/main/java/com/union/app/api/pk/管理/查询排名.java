package com.union.app.api.pk.管理;

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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询排名 {

    @Autowired
    AppService appService;

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


    @RequestMapping(path="/querySort",method = RequestMethod.GET)
    public AppResponse 查询排名信息(@RequestParam("password") String password) throws AppException, IOException {

//        List<PkDetail> pkDetails = new ArrayList<>();

        List<PkDetail> pks = appService.查询PK排名(0);





        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

    @RequestMapping(path="/nextSortPage",method = RequestMethod.GET)
    public AppResponse 查询排名信息(@RequestParam("password") String password,@RequestParam("page") int page) throws AppException, IOException {


        List<PkDetail> pks = appService.查询PK排名(page);



        if(pks.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

}
