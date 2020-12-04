package com.union.app.api.用户相册;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkButton;
import com.union.app.domain.pk.PkButtonType;
import com.union.app.domain.pk.PkDetail;
import com.union.app.entity.用户.UserKvEntity;
import com.union.app.plateform.constant.ConfigItem;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 用户相册 {

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


    @RequestMapping(path="/userPks",method = RequestMethod.GET)
    public AppResponse 用户相册(@RequestParam("userId") String userId,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {


        List<PkDetail> pks = appService.查询用户相册(userId,latitude,longitude,1);



        List<DataSet> dataSets = new ArrayList<>();

        if(CollectionUtils.isEmpty(pks))
        {
            dataSets.add(new DataSet("pkEnd",true));}
        else
        {
            dataSets.add(new DataSet("pks",pks));
            dataSets.add(new DataSet("pkEnd",false));
        }

        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("pageTag",true));









        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));



    }
    @RequestMapping(path="/nextUserPks",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("userId") String userId,@RequestParam("page") int page,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {

        List<PkDetail> pks = appService.查询用户相册(userId,latitude,longitude,page+1);


        if(CollectionUtils.isEmpty(pks))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }


}
