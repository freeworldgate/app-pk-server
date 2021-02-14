package com.union.app.api.pk.管理.捞人;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
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
public class 查询审核捞人 {

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
    FindService findService;


    @RequestMapping(path="/queryApprovingFinds",method = RequestMethod.GET)
    public AppResponse 查询排名信息() throws AppException, IOException {

//        appService.验证Password(password);
        List<DataSet> dataSets = new ArrayList<>();
        List<FindUser> findUsers = findService.查询审核捞人记录(1);
        dataSets.add(new DataSet("findUsers",findUsers));
        dataSets.add(new DataSet("page",1));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextApprovingFinds",method = RequestMethod.GET)
    public AppResponse 查询排名信息(@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<FindUser> findUsers = findService.查询审核捞人记录(page+1);
        if(CollectionUtils.isEmpty(findUsers))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("findUsers",findUsers));
        return AppResponse.buildResponse(PageAction.执行处理器("success",findUsers));


    }

}
