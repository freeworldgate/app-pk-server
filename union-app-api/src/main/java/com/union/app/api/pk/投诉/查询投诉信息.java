package com.union.app.api.pk.投诉;

import com.union.app.domain.pk.complain.Complain;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.complain.ComplainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 查询投诉信息 {


    @Autowired
    ComplainService complainService;





    @RequestMapping(path="/nextComplain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam("userId") String userId,@RequestParam("type") int type) throws AppException, IOException {


        Complain complain = complainService.下一个投诉信息(type);

        if(ObjectUtils.isEmpty(complain)){
            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.正常消息,"无投诉信息"));
        }




//        DataSet dataSet = new DataSet("complain", complain);

        return AppResponse.buildResponse(PageAction.前端数据更新("complain",complain));
    }
















}
