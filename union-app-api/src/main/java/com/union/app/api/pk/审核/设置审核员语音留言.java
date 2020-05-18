package com.union.app.api.pk.审核;

import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
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
import java.util.Date;

@RestController
@RequestMapping(path="/pk")
public class 设置审核员语音留言 {


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

    @RequestMapping(path="/setApproverVoice",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置语音留言(@RequestParam("pkId") String pkId, @RequestParam("approvorId") String approvorId,  @RequestParam("userId") String userId,@RequestParam("fileUrl") String fileUrl,@RequestParam("speckTime") int speckTime) throws AppException, IOException {
//
        Date currentDay = new Date();


        PostEntity postEntity = postService.查询用户帖(pkId,userId);


        approveService.设置审核员语言留言(pkId,approvorId,speckTime,fileUrl,currentDay);


        ApproveMessage approveMessage = approveService.获取审核人员消息(pkId,approvorId,currentDay);


        return AppResponse.buildResponse(PageAction.执行处理器("success",approveMessage));













    }



}
