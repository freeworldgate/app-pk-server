package com.union.app.api.pk.审核;

import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.PostImage;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.pk.审核.ApproveComplain;
import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询审核信息1 {


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

    @RequestMapping(path="/queryApproveInfo1",method = RequestMethod.GET)
    public AppResponse 查询审核信息(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {


        List<DataSet> dataSets = new ArrayList<>();
        PostEntity postEntity = postService.查询帖子ById(pkId,postId);
        String approveUserId = dynamicService.查询审核用户(pkId,postId);

        if(!org.apache.commons.lang.StringUtils.equals(userId,postEntity.getUserId())){
            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }


        List<ApproveUser> approveUserList = approveService.查询今日所有审核用户(pkId,postId);
        DataSet dataSet3 = new DataSet("approveUserList",approveUserList);
        dataSets.add(dataSet3);
        ApproveUser currentApprover = null;
        for(ApproveUser approveUser:approveUserList){
                if(org.apache.commons.lang.StringUtils.equals(approveUser.getUser().getUserId(),approveUserId)){
                    currentApprover = approveUser;
                    break;
                }
        }
        if(!ObjectUtils.isEmpty(currentApprover)) {
                DataSet dataSet4 = new DataSet("currentApprover",currentApprover);
                int index = approveUserList.indexOf(currentApprover);
                DataSet dataSet5 = new DataSet("currentIndex",index);
                dataSets.add(dataSet5);
                dataSets.add(dataSet4);
        }
        else
        {
            DataSet dataSet4 = new DataSet("currentApprover",CollectionUtils.isEmpty(approveUserList)?null:approveUserList.get(0));
            DataSet dataSet5 = new DataSet("currentIndex",0);
            dataSets.add(dataSet5);
            dataSets.add(dataSet4);

        }
        //顺序不能颠倒













        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }



}
