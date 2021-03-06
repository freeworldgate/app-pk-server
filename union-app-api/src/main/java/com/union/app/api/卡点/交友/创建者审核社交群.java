package com.union.app.api.卡点.交友;

import com.union.app.domain.pk.捞人.CreateUserFind;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.user.UserDynamicEntity;
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
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 创建者审核社交群 {


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
    AppService appService;


    @Autowired
    LocationService locationService;

    @Autowired
    FindService findService;

//    @RequestMapping(path="/saveUserPkFind",method = RequestMethod.POST)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse saveUserPkFind(@RequestBody CreateUserFind createUserFind) throws IOException, AppException {
//
//        //校验
//
//
//        findService.保存捞人信息(createUserFind);
//
//
//
//        FindUser findUser = findService.查询用户捞人记录(createUserFind.getPkId(),createUserFind.getUserId());
//
//        return AppResponse.buildResponse(PageAction.前端数据更新("findUser",findUser));
//
//    }



    @RequestMapping(path="/.DDDDD",method = RequestMethod.POST)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse startUserPkFind(@RequestBody CreateUserFind createUserFind) throws AppException, IOException {

        //校验
        findService.校验时间(createUserFind.getFindLength(),createUserFind.getUserId());

        findService.开始捞人(createUserFind);

        FindUser findUser = findService.查询用户捞人记录(createUserFind.getPkId(),createUserFind.getUserId());
        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(createUserFind.getUserId());
        String leftTime = TimeUtils.剩余可打捞时间(userDynamicEntity.getFindTimeLength());


        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("findUser",findUser));
        dataSets.add(new DataSet("leftTime",leftTime));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));



    }









}
