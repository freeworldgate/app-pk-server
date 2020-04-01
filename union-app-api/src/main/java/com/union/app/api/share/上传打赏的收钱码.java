package com.union.app.api.share;

import com.union.app.domain.pk.UserCode;
import com.union.app.domain.user.FeeCodeDomain;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 上传打赏的收钱码 {


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


    @RequestMapping(path="/uploadFeeCode",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse uploadFeeCode(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        UserCode code = userInfoService.查询并创建收款码信息(pkId,userId);

        return AppResponse.buildResponse(PageAction.执行处理器("code",code));
    }



    @RequestMapping(path="/setPhone",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setPhone(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("phone") String phone) throws AppException, IOException {

        userInfoService.设置手机号码(pkId,userId,phone);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
    }


    @RequestMapping(path="/setFeeCode",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse setFeeCode(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("url") String url) throws AppException, IOException {

        UserCode code = userInfoService.设置收款码(pkId,userId,url);

        return AppResponse.buildResponse(PageAction.执行处理器("success",code));
    }






}
