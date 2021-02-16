package com.union.app.api.pk.管理.捞人;

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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 审批 {

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


    @RequestMapping(path="/passFindUser",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询排名信息(@RequestParam("findId") int findId) throws AppException, IOException {

        findService.审批(findId);
        FindUser findUser = findService.查询ByFindId(findId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",findUser));

    }
    @RequestMapping(path="/rejectFindUser",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse rejectFindUser(@RequestParam("findId") int findId) throws AppException, IOException {

        findService.拒绝(findId);
        FindUser findUser = findService.查询ByFindId(findId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",findUser));

    }

}
