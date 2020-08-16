package com.union.app.api.pk;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.user.User;
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
//        if(userService.canUserView(userId))
//        {
            User user = userService.queryUser(userId);
            if (user.getPostTimes() > user.getPkTimes())
            {
                return AppResponse.buildResponse(PageAction.执行处理器("create", "消耗一张榜单，确定要创建榜单吗?"));
            }
            else
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("榜单不足","当前没有可用榜单，你可以通过打榜获取更多榜单，成功发布一个榜帖即可获得一个榜单..."));
            }

//        }
//        else {
//
//            int times = userService.queryUserPkTimes(userId);
//            int maxPks = AppConfigService.getConfigAsInteger(ConfigItem.用户最大建榜数量);
//
////        if(times == 0){
////            return AppResponse.buildResponse(PageAction.执行处理器("create","建榜将使用一张榜卷，共3榜卷，确定要建榜吗?"));
////        }
////        if(times == 1){
////            return AppResponse.buildResponse(PageAction.执行处理器("create","建榜将使用一张榜卷，共2张榜卷，确定要建榜吗?"));
////        }
////        if(times == 2){
////            return AppResponse.buildResponse(PageAction.执行处理器("create","建榜将使用一张榜卷，共1张榜卷，确定要建榜吗?"));
////        }
//            if (times > maxPks - 1) {
//                return AppResponse.buildResponse(PageAction.信息反馈框("提示", maxPks + "张榜卷已经用完"));
//            }
//
//
//            return AppResponse.buildResponse(PageAction.执行处理器("create", "建榜将使用一张榜卷，共" + (maxPks - times) + "张榜卷，确定要建榜吗?"));
//
//        }
//




    }


}
