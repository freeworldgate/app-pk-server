package com.union.app.service.app;

import com.union.app.common.config.AppConfigService;
import com.union.app.entity.pk.AppMode;
import com.union.app.service.pk.dynamic.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {


    @Autowired
    DynamicService dynamicService;



    /**
     * 判断APP是否是商业部署模式
     * @param appName
     * @return
     */
    public static boolean isBussinessApp(String appName) {
        //查表


        return false;
    }


    public AppMode getMode(String pkId, String appName) {
        return AppMode.Pay;
//        int peaple = dynamicService.查询参战人数(pkId);
//        return AppConfigService.getConfigAsInteger("PEOPLE_" + appName,1000) < peaple? AppMode.Pay : AppMode.Free;
    }
}
