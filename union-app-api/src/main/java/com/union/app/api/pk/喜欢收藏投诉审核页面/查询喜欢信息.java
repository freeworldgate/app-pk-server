package com.union.app.api.pk.喜欢收藏投诉审核页面;

import com.union.app.common.redis.RedisSortSetService;
import com.union.app.domain.pk.complain.Complain;
import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.InfoService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class 查询喜欢信息 {


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

    @Autowired
    InfoService infoService;

    @RequestMapping(path="/queryGreateUsers",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息List(@RequestParam("pkId") String pkId) throws AppException, IOException {

        List<User> greates = infoService.查询喜欢信息(pkId,1);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("greates",greates));
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("num",dynamicService.getKeyValue(CacheKeyName.点赞,pkId)));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/queryMoreGreateUsers",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse More查询投诉信息List(@RequestParam("pkId") String pkId,@RequestParam("page") int page) throws AppException, IOException {

        List<User> greates = infoService.查询喜欢信息(pkId,1);


        if(CollectionUtils.isEmpty(greates))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",greates));


    }














}
