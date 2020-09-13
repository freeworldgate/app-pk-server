package com.union.app.api.pk.管理;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.user.User;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 主页预览 {

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


    @RequestMapping(path="/queryPreHomePage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("password") String password,@RequestParam("type") int type) throws AppException, IOException {



        List<PkDetail> pks = appService.查询预设相册(1,type);



        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("pks",pks));
        dataSets.add(new DataSet("page",1));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/morePreHomePage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("password") String password,@RequestParam("page") int page,@RequestParam("type") int type) throws AppException, IOException {
        List<PkDetail> pkDetails = new ArrayList<>();


        List<PkDetail> pks = appService.查询预设相册(page + 1,type);
        pkDetails.addAll(pks);


        if(pkDetails.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetails));

    }

}
