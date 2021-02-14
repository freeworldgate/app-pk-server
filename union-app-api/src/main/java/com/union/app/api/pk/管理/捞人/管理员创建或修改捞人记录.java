package com.union.app.api.pk.管理.捞人;

import com.union.app.domain.pk.捞人.CreateUserFind;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.pk.kadian.捞人.FindUserEntity;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 管理员创建或修改捞人记录 {


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


    @RequestMapping(path="/startManagerUserPkFind",method = RequestMethod.POST)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse startManagerUserPkFind(@RequestBody CreateUserFind createUserFind) throws AppException, IOException {

        String userId = userService.随机选择内置用户();
        createUserFind.setUserId(userId);
        FindUserEntity find = findService.查询用户捞人Entity(createUserFind.getPkId(),createUserFind.getUserId());
        if(!ObjectUtils.isEmpty(find)){
            return AppResponse.buildResponse(PageAction.信息反馈框("内置用户已使用，请重新选择","内置用户已使用，请重新选择"));
        }
        
        userService.内置用户新增打捞时间(userId);
        //校验
        findService.校验时间(createUserFind.getFindLength(),createUserFind.getUserId());

        findService.开始捞人(createUserFind);
        FindUserEntity findUserEntity = findService.查询用户捞人Entity(createUserFind.getPkId(),createUserFind.getUserId());
        //直接审批通过
        findService.审批(findUserEntity.getFindId());


        FindUser findUser = findService.查询用户捞人记录(createUserFind.getPkId(),createUserFind.getUserId());


        UserDynamicEntity userDynamicEntity = userService.queryUserKvEntity(createUserFind.getUserId());
        String leftTime = TimeUtils.剩余可打捞时间(userDynamicEntity.getFindTimeLength());


        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("findUser",findUser));
        dataSets.add(new DataSet("leftTime",leftTime));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));



    }

    @RequestMapping(path="/giveUpManagerUserPkFind",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse giveUpMangerUserPkFind(@RequestParam("userId") String userId,@RequestParam("findId") int findId) throws AppException, IOException {

        if(!userService.管理员用户(userId)){
            return AppResponse.buildResponse(PageAction.信息反馈框("滚","滚"));
        }

        findService.放弃捞人(findId);

        String leftTime = TimeUtils.剩余可打捞时间(0);


        return AppResponse.buildResponse(PageAction.执行处理器("success",leftTime));

    }

    @RequestMapping(path="/clearManagerUserFind",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse clearManagerUserFind(@RequestParam("userId") String userId,@RequestParam("findId") int findId,@RequestParam("pkId") String pkId) throws AppException, IOException {
        if(!userService.管理员用户(userId)){
            return AppResponse.buildResponse(PageAction.信息反馈框("滚","滚"));
        }


        //校验
        findService.清除UserFind(findId);



        FindUser findUser = findService.查询用户捞人记录(pkId,userId);
//        findUser.setExist(true);

        return AppResponse.buildResponse(PageAction.前端数据更新("findUser",findUser));

    }













}
