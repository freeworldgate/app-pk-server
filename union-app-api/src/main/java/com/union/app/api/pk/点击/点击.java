package com.union.app.api.pk.点击;

import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/pk")
public class 点击 {


    @Autowired
    ClickService clickService;



    @RequestMapping(path="/click",method = RequestMethod.GET)
    public AppResponse 刷新配置项(@RequestParam("tab") int tab,@RequestParam("userId") String userId,@RequestParam("id") String id) throws AppException {

        PageAction pageAction = clickService.click(tab,userId,id);

        return AppResponse.buildResponse(pageAction);
    }


}
