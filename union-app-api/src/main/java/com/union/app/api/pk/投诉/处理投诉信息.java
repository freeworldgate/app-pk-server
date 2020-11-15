package com.union.app.api.pk.投诉;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.complain.Complain;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
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
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 处理投诉信息 {


    @Autowired
    ComplainService complainService;

    @Autowired
    PostService postService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PkService pkService;

    @Autowired
    RedisSortSetService redisSortSetService;


    @RequestMapping(path="/complain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("text") String text,@RequestParam("url") String url) throws AppException, IOException {
        Date date = new Date();

        complainService.添加投诉(pkId,userId,text,url);




        Complain complain = complainService.查询Complain投诉信息(pkId,userId);
//            return AppResponse.buildResponse(PageAction.信息反馈框("提示","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
        return AppResponse.buildResponse(PageAction.执行处理器("success",complain));



    }

    @RequestMapping(path="/complainList",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息List(@RequestParam("pkId") String pkId) throws AppException, IOException {

        List<Complain> complainList = complainService.查询投诉信息(pkId,1);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("complains",complainList));
        dataSets.add(new DataSet("page",1));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/morecomplainList",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse More查询投诉信息List(@RequestParam("pkId") String pkId,@RequestParam("page") int page) throws AppException, IOException {

        List<Complain> complainList = complainService.查询投诉信息(pkId,page+1);


        if(CollectionUtils.isEmpty(complainList))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",complainList));


    }














}
