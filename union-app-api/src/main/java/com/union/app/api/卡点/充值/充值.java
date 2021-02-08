package com.union.app.api.卡点.充值;

import com.alibaba.fastjson.JSONObject;
import com.union.app.domain.pk.支付.Pay;
import com.union.app.domain.pk.支付.PayOrder;
import com.union.app.entity.pk.支付.PayOrderEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.捞人.FindService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 充值 {


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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    PayService payService;

    @Autowired
    FindService findService;


    @RequestMapping(path="/pay",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse pay(@RequestParam("userId") String userId,@RequestParam("type") int type) throws AppException {

        //校验
        Pay pay = payService.查询充值选项(type);

        List<DataSet> dataSets = new ArrayList<>();


        dataSets.add(new DataSet("pay",pay));
        dataSets.add(new DataSet("selectPay",pay.getSelectPay()));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));


    }



    @RequestMapping(path="/payType",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse payForTime(@RequestParam("userId") String userId,@RequestParam("type") int type,@RequestParam("payId") String payId) throws Exception {

        //校验
        PayOrderEntity payOrderEntity = payService.创建订单(userId,type,payId);

        payOrderEntity = payService.getPayInfo(userId,payOrderEntity);

        return AppResponse.buildResponse(PageAction.执行处理器("success",payOrderEntity));

    }
    @RequestMapping(path="/paySuccess",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse paySuccess(@RequestParam("userId") String userId,@RequestParam("orderId") String orderId) throws Exception {


        if(payService.订单是否成功(orderId)){

            payService.处理成功订单(orderId);



            return AppResponse.buildResponse(PageAction.前端数据更新("statu","success"));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("支付失败","支付失败!"));
        }

    }









}
