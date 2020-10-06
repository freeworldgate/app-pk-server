package com.union.app.api.pk.审核;

import com.union.app.domain.pk.ValueStr;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.entity.pk.ApproveStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

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
    ApproveService approveService;



    @RequestMapping(path="/oneTimeTask",method = RequestMethod.GET)
    public AppResponse 提示审核员(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {

        Date current = new Date();



        PostEntity postEntity = postService.查询用户帖(pkId,userId);


        if(pkService.isPkCreator(pkId, userId))
        {
                if(StringUtils.isBlank(dynamicService.查询PK群组二维码MediaId(pkId))){
                    ValueStr valueStr = null;
                    if(userService.是否是遗传用户(userId)) {
                        valueStr = new ValueStr("", "更新审核群", "更新今日审核群,审核图册，每天24:00请及时更新当日审核群...");
                    }
                    else
                    {
                        valueStr = new ValueStr("", "更新主题群", "请更新主题群，以方便用户交流...");
                    }

                    return AppResponse.buildResponse(PageAction.执行处理器("groupCode", valueStr));


                }
                ApproveMessage approveMessage = approveService.获取审核人员消息(pkId);

                if (org.springframework.util.ObjectUtils.isEmpty(approveMessage)) {
                    ValueStr valueStr = null;
                    if(userService.是否是遗传用户(userId)) {
                        valueStr = new ValueStr("", "发布图册封面", "制作图册封面并发布...");
                    }
                    else
                    {
                        valueStr = new ValueStr("", "发布图册封面", "制作图册封面并发布...");
                    }
                    return AppResponse.buildResponse(PageAction.执行处理器("editApproverMessage", valueStr));
                }
        }
        if(org.springframework.util.ObjectUtils.isEmpty(postEntity)){

            ValueStr valueStr = null;
            if(userService.是否是遗传用户(userId)) {
                valueStr = new ValueStr("", "没有找到您的图册", "您可以在主题下分享您的图册...");
            }
            else
            {
                valueStr = new ValueStr("", "没有找到您的图册", "您可以在主题下分享您的图册...");
            }
            return AppResponse.buildResponse(PageAction.前端数据更新("publish",true));
//            return AppResponse.buildResponse(PageAction.执行处理器("publish",valueStr));
        }

        if(postEntity.getStatu() == PostStatu.审核中)
        {
            if(postEntity.getApproveStatu() == ApproveStatu.未处理)
            {
                ValueStr valueStr = null;
                if(userService.是否是遗传用户(userId)) {
                    valueStr = new ValueStr(postEntity.getPostId(), "图册未发布", "您的图册暂未审核,审核通过后才可以发布......");
                }
                else
                {
                    valueStr = new ValueStr(postEntity.getPostId(), "图册未发布", "您的图册暂未审核,审核通过后才可以发布......");
                }

                return AppResponse.buildResponse(PageAction.执行处理器("select", valueStr));
            }
            if(postEntity.getApproveStatu() == ApproveStatu.驳回修改)
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("管理员驳回您的图册","修改建议:" + (postEntity.getRejectTextBytes() == null?"":postEntity.getRejectTextBytes())));
            }

        }





//        if ( (postEntity.getStatu() == PostStatu.审核中) && StringUtils.isBlank(dynamicService.查询审核用户(pkId, postEntity.getPostId()))) {
//            return AppResponse.buildResponse(PageAction.执行处理器("select", postEntity.getPostId()));
//        }






        return AppResponse.buildResponse(PageAction.执行处理器("no",""));


    }


}
