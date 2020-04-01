package com.union.app.gateway.executor;

import com.union.app.common.api.registry.bean.ServiceBean;
import com.union.app.common.api.resolver.ServiceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Api执行
 * @author fenghao
 */
@Component
public class ApiExecutor {


//    @Autowired
//    ServiceResolver apiUtilResolver;


    public Object executeGetInvoke(Map<String,String> apiParams, ServiceBean apiRegistry)
    {
//        //API服务ID
//        String apiUniquedId = UUID.randomUUID().toString();
//        Transaction transaction = apiUtilResolver.jpaResolver.beginTransaction();
//        ApiTransaction apiTransaction = new ApiTransaction(apiUniquedId,transaction);
//
//        try{
//            throw ApiException.buildException(ResultCode.E99999999,null,false);
//        }
//        catch (ApiException e)
//        {
//            //分析 apiTransaction
////            apiTransaction.getServiceTransaction().getTransactionStatus().
//        }











        return null;
    }
}
