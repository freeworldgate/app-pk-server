package com.union.app.api.pk;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.ValueStr;
import com.union.app.domain.user.User;
import com.union.app.entity.用户.UserEntity;
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


            ValueStr valueStr = new ValueStr("","创建主题","确定创建主题?");




//            if(userService.用户未激活榜单超限(userId))
//            {
//                return AppResponse.buildResponse(PageAction.信息反馈框("您名下未激活榜单过多","请至少激活一个榜单..."));
//
//            }
            if(userService.是否有可用图贴(userId))
            {
                return AppResponse.buildResponse(PageAction.执行处理器("create", valueStr));

            }
            else
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("创建失败","系统检测到您当前没有可用主题，您可以通过参与更多主题发布有效图贴来获取更多主题..."));
            }



//
//
//            User user = userService.queryUser(userId);
//            if (user.getPostTimes() > user.getPkTimes())
//            {
//                return AppResponse.buildResponse(PageAction.执行处理器("create", "消耗一张图贴，确定要创建主题吗?"));
//            }
//            else
//            {
//                if(userService.是否是遗传用户(userId)){
//
//                    return AppResponse.buildResponse(PageAction.信息反馈框("图贴不足","当前没有可用图贴，你可以通过发布图贴获取更多可用图贴..."));
//                }
//                else
//                {
//
//                    return AppResponse.buildResponse(PageAction.信息反馈框("图贴已耗尽","您名下已经没有可用的图贴了..."));
//                }
//
//
//
//            }






    }


}
