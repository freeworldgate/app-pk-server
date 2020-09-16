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

            if(!userService.是否是遗传用户(userId)){return AppResponse.buildResponse(PageAction.执行处理器("create", "确定要创建主题吗?"));}


            if(userService.用户未激活榜单超限(userId))
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("您名下未激活榜单过多","请至少激活一个榜单..."));

            }
            User user = userService.queryUser(userId);
            if (user.getPostTimes() > user.getPkTimes())
            {
                return AppResponse.buildResponse(PageAction.执行处理器("create", "消耗一张图贴，确定要创建主题吗?"));
            }
            else
            {
                if(userService.是否是遗传用户(userId)){

                    return AppResponse.buildResponse(PageAction.信息反馈框("榜单不足","当前没有可用榜单，你可以通过打榜获取更多榜单，成功发布一个图贴即可获得一个榜单..."));
                }
                else
                {

                    return AppResponse.buildResponse(PageAction.信息反馈框("榜单已耗尽","您名下已经没有可用的榜单了..."));
                }



            }






    }


}
