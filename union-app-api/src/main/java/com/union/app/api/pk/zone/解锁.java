package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.OssStorage;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.PkUnlockEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkStatu;
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
public class 解锁 {


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

    @RequestMapping(path="/unlock",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 进入PK(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId) throws AppException, IOException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);


        if(StringUtils.equals(pkEntity.getUserId(),userId)){return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));}
        if(pkEntity.getAlbumStatu() != PkStatu.已审核){return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));}
        InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId,userId);
        if(!org.springframework.util.ObjectUtils.isEmpty(invitePkEntity)){return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));}

        PkUnlockEntity unlockEntity = appService.查询用户解锁(pkId,userId);
        if(org.springframework.util.ObjectUtils.isEmpty(unlockEntity))
        {
            appService.添加解锁Pk(pkId,userId);

            userService.解锁次数加1(userId);
        }










        return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));


    }


}
