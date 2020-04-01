package com.union.app.api.应用信息;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.应用信息表.ApplicationEntity;
import com.union.app.entity.用户.UserEntity;
import com.union.app.plateform.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/app")
public class 应用信息 {

    @Autowired
    AppDaoService appDaoService;

    @RequestMapping(path="/info",method = RequestMethod.GET)
    public ApiResponse 注册()
    {

        ApplicationEntity applicationEntity = appDaoService.querySingleEntity(ApplicationEntity.class,null);

        return ApiResponse.buildSuccessResponse(applicationEntity);
    }



}
