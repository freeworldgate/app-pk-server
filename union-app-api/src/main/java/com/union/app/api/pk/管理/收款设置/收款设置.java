package com.union.app.api.pk.管理.收款设置;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.支付.PayEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PayService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 收款设置 {


    @Autowired
    AppDaoService daoService;

    @Autowired
    KeyService keyService;

    @Autowired
    PostService postService;

    @Autowired
    AppService appService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    @Autowired
    PayService payService;

    @RequestMapping(path="/queryPayPolicys",method = RequestMethod.GET)
    public AppResponse 查询收款(@RequestParam("userId") String userId) throws AppException, IOException {

        appService.checkManager(userId);
        List<DataSet> dataSets = new ArrayList<>();

        Map<String,PayEntity> payEntityList = payService.查询所有收款方案();
        payEntityList.forEach((k,v)->{
            dataSets.add(new DataSet(k,v));
        });

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
    }

    @RequestMapping(path="/setPayPolicy",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 收款设置(@RequestParam("userId") String userId,@RequestParam("tag") String tag,@RequestParam("type") int type,@RequestParam("value") int value) throws AppException, IOException {


            appService.checkManager(userId);

            payService.设置收费策略(tag,type,value);



            return AppResponse.buildResponse(PageAction.执行处理器("success",""));


    }







}
