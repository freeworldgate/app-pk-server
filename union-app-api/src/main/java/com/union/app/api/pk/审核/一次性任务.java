package com.union.app.api.pk.审核;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.entity.pk.ApproveStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
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
public class 一次性任务 {


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



    @RequestMapping(path="/oneTimeTask",method = RequestMethod.GET)
    public AppResponse 提示审核员(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {

        Date current = new Date();



        PostEntity postEntity = postService.查询用户帖(pkId,userId);


        if(pkService.isPkCreator(pkId, userId))
        {
                if(StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkId, current))){
                    return AppResponse.buildResponse(PageAction.执行处理器("groupCode", ""));
                }
                ApproveMessage approveMessage = approveService.获取审核人员消息(pkId);

                if (org.springframework.util.ObjectUtils.isEmpty(approveMessage)) {
                    return AppResponse.buildResponse(PageAction.执行处理器("editApproverMessage", ""));
                }
        }
        if(org.springframework.util.ObjectUtils.isEmpty(postEntity)){
            return AppResponse.buildResponse(PageAction.执行处理器("publish",""));
        }

        if(postEntity.getStatu() == PostStatu.审核中)
        {
            if(postEntity.getApproveStatu() == ApproveStatu.未处理)
            {
                return AppResponse.buildResponse(PageAction.执行处理器("select", postEntity.getPostId()));
            }
            if(postEntity.getApproveStatu() == ApproveStatu.驳回修改)
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("榜帖修改建议","修改建议:" + postEntity.getRejectTextBytes() == null?"":postEntity.getRejectTextBytes()));
            }

        }





//        if ( (postEntity.getStatu() == PostStatu.审核中) && StringUtils.isBlank(dynamicService.查询审核用户(pkId, postEntity.getPostId()))) {
//            return AppResponse.buildResponse(PageAction.执行处理器("select", postEntity.getPostId()));
//        }






        return AppResponse.buildResponse(PageAction.执行处理器("no",""));


    }


}
