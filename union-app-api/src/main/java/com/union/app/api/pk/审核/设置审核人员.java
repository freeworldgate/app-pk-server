package com.union.app.api.pk.审核;

import com.union.app.domain.pk.ApproveButton;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 设置审核人员 {


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

    @RequestMapping(path="/setApprover",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置审核人员(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {
        PostEntity postEntity = postService.查询帖子ById(postId);
        if(!StringUtils.equals(userId,postEntity.getUserId())){return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));}



        postService.用户转发审批(postEntity);
        dynamicService.设置帖子的审核用户(pkId,postEntity);


        List<DataSet> dataSets = new ArrayList<>();
        ApproveButton approveButton = approveService.获取审核按钮(pkId,postId,userId);
        dataSets.add(new DataSet("button",approveButton));

//        if(approveButton == ApproveButton.转发审核群){
//            dataSets.add(new DataSet("tip1","如群组已满，请选择"));
//            dataSets.add(new DataSet("tip2", org.apache.commons.lang.StringUtils.isBlank(dynamicService.查询审核用户(pkId,postId))?"在线审核":"审核中"));
//        }


        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }



}
