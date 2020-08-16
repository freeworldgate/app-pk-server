package com.union.app.api.pk.管理;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.ActivePk;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.ActiveStatu;
import com.union.app.entity.pk.PkActiveEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path="/pk")
public class 创建内置PK {

    @Autowired
    AppService appService;

    @Autowired
    AppDaoService daoService;

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
    OrderService orderService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();


    @RequestMapping(path="/preCreatePk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 创建预置PK(@RequestParam("userId") String userId,@RequestParam("topic") String topic,@RequestParam("watchWord") String watchWord,@RequestParam("invite") boolean invite) throws AppException, IOException {

        //用户校验
        PkEntity pk = pkService.创建预置PK(topic,watchWord,invite);
        //预置审核消息
        ApproveMessage approveMessage  = approveService.发布审核员消息(pk.getPkId(),pk.getUserId(),"编辑审核公告",RandomUtil.getRandomImage());
        //预置群组
        Date today = new Date();
        String groupUrl = RandomUtil.getRandomImage();
        String mediaId = WeChatUtil.uploadImg2Wx(groupUrl);
        dynamicService.设置PK群组二维码MediaId(pk.getPkId(),mediaId,today);
        dynamicService.设置PK群组二维码Url(pk.getPkId(),groupUrl,today);
        PkDetail pkDetail = pkService.querySinglePk(pk.getPkId());
        appService.vip包装(pkDetail,pkDetail.getUser().getUserId(),"");
        return AppResponse.buildResponse(PageAction.执行处理器("success",pkDetail));
    }

    @RequestMapping(path="/removePk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 删除预置PK(@RequestParam("pkId") String pkId) throws AppException, IOException {

        appService.移除主页预览(pkId);
        pkService.删除预置的PK(pkId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }


}
