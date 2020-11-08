package com.union.app.api.pk.喜欢收藏投诉审核页面;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkButton;
import com.union.app.domain.pk.PkButtonType;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.user.User;
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
public class 展示页面 {

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
    InfoService infoService;


    @RequestMapping(path="/showInfo",method = RequestMethod.GET)
    public AppResponse 展示页面(@RequestParam("pkId") String pkId,@RequestParam("type") int type) throws AppException, IOException {

        PkDetail pk = pkService.querySinglePk(pkId);
        List<User> users = infoService.查询示例用户(pkId,type);
        int num = infoService.查询数量(pkId,type);
        String title = infoService.查询标题(type);
        String icon = infoService.查询图标(type);


        List<DataSet> dataSets = new ArrayList<>();


        dataSets.add(new DataSet("pk",pk));
        dataSets.add(new DataSet("users",users));
        dataSets.add(new DataSet("num",num));
        dataSets.add(new DataSet("title",title));
        dataSets.add(new DataSet("icon",icon));





        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
}
