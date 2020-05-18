package com.union.app.api.pk.审核;

import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 榜主选择审核人员 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ApproveService approveService;

    @RequestMapping(path="/setApproveUser",method = RequestMethod.GET)
    public AppResponse 榜主选择审核人员(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {

        Date current = new Date();

        if(pkService.isPkCreator(pkId,userId)){
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","榜主为默认审核员..."));
        }

        dynamicService.验证预备审核员(pkId,userId,current);
        boolean result = dynamicService.添加预备审核员(pkId,userId);
        List<UserIntegral> approvers = dynamicService.查询预备审核用户列表(pkId,current);

        if(result)
        {
            return AppResponse.buildResponse(PageAction.执行处理器("success",approvers));

        }
        else
        {
            return AppResponse.buildResponse(PageAction.执行处理器("fail",approvers));
        }


    }

    @RequestMapping(path="/removeApproveUser",method = RequestMethod.GET)
    public AppResponse 取消审核人员(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {

        Date current = new Date();

        if(pkService.isPkCreator(pkId,userId)){
            return AppResponse.buildResponse(PageAction.信息反馈框("操作异常","榜主不可取消审核权..."));
        }
//
//        dynamicService.验证预备审核员(pkId,userId,current);
          dynamicService.删除预备审核员(pkId,userId);
//        List<UserIntegral> approvers = dynamicService.查询预备审核用户列表(pkId,current);
        return AppResponse.buildResponse(PageAction.执行处理器("success",""));

    }

    @RequestMapping(path="/approverDetail",method = RequestMethod.GET)
    public AppResponse 审核人员详情(@RequestParam("pkId") String pkId,@RequestParam("approverId") String approverId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {

        Date current = new Date();





        ApproveMessage approveMessage = approveService.获取审核人员消息(pkId,approverId,current);
        if(org.springframework.util.ObjectUtils.isEmpty(approveMessage))
        {
            if(StringUtils.equals(approverId,userId))
            {
                return AppResponse.buildResponse(PageAction.执行处理器("editMessage",""));
            }
            else
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("审核员未激活","审核用户未发布审核消息，激活审核用户..."));
            }












        }
        else
        {
            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/approverInfos/approverInfos?approverUserId=" + approverId + "&pkId=" + pkId,false));
        }



    }

}
