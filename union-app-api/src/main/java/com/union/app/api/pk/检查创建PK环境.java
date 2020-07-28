package com.union.app.api.pk;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.entity.用户.support.UserPostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 检查创建PK环境 {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    UserService userService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;


    @Autowired
    AppService appService;

    @RequestMapping(path="/checkPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse checkPk(@RequestParam("userId") String userId) throws AppException, IOException {
        if(AppConfigService.getConfigAsBoolean(ConfigItem.对所有用户展示审核系统) || userService.canUserView(userId))
        {
            UserPostStatu userPostStatu = userService.queryUserPostStatu(userId);
            if (userPostStatu != UserPostStatu.已打榜)
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("用户未打榜","至少打榜一次，打榜成功后解锁用户建榜能力"));
            }
        }

        int times = userService.queryUserPkTimes(userId);
        if(times == 0){
            return AppResponse.buildResponse(PageAction.执行处理器("create","建榜将使用一张榜卷，共3榜卷，确定要建榜吗?"));
        }
        if(times == 1){
            return AppResponse.buildResponse(PageAction.执行处理器("create","建榜将使用一张榜卷，共2张榜卷，确定要建榜吗?"));
        }
        if(times == 2){
            return AppResponse.buildResponse(PageAction.执行处理器("create","建榜将使用一张榜卷，共1张榜卷，确定要建榜吗?"));
        }
        if(times > 2)
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","三张榜卷已经用完"));
        }









        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }


}
