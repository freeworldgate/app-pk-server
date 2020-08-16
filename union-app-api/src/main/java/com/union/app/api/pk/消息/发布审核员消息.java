package com.union.app.api.pk.消息;

import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.service.ApproveService;
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
public class 发布审核员消息 {


    @Autowired
    ComplainService complainService;



    @Autowired
    ApproveService approveService;



    @RequestMapping(path="/publishApproveMessage",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 发布审核员消息(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("text") String text,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {



        ApproveMessage approveMessage  = approveService.发布审核员消息(pkId,userId,text,imgUrl);



        return AppResponse.buildResponse(PageAction.执行处理器("message",approveMessage));
    }
















}
