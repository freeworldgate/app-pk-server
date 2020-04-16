package com.union.app.api.pk.投诉;

import com.union.app.domain.pk.complain.Complain;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
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
public class 处理投诉信息 {


    @Autowired
    ComplainService complainService;





    @RequestMapping(path="/approvedComplain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam("userId") String userId,@RequestParam("id") String id,@RequestParam("tag") int tag) throws AppException, IOException {



        return AppResponse.buildResponse(PageAction.前端数据更新("complain",""));
    }













    


}
