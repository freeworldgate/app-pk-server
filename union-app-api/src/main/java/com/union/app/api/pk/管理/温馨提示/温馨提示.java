package com.union.app.api.pk.管理.温馨提示;

import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.entity.pk.PkTipEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.文字背景.TextService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 温馨提示 {

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
    TextService textService;


    @RequestMapping(path="/queryTipText",method = RequestMethod.GET)
    public AppResponse queryTipText(@RequestParam("type") int type) throws AppException, IOException {

//        appService.验证Password(password);
        List<DataSet> dataSets = new ArrayList<>();
        List<PkTipEntity> tips = appService.查询温馨提示(type);
        dataSets.add(new DataSet("tips",tips));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/removeTextTip",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse removeTextTip(@RequestParam("id") int id) throws AppException, IOException {

//        appService.验证Password(password);

        appService.删除温馨提示(id);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


    @RequestMapping(path="/addTextTip",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse addTextTip(@RequestParam("tip") String tip,@RequestParam("type") int type) throws AppException, IOException {

//        appService.验证Password(password);
        PkTipEntity pkTipEntity = appService.新增温馨提示(tip,type);

        return AppResponse.buildResponse(PageAction.执行处理器("success",pkTipEntity));

    }
}
