package com.union.app.gateway.entry;


import com.union.app.plateform.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

/**
 * @author root
 */
@RestController
@RequestMapping(path="/api")
public class AppGatewayApiGetController
{

/*
    @Autowired
    ApiRegistry apiRegistry;*/
//
//    @Autowired
//    ApiExecutor apiExecutor;
//
//    @Autowired
//    RequestHelper requestHelper;


    @ResponseBody
    @RequestMapping(path = "/**",method = RequestMethod.GET)
    @Transactional
    public ApiResponse apiGet(HttpServletRequest request) throws Exception
    {

//        /*获取请求的URI*/
//        ApiURI apiURI = requestHelper.matchApiURI(request);
//        /*消息头和Url参数*/
//        Map<String,String> apiParams = requestHelper.collectApiParam(request);
//        /*查找API*/
////        IApi api = apiRegistry.findApi(apiURI);
//
////        Object resultData = apiExecutor.executeGetInvoke(apiParams,apiRegistry);

        return ApiResponse.buildSuccessResponse(null);

    }









}
