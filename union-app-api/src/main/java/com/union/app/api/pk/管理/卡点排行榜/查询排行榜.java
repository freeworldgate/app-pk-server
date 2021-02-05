package com.union.app.api.pk.管理.卡点排行榜;

import com.union.app.domain.pk.Circle;
import com.union.app.domain.pk.Marker;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.user.User;
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
public class 查询排行榜 {

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
    ApproveService approveService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/queryPkList",method = RequestMethod.GET)
    public AppResponse queryPkList( @RequestParam("page") int page)  {


        List<PkDetail> pks = appService.查询卡点排名(page);;

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("page",page));
        dataSets.add(new DataSet("pks",pks));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


}
