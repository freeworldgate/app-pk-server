package com.union.app.api.pk.投诉;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.complain.Complain;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PostService;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping(path="/pk")
public class 处理投诉信息 {


    @Autowired
    ComplainService complainService;

    @Autowired
    PostService postService;

    @Autowired
    DynamicService dynamicService;


    @RequestMapping(path="/complain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {
        Date date = new Date();

        PostEntity postEntity = postService.查询用户帖(pkId,userId);
        if(ObjectUtils.isEmpty(postEntity))
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","未发布你的榜帖，无权投诉..."));
        }


        if(postEntity.getStatu() == PostStatu.上线)
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","榜帖已审核通过，无法投诉榜主..."));
        }

        String approverId =  dynamicService.查询审核用户(pkId,postEntity.getPostId());
        if( StringUtils.isBlank(approverId))
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","未转发审核群或审核已过期需要重新转发..."));
        }
        if(dynamicService.审核等待时间过长(pkId,postEntity.getPostId()))
        {
            return AppResponse.buildResponse(PageAction.执行处理器("complain","审核时间已经超过" + AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间) +"分钟，确定投诉，虚假投诉将可能被封号,确定需要投诉榜主？"));
        }
        else
        {

            return AppResponse.buildResponse(PageAction.信息反馈框("未到投诉时间","按要求发布榜帖后，转发审核群后等待审核时间超过" +  AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间) + "分钟后方可投诉!"));

        }



//
//        complainService.添加投诉(pkId,userId);
//
//
//
//        return AppResponse.buildResponse(PageAction.信息反馈框("提示","已收到您的投诉信息，审核员违反主题规则，我们会尽快处理..."));



    }






    @RequestMapping(path="/confirmComplain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse confirmComplain(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {




        complainService.添加投诉(pkId,userId);



        return AppResponse.buildResponse(PageAction.信息反馈框("提示","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));



    }







    


}
