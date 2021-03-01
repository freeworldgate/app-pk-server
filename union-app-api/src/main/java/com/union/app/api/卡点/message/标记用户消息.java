package com.union.app.api.卡点.message;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.message.Message;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.MessageService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
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
public class 标记用户消息 {

    @Autowired
    UserDynamicService userDynamicService;


    @Autowired
    MessageService messageService;
    @Autowired
    PostService postService;

    @RequestMapping(path="/seeMsg",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询用户评论(@RequestParam("messageId") int messageId) throws AppException, IOException, InterruptedException {

        //统计PK请求此次数。

        messageService.标记消息已读取(messageId);



        return AppResponse.buildResponse(PageAction.前端数据更新("a","b"));

    }


}
