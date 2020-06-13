package com.union.app.api.pk.zone;

import com.union.app.common.id.KeyGetter;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 刷新PKID中的PAGE页面 {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    PostService postService;


    @Autowired
    AppService appService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @RequestMapping(path="/freshPageCache",method = RequestMethod.GET)
    public AppResponse 参战(@RequestParam("pkId") String pkId,@RequestParam("page")int page,@RequestParam("userId") String userId) throws AppException, IOException {


        redisStringUtil.setMapKey(KeyGetter.分页刷新标识Key(pkId),"PAGE-" + page,String.valueOf(System.currentTimeMillis()));


        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.信息反馈框("刷新成功","已写入刷新标志"));

    }
}
