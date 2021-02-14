package com.union.app.api.邀请;

import com.union.app.domain.pk.*;
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
public class 查询邀请 {

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


    @RequestMapping(path="/queryInvites",method = RequestMethod.GET)
    public AppResponse 查询邀请信息(@RequestParam("targetId") String targetId,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {

//        List<PkDetail> pkDetails = new ArrayList<>();

        List<PkDetail> pks = appService.查询用户邀请(targetId,1,latitude,longitude);





        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("target",userService.queryUser(targetId)));
        if(CollectionUtils.isEmpty(pks))
        {
            dataSets.add(new DataSet("pkEnd",true));}
        else
        {


            dataSets.add(new DataSet("pks",pks));
            dataSets.add(new DataSet("pkEnd",false));
        }
;
        dataSets.add(new DataSet("page",1));



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextInvitePage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("targetId") String targetId,@RequestParam("page") int page,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) throws AppException, IOException {

        List<PkDetail> pks = appService.查询用户邀请(targetId,page+1,latitude,longitude);

        if(pks.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }

}
