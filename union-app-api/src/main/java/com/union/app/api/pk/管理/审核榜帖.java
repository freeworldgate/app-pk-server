package com.union.app.api.pk.管理;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.PkDetail;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 审核榜帖 {

    @Autowired
    AppService appService;

    @Autowired
    AppDaoService daoService;

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

    public static Map<String,PkDetail> pkDetailMap = new HashMap<>();

    @RequestMapping(path="/manageApprovingPosts",method = RequestMethod.GET)
    public AppResponse 查询审核榜帖(@RequestParam("password") String password,@RequestParam("type") int type) throws AppException, IOException {
        appService.验证Password(password);

        List<DataSet> dataSets  = appService.查询下一个审核榜帖(type);

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/approvePost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse approvePost(@RequestParam("password") String password,@RequestParam("postId") String postId,@RequestParam("pkId") String pkId,@RequestParam("type") int type) throws AppException, IOException {
        //User认证
        appService.验证Password(password);


//        postService.上线帖子(pkId,postId);
        dynamicService.已审核(pkId,postId);

        List<DataSet> dataSets  = appService.查询下一个审核榜帖(type);





        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }
//
//    @RequestMapping(path="/hiddenPost",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse hiddenPost(@RequestParam("password") String password,@RequestParam("postId") String postId,@RequestParam("pkId") String pkId,@RequestParam("text") String text,@RequestParam("type") int type) throws AppException, IOException {
//        appService.验证Password(password);
//
//
//
//        PostEntity postEntity = postService.查询帖子ById(postId);
//        postEntity.setApproveStatu(ApproveStatu.驳回修改);
//        postEntity.setRejectTimes(postEntity.getRejectTimes() + 1);
//        postEntity.setRejectTextBytes(text);
//        daoService.updateEntity(postEntity);
//        List<DataSet> dataSets  = appService.查询下一个审核榜帖(type);
//
//        dynamicService.驳回用户审核(pkId,postId);
//        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
//
//
//    }
//


}
