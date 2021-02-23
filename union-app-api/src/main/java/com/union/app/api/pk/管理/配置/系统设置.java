package com.union.app.api.pk.管理.配置;

import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
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
    public AppResponse systemSetting(@RequestParam("type") String type,@RequestParam("value") String value,@RequestParam("userId") String userId) throws IOException, AppException {

        appService.checkManager(userId);
        //密码校验

        appService.设置参数(type,value);






        List<DataSet> dataSets = appService.查询当前设置页面();




        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
    @RequestMapping(value = "/querySystemSetting", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse querySystemSetting(@RequestParam("userId") String userId) throws IOException, AppException {

        appService.checkManager(userId);

        List<DataSet> dataSets = appService.查询当前设置页面();

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
}
