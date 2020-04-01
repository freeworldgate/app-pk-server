package com.union.app.common.request;

import com.union.app.common.api.registry.ApiURI;
import com.union.app.common.api.registry.bean.ServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * 请求处理
 */
//@Component
public class RequestHelper {

    @Autowired
    ServiceBean apiRegistry;


    @Value("${union.apiRegistry.mode}")
    private String apiMode;




    public Map<String,String> collectApiParam(HttpServletRequest request) {

        return Collections.emptyMap();
    }

    public ApiURI matchApiURI(HttpServletRequest request)  {
/*
        String uri = request.getRequestURI();
        ApiURI apiURI = apiRegistry.findURI(uri);

        */
/*没有找到对应的服务，如果当前模式是测试模式，那么重加载Jar文件*//*

        if(apiURI == null && "test".equalsIgnoreCase(this.apiMode) )
        {
            try {
                apiRegistry.loadApi();
            } catch (IOException e) {
                throw ApiException.buildException(ResultCode.E04000003,null);
            }
            apiURI = apiRegistry.findURI(uri);
        }
        if(apiURI == null)
        {
            */
/*不存在的API URI*//*

            throw ApiException.buildException(ResultCode.E04000002,null);
        }
*/
        return null;
    }

    public Object collectApiEntity(HttpServletRequest request) {
        return null;
    }
}
