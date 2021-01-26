package com.union.app.api.卡点.搜索创建;

import com.union.app.api.卡点.搜索创建.返回对象.SearchResult;
import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Circle;
import com.union.app.domain.pk.Marker;
import com.union.app.domain.pk.PkDetail;
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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @Autowired
    KeyService keyService;



    @RequestMapping(path="/searchPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse searchPk(@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude,@RequestParam("name") String name) throws AppException, IOException, InterruptedException {
        //坐标
        String locationId = locationService.坐标转换成UUID(latitude,longitude,name);
        System.out.println(locationId+"     经度:" + latitude + "纬度:" + longitude);
        PkDetail pkDetail = locationService.搜索卡点(locationId);
        Marker marker = locationService.buildMarker(name,latitude,longitude);
        SearchResult searchResult = new SearchResult();
        searchResult.setMarkers(new Marker[]{marker});
        searchResult.setMaxLength(AppConfigService.getConfigAsInteger(ConfigItem.创建卡点范围));


        if(!ObjectUtils.isEmpty(pkDetail)) {

            double latitudeoff = latitude- keyService.获取偏移量(pkDetail.getType().getScale());

            searchResult.setCircles(new Circle[]{pkDetail.getCircle()});
            searchResult.setScale(pkDetail.getType().getScale());
            searchResult.setLatitude(latitudeoff);
            searchResult.setLongitude(longitude);




        }
        else
        {

//            double latitudeoff = latitude-keyService.获取偏移量(keyService.获取偏缩放等级(100));
            searchResult.setCircles(new Circle[]{new Circle(latitude,longitude,100)});
            searchResult.setScale(keyService.获取缩放等级(100));
            searchResult.setLatitude(latitude);
            searchResult.setLongitude(longitude);
        }

        searchResult.setPk(pkDetail);

        return AppResponse.buildResponse(PageAction.执行处理器("success",searchResult));

    }


}
