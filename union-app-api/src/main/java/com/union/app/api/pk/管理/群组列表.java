package com.union.app.api.pk.管理;

import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/pk")
public class 群组列表 {

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
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

//
//    @RequestMapping(path="/allCashierGroups",method = RequestMethod.GET)
//    public AppResponse 用户相册(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId) throws AppException, IOException {
//
//        appService.验证Password(password);
//
//        List<CashierGroup> groups = appService.查询用户群组(cashierId,1);
//
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",groups));
//
//    }
//    @RequestMapping(path="/nextPageCashierGroups",method = RequestMethod.GET)
//    public AppResponse 查询单个PK(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId,@RequestParam("page") int page) throws AppException, IOException {
//
//        appService.验证Password(password);
//        List<CashierGroup> groups = appService.查询用户群组(cashierId,page+1);
//
//
//        if(CollectionUtils.isEmpty(groups))
//        {
//            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));
//
//        }
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",groups));
//
//    }
//
//    @RequestMapping(path="/uploadCashierGroup",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 上传群组(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {
//        appService.验证Password(password);
//        appService.上传群组(cashierId,imgUrl);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//
//    }
//
//    @RequestMapping(path="/changeGroupStatu",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 修改状态(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId,@RequestParam("groupId") String groupId) throws AppException, IOException {
//        appService.验证Password(password);
//        appService.修改群组状态(cashierId,groupId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//
//    }
//
//
//    @RequestMapping(path="/deleteGroup",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse deleteGroup(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId,@RequestParam("groupId") String groupId) throws AppException, IOException {
//        appService.验证Password(password);
//        appService.删除群组(cashierId,groupId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//
//    }
//

//    /**
//     * 接收微信后台发来的用户消息
//     * @return
//     */
//    @RequestMapping(value = "/replaceCashierGroup", method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 更新群二维码(@RequestParam("password") String password,@RequestParam("cashierId") String cashierId,@RequestParam("groupId") String groupId,@RequestParam("imgUrl") String imgUrl) throws IOException, AppException {
//        appService.验证Password(password);
//        appService.更新群组(cashierId,groupId,imgUrl);
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//    }
//
//


}
