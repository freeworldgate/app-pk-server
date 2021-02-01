package com.union.app.api.pk.管理.文字背景;

import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.pk.service.文字背景.TextService;
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
public class 添加文字背景 {

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
    ApproveService approveService;

    @Autowired
    FindService findService;

    @Autowired
    TextService textService;

    @RequestMapping(path="/addTextBack",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse addTextBack(@RequestParam("backColor") String backColor,@RequestParam("fontColor") String fontColor,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {

        if("ffffffff".equalsIgnoreCase(backColor)){
            throw AppException.buildException(PageAction.信息反馈框("不支持白色","不支持白色"));
        }



        String backId = textService.添加背景(backColor,fontColor,imgUrl);
        TextBack textBack = textService.查询TextBackEntity(backId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",textBack));

    }


    @RequestMapping(path="/removeBack",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse addTextBack(@RequestParam("backId") String backId) throws AppException, IOException {

        textService.删除背景(backId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }



}
