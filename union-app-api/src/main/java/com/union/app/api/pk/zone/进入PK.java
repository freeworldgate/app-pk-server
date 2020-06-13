package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.domain.pk.PkDetail;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkStatu;
import com.union.app.entity.pk.PkType;
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



        if(!userService.isUserVip(userId))
        {

            if(!StringUtils.equals(userId,pkEntity.getUserId())){


                InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId,userId);
                if(org.springframework.util.ObjectUtils.isEmpty(invitePkEntity))
                {
                    return AppResponse.buildResponse(PageAction.信息反馈框("仅邀请用户可见","您不是邀请用户"));
                }
                else
                {
                    return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
                }

            }
            else
            {
                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
            }




        }
        else
        {


            if(ObjectUtils.equals(pkEntity.getAlbumStatu(),PkStatu.审核中))
            {
                if(StringUtils.equals(userId,pkEntity.getUserId())){
                    return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
//
                }
                else {
                    return AppResponse.buildResponse(PageAction.信息反馈框("相册未激活","相册未激活"));
                }

            }
            else
            {
                if(!StringUtils.equals(userId,pkEntity.getUserId())){
                    InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId,userId);
                    if(org.springframework.util.ObjectUtils.isEmpty(invitePkEntity))
                    {
                        return AppResponse.buildResponse(PageAction.信息反馈框("仅邀请用户可见","您不是邀请用户"));
                    }
                    if(!pkService.是否更新今日审核群(pkId))
                    {
                        return AppResponse.buildResponse(PageAction.信息反馈框("提示","榜主未更新今日审核群"));
                    }
                    if(!pkService.是否更新今日公告(pkId))
                    {
                        return AppResponse.buildResponse(PageAction.信息反馈框("提示","榜主未发布公告"));
                    }


                }
                else
                {
                    if(!pkService.是否更新今日审核群(pkId))
                    {
                        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/message/message?pkId=" + pkId,true));
                    }



                }

            }


        }


        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId=" + pkId,true));
    }


}
