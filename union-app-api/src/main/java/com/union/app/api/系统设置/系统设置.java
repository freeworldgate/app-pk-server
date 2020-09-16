package com.union.app.api.系统设置;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.user.User;

import com.union.app.entity.pk.PkCashierFeeCodeEntity;
import com.union.app.entity.pk.PkCashierGroupEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 系统设置 {


    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PkService pkService;

    @Autowired
    AppService appService;
    /**
     * 接收微信后台发来的用户消息
     * @return
     */
    @RequestMapping(value = "/systemSetting", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse systemSetting(@RequestParam("type") String type,@RequestParam("password") String password,@RequestParam("value") String value) throws IOException, AppException {


        //密码校验

            appService.验证Password(password);
            appService.设置参数(type,value);






        List<DataSet> dataSets = appService.查询当前设置页面();




        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
    @RequestMapping(value = "/querySystemSetting", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse querySystemSetting() throws IOException {



        List<DataSet> dataSets = appService.查询当前设置页面();




        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
}
