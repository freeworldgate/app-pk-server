package com.union.app.api.pk.管理.城市;

import com.union.app.entity.pk.PkTipEntity;
import com.union.app.entity.pk.city.CityEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.文字背景.TextService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class 城市 {

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
    TextService textService;


    @RequestMapping(path="/queryCities",method = RequestMethod.GET)
    public AppResponse queryCities(@RequestParam("userId") String userId) throws AppException, IOException {

        appService.checkManager(userId);

        List<DataSet> dataSets = new ArrayList<>();

        List<CityEntity> cities = appService.查询所有城市();

        dataSets.add(new DataSet("cities",cities));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }





    @RequestMapping(path="/needPay",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse neepPay(@RequestParam("cityCode") int cityCode,@RequestParam("userId") String userId) throws AppException, IOException {


        appService.checkManager(userId);
        boolean statu = appService.修改收费状态(cityCode);


        return AppResponse.buildResponse(PageAction.执行处理器("success",statu));

    }



}
