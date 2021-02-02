package com.union.app.api.卡点.捞人;

import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.pk.kadian.捞人.FindStatu;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询Single捞人记录 {


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
    FindService findService;

    @RequestMapping(path="/querySingleFind",method = RequestMethod.GET)
    public AppResponse queryUserFind(@RequestParam("findId") int findId) throws IOException {

        FindUser findUser = findService.查询ByFindId(findId);

        if(!ObjectUtils.isEmpty(findUser)&&findUser.getStatu().getKey() != FindStatu.打捞中.getStatu())
        {
            findUser = null;
        }



        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("findUser",findUser));

        dataSets.add(new DataSet("backUrl",appService.查询背景(5)));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

}
