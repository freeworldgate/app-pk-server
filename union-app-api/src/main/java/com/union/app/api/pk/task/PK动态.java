package com.union.app.api.pk.task;

import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.PkDynamic.FeeTask;
import com.union.app.domain.pk.PkMode;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class PK动态 {


    /**
     * hehe
     */
    @Autowired
    DynamicService dynamicService;




    @RequestMapping(path="/queryPkStatu",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse PK动态(@RequestParam("pkId") String pkId) throws AppException, IOException {


        List<FactualInfo> factualInfos = dynamicService.获取当前PK操作动态(pkId);
        return AppResponse.buildResponse(PageAction.执行处理器("infos",factualInfos));



//        if(dynamicService.getPK模式(pkId) == PkMode.打赏模式)
//        {
//            List<FeeTask> task = dynamicService.获取PK打赏任务(pkId);
//            return AppResponse.buildResponse(PageAction.执行处理器("tasks",task));
//        }
//        else
//        {

//        }

    }



}
