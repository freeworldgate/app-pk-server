package com.union.app.api.pk.搜索创建;

import com.union.app.domain.pk.PkDetail;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
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
        //        String currentDay = TimeUtils.当前日期();

        PkDetail pkDetail = locationService.搜索卡点(locationId);




        return AppResponse.buildResponse(PageAction.前端数据更新("pk",pkDetail));

    }


}
