package com.union.app.api.pk.审核;

import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 设置留言 {


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

    @RequestMapping(path="/setComment",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置留言(@RequestParam("pkId") String pkId,  @RequestParam("userId") String userId,@RequestParam("text") String text,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {

        Date currentDay = new Date();

//        List<DataSet> dataSets = new ArrayList<>();
//        PostEntity postEntity = postService.查询用户帖(pkId,userId);
//        if(postEntity.getStatu() != PostStatu.审核中){
//            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"当前状态不支持设置留言"));
//        }
//
//        approveService.设置审核留言(pkId,postEntity.getPostId(),userId,text,imgUrl);
//        ApproveComment approveComment = approveService.获取留言信息(pkId,postEntity.getPostId());

        return AppResponse.buildResponse(PageAction.执行处理器("message",""));













    }



}
