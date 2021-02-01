package com.union.app.api.pk.管理.文字背景;

import com.union.app.domain.pk.捞人.FindUser;
import com.union.app.domain.pk.文字背景.TextBack;
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
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.pk.service.文字背景.TextService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询背景 {

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


    @RequestMapping(path="/queryBacks",method = RequestMethod.GET)
    public AppResponse 查询排名信息() throws AppException, IOException {

//        appService.验证Password(password);
        List<DataSet> dataSets = new ArrayList<>();
        List<TextBack> textBacks = textService.查询TextBacks(1);
        dataSets.add(new DataSet("backs",textBacks));
        dataSets.add(new DataSet("page",1));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextBacks",method = RequestMethod.GET)
    public AppResponse 查询排名信息(@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<TextBack> textBacks = textService.查询TextBacks(page+1);
        if(CollectionUtils.isEmpty(textBacks))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("backs",textBacks));
        return AppResponse.buildResponse(PageAction.执行处理器("success",textBacks));


    }

}
