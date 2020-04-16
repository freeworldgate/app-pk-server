package com.union.app.api.share;

import com.union.app.domain.pk.help.HelpInfo;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class 帮助信息 {


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
    OrderService orderService;

    @Autowired
    UserInfoService userInfoService;


    @RequestMapping(path="/helpInfo",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 帮助信息(@RequestParam("tag") int tag) throws AppException, IOException {

        List<HelpInfo> helpInfoList = new ArrayList<>();


        if(tag == 1){
            for(int i=0;i<100;i++) {
                HelpInfo helpInfo = new HelpInfo("动作" + i, RandomUtil.getRandomImage());
                helpInfoList.add(helpInfo);
            }
            return AppResponse.buildResponse(PageAction.执行处理器("success",helpInfoList));
        }else{
            for(int i=0;i<100;i++) {
                HelpInfo helpInfo = new HelpInfo("动作" + i, RandomUtil.getRandomImage());
                helpInfoList.add(helpInfo);
            }
            return AppResponse.buildResponse(PageAction.执行处理器("success",helpInfoList));
        }

    }




}
