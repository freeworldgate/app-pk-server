package com.union.app.api.邀请;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.*;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.PkLocationEntity;
import com.union.app.entity.pk.PkType;
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
public class 查询邀请 {

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


    @RequestMapping(path="/queryInvites",method = RequestMethod.GET)
    public AppResponse 查询邀请信息(@RequestParam("userId") String userId,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {

//        List<PkDetail> pkDetails = new ArrayList<>();

        List<PkDetail> pks = appService.查询用户邀请(userId,1,latitude,longitude);

        List<Marker> markers = new ArrayList<>();
        List<Circle> circles = new ArrayList<>();

//        pks.forEach(pk ->{
//            PkLocationEntity location = null;
//            if(!ObjectUtils.isEmpty(location)) {
//                Marker marker = new Marker();
//                marker.setId(RandomUtil.getRandomNumber());
//                marker.setLatitude(location.getLatitude() / 1000000.0d);
//                marker.setLongitude(location.getLongitude() / 1000000.0d);
////                marker.setIconPath(pk.getUser().getImgUrl());
//                Callout callout = new Callout();
////                callout.setContent(pk.getTopic().length()>10?pk.getTopic().substring(0,10)+"...":pk.getTopic());
//                marker.setCallout(callout);
//                markers.add(marker);
//
//                Circle circle = new Circle();
//                circle.setLatitude(location.getLatitude() / 1000000.0d);
//                circle.setLongitude(location.getLongitude() / 1000000.0d);
//                circles.add(circle);
//
//            }
//
//
//        });



        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("circles",circles));
        dataSets.add(new DataSet("markers",markers));
        if(CollectionUtils.isEmpty(pks))
        {
            dataSets.add(new DataSet("pkEnd",true));}
        else
        {


            dataSets.add(new DataSet("pks",pks));
            dataSets.add(new DataSet("pkEnd",false));
        }
;
        dataSets.add(new DataSet("page",1));

        dataSets.add(new DataSet("imgBack",appService.查询背景(3)));








        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextInvitePage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("userId") String userId,@RequestParam("page") int page,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {

        List<PkDetail> pks = appService.查询用户邀请(userId,page+1,latitude,longitude);

        if(pks.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

}
