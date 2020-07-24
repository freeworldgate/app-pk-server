package com.union.app.api.pk.审核;

import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveUser;
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
public class 查询审核员审核中列表 {


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

    @RequestMapping(path="/queryApprovingPost",method = RequestMethod.GET)
    public AppResponse 查询审核员审核中列表(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        Date date = new Date();
        List<DataSet> dataSets = new ArrayList<>();

        List<Post> posts = dynamicService.查询审核中指定范围的Post(pkId,userId,0);



        DataSet dataSet2 = new DataSet("approvedPosts",posts);
        DataSet dataSet4 = new DataSet("currentApprovedPage",1);
        DataSet dataSet8 = new DataSet("pkId",pkId);
        DataSet dataSet9 = new DataSet("creator",pkService.queryPkCreator(pkId));

        dataSets.add(dataSet2);
        dataSets.add(dataSet4);
        dataSets.add(dataSet8);
        dataSets.add(dataSet9);

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));





    }
    @RequestMapping(path="/queryMoreApprovingPost",method = RequestMethod.GET)
    public AppResponse 查询审核信息More(@RequestParam("pkId") String pkId,@RequestParam("currentApprovedPage") int page,@RequestParam("userId") String userId) throws AppException, IOException {
        Date date = new Date();
        List<Post> posts = dynamicService.查询审核中指定范围的Post(pkId, userId, page);

        if(CollectionUtils.isEmpty(posts)){
            return AppResponse.buildResponse(PageAction.前端数据更新("approvedEnd",true));
        }


        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));
    }



}
