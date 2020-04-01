package com.union.app.service.pk.share;


import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.ShareDynamicMessage;
import com.union.app.domain.pk.UserCode;
import com.union.app.domain.pk.share.CreatorInfo;
import com.union.app.domain.pk.share.PosterInfo;
import com.union.app.domain.pk.share.ShareInfo;
import com.union.app.domain.pk.share.ShareWindow;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.UserDynamicEntity;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ShareService {

    @Autowired
    AppDaoService appDaoService;

    @Autowired
    PkService pkService;

    @Autowired
    UserService userService;


    @Autowired
    UserInfoService userInfoService;





























    public ShareWindow 获取分享窗口数据(String pkId,String userId){

        ShareWindow shareWindow = new ShareWindow();

        UserCode userCode = userInfoService.查询收款码信息(pkId,userId);
        boolean shareSwitch = this.是否有权限分享(pkId,userId);


        PosterInfo posterInfo = this.查询打榜用户信息(pkId);
        CreatorInfo creatorInfo = this.查询榜主信息(pkId);
        ShareInfo shareInfo = this.查询分享用户信息(pkId);

        List<ShareDynamicMessage> shareDynamicMessages = this.查询ShareMessage动态信息();


        shareWindow.setCreatorInfo(creatorInfo);
        shareWindow.setPosterInfo(posterInfo);
        shareWindow.setShareInfo(shareInfo);
        shareWindow.setMessage(shareDynamicMessages);
        shareWindow.setPkId(pkId);
        shareWindow.setUserId(userId);
        shareWindow.setUserFeeCodeUrl(ObjectUtils.isEmpty(userCode)?null:userCode.getUrl());
        shareWindow.setShareSwitch(shareSwitch);

        return shareWindow;
    }

    private List<ShareDynamicMessage> 查询ShareMessage动态信息() {






        return null;
    }






    private boolean 是否有权限分享(String pkId, String userId) {
        //是否有发帖或者打赏过：
        return false;

    }


    private ShareInfo 查询分享用户信息(String pkId) {





        return null;
    }


    private CreatorInfo 查询榜主信息(String pkId) {
        User user = pkService.queryPkCreator(pkId);






        return null;
    }


    private PosterInfo 查询打榜用户信息(String pkId) {











        return null;
    }


    private String 查询用户收钱码(String userId){

        if(StringUtils.isEmpty(userId)){return null;}

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserDynamicEntity.class)
                    .compareFilter("userId",CompareTag.Equal,userId);
        UserDynamicEntity userDynamicEntity = appDaoService.querySingleEntity(UserDynamicEntity.class,filter);
        if(ObjectUtils.isEmpty(userDynamicEntity))
        {
            return null;
        }
        else
        {
            return userDynamicEntity.getFeePayUrl();
        }
    }













































}
