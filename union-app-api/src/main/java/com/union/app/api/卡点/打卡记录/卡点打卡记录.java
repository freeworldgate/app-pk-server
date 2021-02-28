package com.union.app.api.卡点.打卡记录;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.排名.UserSort;
import com.union.app.entity.pk.PkEntity;
import com.union.app.plateform.constant.ConfigItem;
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
public class 卡点打卡记录 {


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
    AppService appService;

    @Autowired
    LocationService locationService;


    @RequestMapping(path="/queryUserPkPosts",method = RequestMethod.GET)
    public AppResponse querypkSorts(@RequestParam("targetId") String targetId,@RequestParam("pkId") String pkId)  {

        PkEntity pkEntity = locationService.querySinglePkEntityWithoutCache(pkId);
        List<Post> sorts = postService.查询用户发帖列表(targetId,pkId,1);



        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("emptyData",appService.查询背景(4)));
        dataSets.add(new DataSet("pk",pkEntity));
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("sorts",sorts));
        dataSets.add(new DataSet("postBorderRadius", AppConfigService.getConfigAsInteger(ConfigItem.小图片圆角)));
        dataSets.add(new DataSet("post1BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post2或4张图圆角)));
        dataSets.add(new DataSet("post2BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post1张图圆角)));
        dataSets.add(new DataSet("post3BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.文字背景圆角)));



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

    @RequestMapping(path="/nextUserPkPost",method = RequestMethod.GET)
    public AppResponse nextPkApprovingImagePage(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("page") int page) {


        List<Post> sorts = postService.查询用户发帖列表(userId,pkId,page+1);


        if(CollectionUtils.isEmpty(sorts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",sorts));


    }



}
