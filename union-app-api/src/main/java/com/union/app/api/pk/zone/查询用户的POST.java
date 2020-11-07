package com.union.app.api.pk.zone;

import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PayPolicy;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.ValueStr;
import com.union.app.entity.pk.InvitePkEntity;
import com.union.app.entity.pk.InviteType;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.UserKvEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping(path="/pk")
public class 查询用户的POST {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    UserService userService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    CacheStorage cacheStorage;

    @Autowired
    PostService postService;

    @Autowired
    AppService appService;


    @RequestMapping(path="/queryUserPost",method = RequestMethod.GET)
    public AppResponse 查询用户的POST(@RequestParam("pkId") String pkId, @RequestParam("userId") String userId) throws AppException, IOException {

            PostEntity post = postService.查询用户帖(pkId,userId);

            if(!ObjectUtils.isEmpty(post))
            {

                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/prepost/prepost?pkId=" + pkId + "&postId=" + post.getPostId(),true));
//                return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/post/post?pkId=" + pkId + "&postId=" + post.getPostId(),true));
            }
            else
            {

                return AppResponse.buildResponse(PageAction.信息反馈框("图册未发布", "没有找到您的图册内容，点击发布图册编辑内容..."));

            }













    }



    @RequestMapping(path="/checkUserPost",method = RequestMethod.GET)
    public AppResponse check用户的POST(@RequestParam("pkId") String pkId, @RequestParam("userId") String userId, HttpServletRequest request) throws AppException, IOException {

        PostEntity post = postService.查询用户帖(pkId,userId);
        if(!ObjectUtils.isEmpty(post)){

            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/prepost/prepost?pkId=" + pkId + "&postId=" + post.getPostId(),true));
        }



        if(!pkService.isPkCreator(pkId,userId))
        {
            String url = "";
            ValueStr valueStr = new ValueStr(url, "编辑图册", "请按照主题编辑图册...");
            return AppResponse.buildResponse(PageAction.执行处理器("uploadImgs",valueStr));

        }
        else
        {

            if(appService.是否收费(userId)) {

                UserKvEntity userEntity = userService.queryUserKvEntity(userId);
                //总可激活次数  和  已用次数
                if (userEntity.getFeeTimes() > 0) {
                    //不用收费:提示:图册发布后将激活该主题
                    String url = "";
                    ValueStr valueStr = new ValueStr(url, "激活主题", "发布图册将视为激活该图册,剩余可激活主题次数为" + (userEntity.getFeeTimes())+"...");
                    return AppResponse.buildResponse(PageAction.执行处理器("uploadImgs",valueStr));

                }
                else
                {
                    String _singlePkPrice = "";
                    String _12PkPrice = "";
                    //收费：返回收费方案供用户选择。
                    if(userService.是否是遗传用户(userId))
                    {
                        _singlePkPrice = AppConfigService.getConfigAsString(ConfigItem.遗传用户单个主题费用);
                        _12PkPrice = AppConfigService.getConfigAsString(ConfigItem.遗传用户12个主题打包费用);

                    }
                    else
                    {
                        _singlePkPrice = AppConfigService.getConfigAsString(ConfigItem.普通用户单个主题费用);
                        _12PkPrice = AppConfigService.getConfigAsString(ConfigItem.普通用户12个主题打包费用);
                    }
                    String _a_singlePkPrice = String.valueOf(Double.valueOf(_singlePkPrice) * 10 * 10);
                    String _a_12singlePkPrice = String.valueOf(Double.valueOf(_12PkPrice) * 10 * 10);
                    //

                    return AppResponse.buildResponse(PageAction.执行处理器("pay",appService.获取支付信息(userId,request)));




                }
            }
            else
            {
                String url = "";
                ValueStr valueStr = new ValueStr(url, "编辑图册", "发布图册将视为激活该图册,请按照主题要求编辑图册...");
                return AppResponse.buildResponse(PageAction.执行处理器("uploadImgs",valueStr));


            }

//                String url = "";
//                ValueStr valueStr = new ValueStr(url, "编辑图册", "请按照主题要求编辑图册...");
//                return AppResponse.buildResponse(PageAction.执行处理器("uploadImgs",valueStr));

        }













    }


}
