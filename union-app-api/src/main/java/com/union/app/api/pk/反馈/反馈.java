package com.union.app.api.pk.反馈;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.TipUrl;
import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 反馈 {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;


    @Autowired
    AppService appService;

    @RequestMapping(path="/updateTipBack",method = RequestMethod.GET)
    public AppResponse updateTipBack() throws AppException, IOException {

        TipUrl tipUrl = new TipUrl();

        tipUrl.setTipBack(appService.查询背景(6));
        tipUrl.setTipImg(appService.查询背景(7));







        return AppResponse.buildResponse(PageAction.执行处理器("success",tipUrl));
    }



}
