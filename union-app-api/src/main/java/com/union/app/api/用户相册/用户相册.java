package com.union.app.api.用户相册;

import com.union.app.domain.pk.PkDetail;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 用户相册 {

    @Autowired
    AppService appService;

    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;


    @RequestMapping(path="/userPks",method = RequestMethod.GET)
    public AppResponse 用户相册(@RequestParam("userId") String userId) throws AppException, IOException {

//        List<PkDetail> pkDetails = new ArrayList<>();

        List<PkDetail> pks = appService.查询用户相册(userId,1);

        appService.vip包装(pks,userId,"");
        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }
    @RequestMapping(path="/nextUserPks",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

//        List<PkDetail> pkDetails = new ArrayList<>();
        List<PkDetail> pks = appService.查询用户相册(userId,page);
        appService.vip包装(pks,userId,"");
//        if(!userService.isUserVip(userId) )
//        {
//            for(PkDetail pkDetail:pks)
//            {
//                if(pkDetail.getPkType() != PkType.预设相册)
//                {
//                    pkDetails.add(pkDetail);
//                }
//            }
//        }
//        else
//        {
//            pkDetails.addAll(pks);
//        }


        if(CollectionUtils.isEmpty(pks))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",pks));

    }


}
