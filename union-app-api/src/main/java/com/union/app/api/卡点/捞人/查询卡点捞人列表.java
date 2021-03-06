package com.union.app.api.卡点.捞人;

import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.kadian.捞人.FindStatu;
import com.union.app.entity.pk.kadian.捞人.FindUserEntity;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class 查询卡点捞人列表 {


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
    FindService findService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/queryPkFinds",method = RequestMethod.GET)
    public AppResponse queryPkImages(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {

        List<DataSet> dataSets = new ArrayList<>();
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        if(!pkEntity.isFindSet())
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("打捞功能已关闭","服务异常，请稍后再试!"));

        }

        List<FindUser> findUsers = findService.查询卡点捞人列表(pkId);

        FindUserEntity findUserEntity = findService.查询用户捞人Entity(pkId,userId);
        long current = System.currentTimeMillis();
        FindUser userFindUser = null;
        if(!ObjectUtils.isEmpty(findUserEntity) &&(findUserEntity.getFindStatu() == FindStatu.打捞中)&&(current>findUserEntity.getStartTime() && current < findUserEntity.getEndTime()))
        {
            for(FindUser findUser:findUsers)
            {
                if(StringUtils.equals(findUser.getUser().getUserId(),userId)){
                    findUsers.remove(findUser);
                    userFindUser = findUser;
                    break;
                }
            }
            if(ObjectUtils.isEmpty(userFindUser)){
                userFindUser = findService.translate(findUserEntity);
            }
            if(!ObjectUtils.isEmpty(userFindUser)){
                findUsers.add(0,userFindUser);
            }
        }







        if(CollectionUtils.isEmpty(findUsers))
        {
            dataSets.add(new DataSet("backUrl",appService.查询背景(0)));
        }
        else
        {
            dataSets.add(new DataSet("backUrl",appService.查询背景(5)));
            dataSets.add(new DataSet("findUsers",findUsers));
        }
        dataSets.add(new DataSet("appBack",appService.查询背景(11)));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }

}
