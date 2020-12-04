package com.union.app.api.pk.审核;

import com.union.app.domain.pk.ValueStr;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.entity.pk.ApproveStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping(path="/pk")
public class 一次性任务 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    ApproveService approveService;



    @RequestMapping(path="/oneTimeTask",method = RequestMethod.GET)
    public AppResponse 提示审核员(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {

        Date current = new Date();







        return AppResponse.buildResponse(PageAction.执行处理器("no",""));


    }


}
