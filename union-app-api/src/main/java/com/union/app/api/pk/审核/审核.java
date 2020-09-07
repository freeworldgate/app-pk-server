package com.union.app.api.pk.审核;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.ApproveStatu;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
public class 审核 {


    @Autowired
    AppDaoService daoService;


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

    @RequestMapping(path="/approve",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 审核(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {


        List<DataSet> dataSets = new ArrayList<>();


        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非榜主用户"));
        }

        postService.上线帖子(pkId,postId);
        dynamicService.已审核(pkId,postId);

        Post post = postService.查询帖子(pkId,postId,null);
        ApproveComment pkComment = approveService.获取留言信息(pkId, postId);

        dataSets.add(new DataSet("userPost",post));
        dataSets.add(new DataSet("pkComment",pkComment));
        dataSets.add(new DataSet("creator",pkService.queryPkCreator(pkId)));

        dataSets.add(new DataSet("pkId",pkId));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
    }

    @RequestMapping(path="/doApprove",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 审核榜帖(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {



        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非榜主用户"));
        }

        postService.上线帖子(pkId,postId);
        dynamicService.已审核(pkId,postId);

        List<DataSet> dataSets = new ArrayList<>();
        Post post = dynamicService.查询审核中指定范围的Post(pkId);
        DataSet dataSet2 = new DataSet("post",post);
        DataSet dataSet8 = new DataSet("pkId",pkId);


        dataSets.add(dataSet2);
        dataSets.add(dataSet8);

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/rejectApprove",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 驳回修改(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId,@RequestParam("text") String text) throws AppException, IOException {



        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非榜主用户"));
        }

        PostEntity postEntity = postService.查询帖子ById(pkId,postId);
        postEntity.setApproveStatu(ApproveStatu.驳回修改);
        postEntity.setRejectTextBytes(text);
        postEntity.setRejectTimes(postEntity.getRejectTimes() + 1);
        daoService.updateEntity(postEntity);
        dynamicService.驳回用户审核(pkId,postId);







        List<DataSet> dataSets = new ArrayList<>();
        Post post = dynamicService.查询审核中指定范围的Post(pkId);
        DataSet dataSet2 = new DataSet("post",post);
        DataSet dataSet8 = new DataSet("pkId",pkId);


        dataSets.add(dataSet2);
        dataSets.add(dataSet8);



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }







    @RequestMapping(path="/rejectApprovingPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 驳回修改Post(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId,@RequestParam("text") String text) throws AppException, IOException {



        if(!pkService.isPkCreator(pkId,userId))
        {
            throw AppException.buildException(PageAction.消息级别提示框(Level.错误消息,"非榜主用户"));
        }

        PostEntity postEntity = postService.查询帖子ById(pkId,postId);
        postEntity.setApproveStatu(ApproveStatu.驳回修改);
        postEntity.setRejectTextBytes(text);
        postEntity.setRejectTimes(postEntity.getRejectTimes() + 1);
        daoService.updateEntity(postEntity);

        dynamicService.驳回用户审核(pkId,postId);

        return AppResponse.buildResponse(PageAction.前端数据更新("tt1","已驳回"));

    }





}
