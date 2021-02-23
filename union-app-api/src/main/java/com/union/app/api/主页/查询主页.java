package com.union.app.api.主页;

import com.union.app.domain.pk.Callout;
import com.union.app.domain.pk.Circle;
import com.union.app.domain.pk.Marker;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询主页 {

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
    DynamicService dynamicService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/queryHomePage",method = RequestMethod.GET)
    public AppResponse 查询主页(@RequestParam("userId") String userId,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude)  {



        List<PkDetail> pks = locationService.查询附近卡点(userId,latitude,longitude);;


        List<Marker> markers = new ArrayList<>();
        List<Circle> circles = new ArrayList<>();
        List<DataSet> dataSets = new ArrayList<>();

        if(!CollectionUtils.isEmpty(pks)){
            circles.add(pks.get(0).getCircle());
            dataSets.add(new DataSet("latitude",pks.get(0).getLatitude()));
            dataSets.add(new DataSet("longitude",pks.get(0).getLongitude()));
            dataSets.add(new DataSet("scale",pks.get(0).getType().getScale()));

        }

        pks.forEach(pk ->{

                markers.add(pk.getMarker());

        });

//        pks.clear();
//        circles.clear();
//        markers.clear();



        dataSets.add(new DataSet("pageTag",true));
        dataSets.add(new DataSet("pks",pks));
        dataSets.add(new DataSet("circles",circles));
        dataSets.add(new DataSet("markers",markers));


        User user = userService.queryUser(userId);
        if(!ObjectUtils.isEmpty(user))
        {
            dataSets.add(new DataSet("user",user));
        }
        dataSets.add(new DataSet("appBack",appService.查询背景(11)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
//
//    @RequestMapping(path="/nextHomePage",method = RequestMethod.GET)
//    public AppResponse 查询主页(@RequestParam("userId") String userId,@RequestParam("fromUser") String fromUser,@RequestParam("page") int page) throws AppException, IOException {
//        List<PkDetail> pkDetails = new ArrayList<>();
//
//        if(userService.canUserView(userId,fromUser))
//        {
//            List<PkDetail> pks = appService.查询用户主页(userId,page + 1,1);
//            pkDetails.addAll(pks);
//        }
//        else
//        {
//            List<PkDetail> pks = appService.查询用户主页(userId,page + 1,0);
//            pkDetails.addAll(pks);
//
//        }
//
//        if(pkDetails.size() == 0)
//        {
//            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));
//
//        }
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetails));
//
//    }

}
