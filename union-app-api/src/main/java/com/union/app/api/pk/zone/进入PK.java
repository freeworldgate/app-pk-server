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
        if(pkService.isPkCreator(pkId,userId))
        {
            if(userService.是否是遗传用户(userId) && !pkService.是否更新今日审核群(pkEntity))
            {
                String url = "/pages/pk/message/message?pkId=" + pkId + "&type=1" + "&userId=" + userId;
                ValueStr valueStr = new ValueStr(url, "更新主题群", "更新主题群,用户发布图册后添加主题群...");
                return AppResponse.buildResponse(PageAction.执行处理器("group",valueStr));
            }
            if(ObjectUtils.equals(pkEntity.getAlbumStatu(),PkStatu.审核中)){

                String url = "";
                ValueStr valueStr = new ValueStr(url, "发布主题", "确定发布主题，发布后将无法修改主题内容...");
                return AppResponse.buildResponse(PageAction.执行处理器("doApprove", valueStr));
            }
        }



        //非遗传用户且无解锁限制，则任意进入任何PK
        if(!AppConfigService.getConfigAsBoolean(ConfigItem.普通用户发帖后解锁更多主题) && !userService.是否是遗传用户(userId))
        {
            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
        }


        //邀请或者非邀请

        if(!pkService.isPkCreator(pkId,userId))
        {
            UserEntity userEntity = userService.queryUserEntity(userId);
            InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId,userId);
            if(!org.springframework.util.ObjectUtils.isEmpty(invitePkEntity))
            {
                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
            }
            else
            {
                //邀请主题数量
                int invites = userEntity.getInviteTimes();
                int posts = userEntity.getPostTimes();
                if(invites < (posts + 1) * AppConfigService.getConfigAsInteger(ConfigItem.邀请和可解锁主题倍数关系))
                {
                    ValueStr valueStr = new ValueStr("/pages/pk/pk/pk?pkId=" + pkId,"解锁主题","根据您发布有效图册数量,您剩余可解锁主题为" + ((posts+1)*3 - invites) + "个,解锁主题将添加到我的邀请列表,请选择您感兴趣的主题...");
                    return AppResponse.buildResponse(PageAction.执行处理器("unlock",valueStr));
                }
                else
                {
                    return AppResponse.buildResponse(PageAction.信息反馈框("图册不足","根据您发布的图册数量,可解锁主题已达上限，您可以通过发布更多图册解锁更多主题..."));
                }

            }




        }









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
