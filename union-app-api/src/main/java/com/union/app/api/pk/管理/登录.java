package com.union.app.api.pk.管理;

import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 登录 {


    @Autowired
    AppService appService;


    @RequestMapping(path="/loginManager",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam(value="password") String password) throws AppException, IOException {


        appService.验证Password(password);

        return AppResponse.buildResponse(PageAction.执行处理器("success","password"));

    }













}
