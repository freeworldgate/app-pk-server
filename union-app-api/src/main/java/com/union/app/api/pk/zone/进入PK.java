package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkActive;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.TipConstant;
import com.union.app.domain.pk.ValueStr;
import com.union.app.entity.pk.*;
import com.union.app.entity.用户.UserEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 进入PK {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    UserService userService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;



    @Autowired
    AppService appService;

    @RequestMapping(path="/viewPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 进入PK(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId) throws AppException, IOException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        if(pkEntity.getPkType()==PkType.内置相册 && pkEntity.getIsInvite() == InviteType.邀请 )
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("仅邀请用户可见","您不是邀请用户"));
        }




        //邀请或者非邀请

        if(pkService.isPkCreator(pkId,userId))
        {

            if(!pkService.是否更新今日审核群(pkEntity))
            {
                String url = "/pages/pk/message/message?pkId=" + pkId + "&type=1" + "&userId=" + userId;
                ValueStr valueStr = null;
                if(userService.是否是遗传用户(userId)) {
                     valueStr = new ValueStr(url, "更新审核群", "更新今日审核群...");
                }
                else
                {
                    valueStr = new ValueStr(url, "更新主题群", "更新今日主题群组");
                }
                return AppResponse.buildResponse(PageAction.执行处理器("group",valueStr));
            }
            //遗传用户创建者未更新今日公告
            if(!pkService.是否更新今日公告(pkId))
            {
                String url = "/pages/pk/messageInfo/messageInfo?pkId=" + pkId;
                ValueStr valueStr = new ValueStr(url,"图贴样例","请按要求上传图贴样例...");
                return AppResponse.buildResponse(PageAction.执行处理器("message",valueStr));
            }

            if( ObjectUtils.equals(pkEntity.getAlbumStatu(),PkStatu.审核中)){

                if(userService.是否是遗传用户(userId)) {
                    String url = "/pages/pk/selectPker/selectPker?pkId=" + pkId;
                    ValueStr valueStr = new ValueStr(url, "激活主题", "确定要激活主题?,审核通过后可以使用...");
                    return AppResponse.buildResponse(PageAction.执行处理器("approve", valueStr));
                }
                else
                {
                    PkActive active = appService.查询激活信息(pkId);
                    if(org.springframework.util.ObjectUtils.isEmpty(active))
                    {
                        String url = "";
                        ValueStr valueStr = new ValueStr(url, "发布主题", "确定发布主题...");
                        return AppResponse.buildResponse(PageAction.执行处理器("doApprove", valueStr));
                    }
                    else
                    {
                        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/selectPker/selectPker?pkId=" + pkId,true));

                    }

                }
            }

        }
        else
        {


            if(userService.用户解锁(userId))
            {
                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));

            }
            else
            {
                return AppResponse.buildResponse(PageAction.信息反馈框("用户未解锁","至少发布一次图贴，解锁后可以查看和发布主题"));
            }




//
//
//            if(!userService.是否是遗传用户(userId))
//            {
//                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
//            }
//            else
//            {
//                  UserEntity userEntity = userService.queryUserEntity(userId);
//                 if(userEntity.getPostTimes() < 1)
//                 {
//                     return AppResponse.buildResponse(PageAction.信息反馈框("用户未解锁","至少发布一次图贴，解锁后可以查看和发布主题"));
//                 }
//                 else
//                 {
//                     return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
//                 }
//
//
//
//
//
//
//
//            }

        }



//
//        if(!pkService.isPkCreator(pkId,userId)  && !pkService.是否更新今日审核群(pkEntity))
//        {
////            return AppResponse.buildResponse(PageAction.信息反馈框("提示","榜主未更新今日审核群"));
//        }







        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));















//
//        if(!userService.canUserView(userId))
//        {
//
//            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
//
//        }
//        else
//        {
//
//
//            if(!ObjectUtils.equals(pkEntity.getAlbumStatu(),PkStatu.已审核))
//            {
//                //未审核
//
//
//            }
//            else
//            {
//                if(!StringUtils.equals(userId,pkEntity.getUserId())){
//
//
//                    if(!pkService.是否更新今日审核群(pkId))
//                    {
//                        return AppResponse.buildResponse(PageAction.信息反馈框("提示","榜主未更新今日审核群"));
//                    }
//
//
//
//                }
//                else
//                {
//
//
//                }
//
//            }
//
//
//        }



    }


}
