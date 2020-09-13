package com.union.app.api.pk.管理;

import com.union.app.domain.pk.CashierFeeCode;
import com.union.app.domain.pk.CashierGroup;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 收款码列表 {

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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;


    @RequestMapping(path="/allFeeCodes",method = RequestMethod.GET)
    public AppResponse allFeeCodes(@RequestParam("userId") String userId,@RequestParam("cashierId") String cashierId) throws AppException, IOException {



        List<CashierFeeCode> feeCodes = appService.查询用户收款码(cashierId);


        return AppResponse.buildResponse(PageAction.执行处理器("success",feeCodes));

    }


    @RequestMapping(path="/uploadFeeCode",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 上传群组(@RequestParam("userId") String userId,@RequestParam("cashierId") String cashierId,@RequestParam("feeNumber") int feeNumber,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {

        appService.上传收款码(cashierId,feeNumber,imgUrl);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }
//
//    @RequestMapping(path="/changeFeeCodeStatu",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 修改状态(@RequestParam("userId") String userId,@RequestParam("cashierId") String cashierId,@RequestParam("feeCodeId") String feeCodeId) throws AppException, IOException {
//
//        appService.修改收款码状态(cashierId,feeCodeId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//
//    }

//
//    @RequestMapping(path="/deleteFeeCode",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse deleteGroup(@RequestParam("userId") String userId,@RequestParam("cashierId") String cashierId,@RequestParam("feeCodeId") String feeCodeId) throws AppException, IOException {
//
//        appService.删除收款码(cashierId,feeCodeId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//
//    }


    @RequestMapping(path="/replaceFeeCode",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse replaceFeeCode(@RequestParam("userId") String userId,@RequestParam("cashierId") String cashierId,@RequestParam("feeCodeId") String feeCodeId,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {

        appService.修改收款码(cashierId,feeCodeId,imgUrl);

        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


}
