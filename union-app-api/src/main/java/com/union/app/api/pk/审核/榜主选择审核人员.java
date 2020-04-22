package com.union.app.api.pk.审核;

import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 榜主选择审核人员 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ApproveService approveService;

    @RequestMapping(path="/setApproveUser",method = RequestMethod.GET)
    public AppResponse 榜主选择审核人员(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("approverId") String approverId,@RequestParam("tag") int tag) throws AppException, IOException, ParseException {

        Date current = new Date();

        if(!pkService.isPkCreator(pkId,userId)){
            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }



        if(tag == 1) {
//            dynamicService.验证预备审核员(pkId,approverId,current);
            dynamicService.添加预备审核员(pkId,approverId);
        }
        else
        {
            dynamicService.删除预备审核员(pkId,approverId);
        }
        UserIntegral approver = dynamicService.查询用户打榜信息(pkId,approverId,current);

        return AppResponse.buildResponse(PageAction.执行处理器("success",approver));

    }



}
