package com.union.app.api.pk.zone;

import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/pk")
public class 参战 {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;




    @Autowired
    AppService appService;

    @RequestMapping(path="/addPost",method = RequestMethod.GET)
    public AppResponse 参战(@RequestParam("userId") String userId,@RequestParam("pkId") String pkId) throws AppException {

//        if(true)
//        {
//            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/publishPost/publishPost",false));
//        }
//
//
//
//        PostEntity postEntity = pkService.querySinglePostEntity(userId,pkId);
//
//        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
//
//        if(!ObjectUtils.isEmpty(postEntity))
//        {
//            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/userSinglePk/userSinglePk?postId=" + postEntity.getPostId(),false));
//        }
//        //当前模式
//        if(appService.getMode(pkEntity.getPkId(),pkEntity.getAppName()) == AppMode.Pay)
//        {
//            //查询用户是否有空榜帖
//            EmptyPostEntity emptyPostEntity = pkService.queryUserEmptyPost(userId,pkId);
//
//
//
//            if(!ObjectUtils.isEmpty(emptyPostEntity)){
//                // 操作框
//                return AppResponse.buildResponse(PageAction.执行处理器("publishPost",pkId));
//            }
//            else
//            {
//                // 操作框
//                return AppResponse.buildResponse(PageAction.执行处理器("pay",pkId));
//            }
//
//        }
//        else
//        {
//            //直接编辑榜帖
            return AppResponse.buildResponse(PageAction.执行处理器("free",pkId));
//        }
    }
}
