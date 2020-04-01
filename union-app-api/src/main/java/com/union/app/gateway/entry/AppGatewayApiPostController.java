package com.union.app.gateway.entry;


import com.union.app.common.api.registry.ApiURI;
import com.union.app.common.request.RequestHelper;
import com.union.app.plateform.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path="/api")
public class AppGatewayApiPostController
{

/*
    @Autowired
    ApiRegistry apiRegistry;*/

//    @Autowired
//    ApiExecutor apiExecutor;

//    @Autowired
//    RequestHelper requestHelper;

    @ResponseBody
    @RequestMapping(path = "/**",method = RequestMethod.POST)
    public ApiResponse apiPost(HttpServletRequest request) throws Exception
    {
//        /*获取请求的URI*/
//        ApiURI apiURI = requestHelper.matchApiURI(request);
//        /*消息头和Url参数*/
//        Map<String,String> apiParams = requestHelper.collectApiParam(request);
//        Object postEntity = requestHelper.collectApiEntity(request);
//        /*查找API*/
////        IApi api = apiRegistry.findApi(apiURI);
//
////        Object resultData = apiExecutor.executePostInvoke(apiParams,apiRegistry,postEntity);
//        Object resultData = null;

        return ApiResponse.buildSuccessResponse("");

    }









}
