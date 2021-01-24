package com.union.app.api.卡点;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.daka.CreateLocation;
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
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 创建卡点 {


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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @Autowired
    PayService payService;

    @RequestMapping(path="/buildPk",method = RequestMethod.POST)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse buildPk(@RequestBody CreateLocation createLocation ) throws AppException, IOException, InterruptedException {

        payService.用户创建卡点(createLocation.getUserId());


        String locationId = locationService.创建卡点(createLocation);
        PkDetail pkDetail = locationService.搜索卡点(locationId);





        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("pk",pkDetail));
        dataSets.add(new DataSet("mode",0));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


}
