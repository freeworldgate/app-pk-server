package com.union.app.api.pk.审核;

import com.union.app.domain.pk.Post;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class 查询审核员已审核列表 {


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

    @RequestMapping(path="/queryApprovedPost",method = RequestMethod.GET)
    public AppResponse 查询审核信息(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        Date currentDate = new Date();




        List<DataSet> dataSets = new ArrayList<>();

        List<Post> posts = dynamicService.查询已审核指定范围的Post(userId,pkId,0);
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

    @RequestMapping(path="/queryMoreApprovedPost",method = RequestMethod.GET)
    public AppResponse 查询审核信息More(@RequestParam("pkId") String pkId,@RequestParam("currentApprovedPage") int page,@RequestParam("userId") String userId) throws AppException, IOException {


        List<Post> posts = dynamicService.查询已审核指定范围的Post(userId,pkId,page);
        if(CollectionUtils.isEmpty(posts)){
            return AppResponse.buildResponse(PageAction.前端数据更新("approvedEnd",true));
        }


        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));
    }



}
