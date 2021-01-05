package com.union.app.api.卡点.搜索创建;

import com.union.app.domain.pk.Circle;
import com.union.app.domain.pk.Marker;
import com.union.app.domain.pk.PkDetail;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
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
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 搜索卡点 {


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

    @Autowired
    AppService appService;

    @Autowired
    ComplainService complainService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/searchPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse searchPk(@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude,@RequestParam("name") String name) throws AppException, IOException, InterruptedException {
        //坐标
        String locationId = locationService.坐标转换成UUID(latitude,longitude,name);
        System.out.println(locationId+"     经度:" + latitude + "纬度:" + longitude);
        PkDetail pkDetail = locationService.搜索卡点(locationId);
        Marker marker = locationService.buildMarker(name,latitude,longitude);
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("scale",16));
        if(!ObjectUtils.isEmpty(pkDetail)) {
            dataSets.add(new DataSet("circles", new Circle[]{pkDetail.getCircle()}));
            dataSets.add(new DataSet("scale",pkDetail.getType().getScale()));
        }
        dataSets.add(new DataSet("markers",new Marker[]{marker}));
        dataSets.add(new DataSet("latitude",latitude));
        dataSets.add(new DataSet("longitude",longitude));
        dataSets.add(new DataSet("emptyImage",appService.查询背景(1)));
        dataSets.add(new DataSet("pk",pkDetail));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


}
