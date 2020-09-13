package com.union.app.api.pk.管理;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.cashier.PkCashier;
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
public class 收款用户 {

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


    @RequestMapping(path="/allCashiers",method = RequestMethod.GET)
    public AppResponse 用户相册(@RequestParam("password") String password) throws AppException, IOException {



        List<PkCashier> cashiers = appService.查询收款列表(1);


        return AppResponse.buildResponse(PageAction.执行处理器("success",cashiers));

    }
    @RequestMapping(path="/nextPageCashiers",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("password") String password,@RequestParam("page") int page) throws AppException, IOException {


        List<PkCashier> cashiers = appService.查询收款列表(page+1);


        if(CollectionUtils.isEmpty(cashiers))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",cashiers));

    }

    @RequestMapping(path="/createCashier",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 创建用户(@RequestParam("password") String password,@RequestParam("name") String name) throws AppException, IOException {
        appService.新建收款用户(name);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }



    @RequestMapping(path="/changeCahierStatu",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 修改用户状态(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId) throws AppException, IOException {
        appService.修改用户状态(cashierId);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


    @RequestMapping(path="/uploadCashierLink",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 上传链接(@RequestParam("password") String password,@RequestParam("linkImg") String linkImg,@RequestParam("cashierId") String cashierId,@RequestParam("type") int type) throws AppException, IOException {
        appService.上传收款链接(cashierId,type,linkImg);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }


    @RequestMapping(path="/deleteCashier",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 上传链接(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId) throws AppException, IOException {
        appService.删除收款人(cashierId);


        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }









}
