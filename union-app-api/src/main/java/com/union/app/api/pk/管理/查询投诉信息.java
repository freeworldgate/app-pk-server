package com.union.app.api.pk.管理;

import com.union.app.domain.pk.complain.Complain;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询投诉信息 {


    @Autowired
    ComplainService complainService;

    @Autowired
    AppService appService;


    @RequestMapping(path="/nextComplain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam("password") String password,@RequestParam("type") int type) throws AppException, IOException {
        appService.验证Password(password);

        List<DataSet> dataSets  = appService.查询投诉榜帖(type);

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(path="/approveComplain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 有效投诉(@RequestParam("password") String password,@RequestParam("id") String id,@RequestParam("type") int type) throws AppException, IOException {

        appService.验证Password(password);
        appService.有效投诉(id);
        List<DataSet> dataSets  = appService.查询投诉榜帖(type);
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(path="/hiddenComplain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 无效投诉(@RequestParam("password") String password,@RequestParam("id") String id,@RequestParam("type") int type) throws AppException, IOException {
        appService.验证Password(password);

        appService.无效投诉(id);
        List<DataSet> dataSets  = appService.查询投诉榜帖(type);
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }












}
