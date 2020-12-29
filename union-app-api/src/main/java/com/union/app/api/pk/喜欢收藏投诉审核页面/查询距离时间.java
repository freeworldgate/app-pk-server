package com.union.app.api.pk.喜欢收藏投诉审核页面;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.用户.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询距离时间 {


    @Autowired
    ComplainService complainService;

    @Autowired
    PostService postService;

    @Autowired
    AppService appService;

    @Autowired
    PkService pkService;

    @Autowired
    LocationService locationService;

    @Autowired
    UserService userService;

    @RequestMapping(path="/queryLengthTime",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息List(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {
        List<DataSet> dataSets = new ArrayList<>();
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        PostEntity postEntity = locationService.查询最新用户Post发布时间(pkId,userId);
        if(ObjectUtils.isEmpty(postEntity)){
            dataSets.add(new DataSet("leftTime",0));
        }
        else
        {
            long postLastUpdateTime = postEntity.getTime();
            long leftTime = System.currentTimeMillis() - postLastUpdateTime;
            int timePerid = AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔);
            if(leftTime > timePerid * 1000)
            {
                dataSets.add(new DataSet("leftTime",0));
            }
            else
            {
                dataSets.add(new DataSet("leftTime",timePerid-leftTime/1000));
            }


        }
        dataSets.add(new DataSet("postTimes",locationService.查询用户打卡次数(pkId,userId)));
        //查询用户打卡次数:




//
//        int length = locationService.计算坐标间距离(latitude,longitude,pkEntity.getLatitude(),pkEntity.getLongitude());
//        String lengthStr = locationService.距离转换成描述(length);



//        dataSets.add(new DataSet("length",length));

//        dataSets.add(new DataSet("lengthStr",lengthStr));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
















}
