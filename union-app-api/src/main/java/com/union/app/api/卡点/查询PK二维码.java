package com.union.app.api.卡点;

import com.union.app.entity.pk.PkEntity;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询PK二维码 {


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

    @RequestMapping(path="/queryPkCode",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId) {
            List<DataSet> dataSets = new ArrayList<>();

            PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
            dataSets.add(new DataSet("backUrl",pkEntity.getBackUrl()));
            dataSets.add(new DataSet("codeUrl",pkEntity.getCodeUrl()));



            return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));






    }

}
