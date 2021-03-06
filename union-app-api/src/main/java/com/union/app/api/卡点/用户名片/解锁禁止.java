package com.union.app.api.卡点.用户名片;

import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
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
public class 解锁禁止 {



    @Autowired
    UserService userService;



    @RequestMapping(path="/changeLock",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse deleteApply(@RequestParam("applyId") String applyId,@RequestParam("userId") String userId) throws IOException, AppException {

        userService.修改锁状态(userId,applyId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


}
