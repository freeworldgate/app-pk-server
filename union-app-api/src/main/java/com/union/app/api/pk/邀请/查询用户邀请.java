package com.union.app.api.pk.邀请;

import com.union.app.domain.pk.ApproveButton;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.ApproveService;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询用户邀请 {


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
    @Autowired
    AppService appService;
    /**
     * 审核员审批页面入口
     * @param pkId
     * @param userId
     * @return
     * @throws AppException
     * @throws IOException
     */
    @RequestMapping(path="/queryInvite",method = RequestMethod.GET)
    public AppResponse queryInvite(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);

        if(StringUtils.equals(pkEntity.getUserId(),userId)){return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));}
//        if(pkEntity.getAlbumStatu() != PkStatu.已审核){return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));}
        PostEntity postEntity = postService.查询用户帖(pkId,userId);
//        InvitePkEntity invitePkEntity = appService.queryInvitePk(pkId,userId);
        if(!org.springframework.util.ObjectUtils.isEmpty(postEntity)){return AppResponse.buildResponse(PageAction.前端数据更新("inviteTag",true));}




        return AppResponse.buildResponse(PageAction.执行处理器("invite",pkId));

    }



}
