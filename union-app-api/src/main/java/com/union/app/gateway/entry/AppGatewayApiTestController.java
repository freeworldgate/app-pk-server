package com.union.app.gateway.entry;


import com.union.app.gateway.executor.ApiExecutor;
import com.union.app.plateform.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author root
 */
@RestController
@RequestMapping(path="/test")
public class AppGatewayApiTestController
{

//    @Autowired
//    ApiExecutor apiExecutor;


    @ResponseBody
    @RequestMapping(path = "/fuck",method = RequestMethod.GET)
    public ApiResponse apiGet(HttpServletRequest request) throws Exception
    {


//        Object object = apiExecutor.executeGetInvoke(null,null);
//

        return ApiResponse.buildSuccessResponse("");

    }









}
