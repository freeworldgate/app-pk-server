package com.union.app.api.pk.zone;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.ShareDynamicMessage;
import com.union.app.domain.pk.Winners;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.RedisService;
import com.union.app.service.pk.service.PkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping(path="/pk")
public class 推荐榜 {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    RedisService redisService;


    @RequestMapping(path="/zoneRefresh",method = RequestMethod.GET)
    public AppResponse PK主页(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("sessionId") String sessionId) throws AppException, IOException {


        PkDetail pk = pkService.querySinglePk(pkId);

        //消息
//        ShareDynamicMessage shareDynamicMessage = pkService.getPkMessage(pkId);


//        Set<String> posts = pkService.查询POST视图(pkId);

        return AppResponse.buildResponse(PageAction.信息反馈框("AAA","BBB"));

    }



//
//    @RequestMapping(path="/nextPage",method = RequestMethod.GET)
//    public AppResponse 下一页(@RequestParam("page") int page,@RequestParam("userId") String userId,@RequestParam("pkId") String pkId,@RequestParam("sessionId") String sessionId) throws AppException {
//
//        String viewId = pkService.查询当前视图Id(sessionId);
//
////        List<String> posts = pkService.查询POST视图(pkId,sessionId);
//
//        return AppResponse.buildResponse(PageAction.信息反馈框("AAA","BBB"));
//    }
//






}
