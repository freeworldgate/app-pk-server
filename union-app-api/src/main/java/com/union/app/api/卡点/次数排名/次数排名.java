package com.union.app.api.卡点.次数排名;

import com.union.app.domain.pk.排名.UserSort;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
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
public class 次数排名 {


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
    PkUserDynamicService pkUserDynamicService;

    @RequestMapping(path="/queryPkSorts",method = RequestMethod.GET)
    public AppResponse querypkSorts(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId) throws AppException, IOException, InterruptedException {

        //下一页
//        PkDynamic userSort = null;
        List<UserSort> sorts = pkUserDynamicService.查询卡点打卡排名(pkId,1);
        for(UserSort userSort :sorts)
        {
            if(StringUtils.equals(userId, userSort.getUser().getUserId()))
            {
//                userSort = pkDynamic;
                sorts.remove(userSort);
                sorts.add(0, userSort);
                break;
            }
        }


        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("emptyData",appService.查询背景(4)));

//        dataSets.add(new DataSet("userSort",userSort));
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("sorts",sorts));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

    @RequestMapping(path="/nextPkSorts",method = RequestMethod.GET)
    public AppResponse nextPkApprovingImagePage(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("page") int page) throws AppException, IOException, InterruptedException {


        List<UserSort> sorts = pkUserDynamicService.查询卡点打卡排名(pkId,page+1);


        if(CollectionUtils.isEmpty(sorts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",sorts));


    }



}
