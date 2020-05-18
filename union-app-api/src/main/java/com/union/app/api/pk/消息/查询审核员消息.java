package com.union.app.api.pk.消息;

import com.union.app.domain.pk.complain.Complain;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询审核员消息 {


    @Autowired
    ComplainService complainService;



    @Autowired
    ApproveService approveService;


    @Autowired
    UserService userService;



    @RequestMapping(path="/queryApproveMessage",method = RequestMethod.GET)
    public AppResponse 查询审核员消息(@RequestParam("pkId") String pkId,@RequestParam("approverUserId") String approverUserId,@RequestParam("userId") String userId) throws AppException, IOException {

        Date currentDate = new Date();

        ApproveMessage approveMessage = approveService.获取审核人员消息(pkId,approverUserId,currentDate);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("message",approveMessage));
        dataSets.add(new DataSet("user",userService.queryUser(userId)));
        dataSets.add(new DataSet("pkId",pkId));
        dataSets.add(new DataSet("date",TimeUtils.dateStr(currentDate)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
    }
















}
