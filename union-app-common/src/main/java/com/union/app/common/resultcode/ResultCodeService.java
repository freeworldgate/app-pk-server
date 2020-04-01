package com.union.app.common.resultcode;

import com.union.app.common.spring.context.SpringContextUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.外部错误码映射表.ResultCodeEntity;

import java.util.List;

public class ResultCodeService {

    private static AppDaoService appDao = SpringContextUtil.getBean(AppDaoService.class);



    public static ResultCodeEntity queryResultCode(String resultCode)
    {
        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(ResultCodeEntity.class).compareFilter("resultCode",CompareTag.Equal,resultCode);
        List<ResultCodeEntity> resultCodeEntities = appDao.queryEntities(ResultCodeEntity.class,entityFilterChain);
        if(resultCodeEntities == null || resultCodeEntities.isEmpty()){
            return null;
        }
        return resultCodeEntities.get(0);
    }



}
