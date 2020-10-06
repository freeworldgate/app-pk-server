package com.union.app.api.pk.投诉;

import com.union.app.common.config.AppConfigService;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
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

    @Autowired
    PkService pkService;

    @Autowired
    RedisSortSetService redisSortSetService;


    @RequestMapping(path="/complain",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询投诉信息(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("text") String text) throws AppException, IOException {
        Date date = new Date();

        PostEntity postEntity = postService.查询用户帖(pkId,userId);
        if(ObjectUtils.isEmpty(postEntity))
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("未发布图册","请按照主题要求编辑图册方可对主题进行投诉..."));
        }


        if(postEntity.getStatu() == PostStatu.上线)
        {
//            return AppResponse.buildResponse(PageAction.信息反馈框("提示","图贴已发布..."));
            return AppResponse.buildResponse(PageAction.信息反馈框("消息通知","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
        }


        if(postEntity.getRejectTimes() > 0)
        {
            complainService.添加投诉(pkId,userId,text);
//            return AppResponse.buildResponse(PageAction.信息反馈框("提示","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
            return AppResponse.buildResponse(PageAction.信息反馈框("消息通知","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
        }
        if(pkService.是否审核中(pkId,postEntity.getPostId()) )//      redisSortSetService.isMember(CacheKeyName.榜主审核中列表(pkId) ,postEntity.getPostId()))
        {
            if(dynamicService.审核等待时间过长(pkId,postEntity.getPostId()))
            {

                complainService.添加投诉(pkId,userId,text);
                return AppResponse.buildResponse(PageAction.信息反馈框("消息通知","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
            }
            else
            {
//                return AppResponse.buildResponse(PageAction.信息反馈框("消息通知","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
                return AppResponse.buildResponse(PageAction.信息反馈框("未到投诉时间","发布图册后，转发审核群后等待审核时间超过" +  AppConfigService.getConfigAsInteger(ConfigItem.榜帖可发起投诉的等待时间) + "分钟后方可投诉!"));

            }
        }
        else
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","图册未审核，请先发布图册..."));
//            return AppResponse.buildResponse(PageAction.信息反馈框("消息通知","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));
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
    public AppResponse confirmComplain(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("text") String text) throws AppException, IOException {




        complainService.添加投诉(pkId,userId,text);



        return AppResponse.buildResponse(PageAction.信息反馈框("提示","已收到您的投诉信息，如若榜主违反主题规则，我们会尽快处理..."));



    }







    


}
