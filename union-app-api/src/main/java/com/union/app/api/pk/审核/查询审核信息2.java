package com.union.app.api.pk.审核;

import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询审核信息2 {


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

    /**
     * 审核员审批页面入口
     * @param pkId
     * @param postId
     * @param userId
     * @return
     * @throws AppException
     * @throws IOException
     */
    @RequestMapping(path="/queryApproveInfo2",method = RequestMethod.GET)
    public AppResponse 查询审核信息(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {


        Date currentDay = new Date();

        List<DataSet> dataSets = new ArrayList<>();
        String approveUserId = dynamicService.查询审核用户(pkId,postId,currentDay);
        if(!dynamicService.用户是否为今日审核员(pkId,approveUserId,currentDay)){

            return AppResponse.buildResponse(PageAction.前端数据更新("outOfTime",true));
        }




        ApproveUser approveUser = approveService.查询帖子的审核用户(pkId,postId,currentDay);
        Post post = postService.查询帖子(pkId,postId,null,currentDay);
        DataSet dataSet3 = new DataSet("userPost",post);
        DataSet dataSet4 = new DataSet("approveUserId",approveUserId);
        DataSet dataSet5 = new DataSet("approveUser",approveUser);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);
        dataSets.add(dataSet5);





        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }



}
