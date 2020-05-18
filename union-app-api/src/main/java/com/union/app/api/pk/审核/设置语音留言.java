package com.union.app.api.pk.审核;

import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
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

@RestController
@RequestMapping(path="/pk")
public class 设置语音留言 {


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
    UserInfoService userInfoService;

    @Autowired
    ApproveService approveService;

    @RequestMapping(path="/setCommentVoice",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置语音留言(@RequestParam("pkId") String pkId, @RequestParam("approvorId") String approvorId,  @RequestParam("userId") String userId,@RequestParam("fileUrl") String fileUrl,@RequestParam("speckTime") int speckTime) throws AppException, IOException {
//
        Date currentDay = new Date();


        PostEntity postEntity = postService.查询用户帖(pkId,userId);


        approveService.设置语言留言(pkId,postEntity.getPostId(),approvorId,fileUrl,speckTime);
//
//        List<ApproveUser> newApproveUserList = new ArrayList<>();
//
////        List<ApproveUser> approveUserList = approveService.查询今日所有审核用户(pkId,postEntity.getPostId());
//
        ApproveUser currentApproveUser = approveService.查询审核用户WidthCommentById(pkId,postEntity.getPostId(),approvorId,currentDay);
////        查询帖子的审核用户(pkId,postEntity.getPostId());
////        for(ApproveUser approveUser:approveUserList){
////            if(org.apache.commons.lang.StringUtils.equals(approveUser.getUser().getUserId(),approvorId)){
////                currentApproveUser = approveUser;
////            }
////
////        }
//
//        DataSet dataSet4 = new DataSet("currentApprover",currentApproveUser);
////        DataSet dataSet3 = new DataSet("approveUserList",approveUserList);
////        DataSet dataSet5 = new DataSet("currentIndex",0);
////        dataSets.add(dataSet3);
//        dataSets.add(dataSet4);







//        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
        return AppResponse.buildResponse(PageAction.执行处理器("success",currentApproveUser));













    }



}
