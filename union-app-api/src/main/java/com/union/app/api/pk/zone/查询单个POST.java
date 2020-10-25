package com.union.app.api.pk.zone;

import com.union.app.domain.pk.*;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class 查询单个POST {


    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    OssStorage ossStorage;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;


    @Autowired
    AppService appService;

    @Autowired
    DynamicService dynamicService;


    @RequestMapping(path="/queryPostById",method = RequestMethod.GET)
    public AppResponse 查询单个POSTById(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException {

        Date currentDate = new Date();
        Post post = postService.查询帖子(pkId,postId,userId);

        User creator = pkService.queryPkCreator(pkId);

        List<DataSet> dataSets = new ArrayList<>();
        DataSet dataSet1 = new DataSet("post",post);
        DataSet dataSet2 = new DataSet("creator",creator);

        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(new DataSet("t1","修改标题"));
        dataSets.add(new DataSet("t2","留言"));
        dataSets.add(new DataSet("t3","审核"));
        dataSets.add(new DataSet("t4","检测到当前图册处于未审核状态，请前往审核页面,榜主通过审核后方可发布该图册内容..."));
        dataSets.add(new DataSet("t5","修改主题"));
        dataSets.add(new DataSet("t6","修改主题内容"));
        dataSets.add(new DataSet("t7","修改图片需要重新审核图册内容..."));
        dataSets.add(new DataSet("t3","审核"));
        dataSets.add(new DataSet("t3","审核"));
        dataSets.add(new DataSet("imgBack",appService.查询背景(4)));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
    }

    @RequestMapping(path="/queryPostByPostId",method = RequestMethod.GET)
    public AppResponse 查询单个queryPostByPostId(@RequestParam("postId") String postId) throws AppException, IOException {
        PostEntity postEntity = postService.查询帖子ById(postId);
        Post post = postService.translate(postEntity);

        if(post.getStatu().getKey() != PostStatu.上线.getStatu())
        {
            return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/pk/pk?pkId="+postEntity.getPkId()+"&fromUser="+post.getCreator().getUserId(),false));
        }
        dynamicService.扫描次数加1(postEntity.getPkId(),postId);


        post.setImgBack(appService.查询背景(4));





        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
    }










    @RequestMapping(path="/importPost",method = RequestMethod.GET)
    public AppResponse 查询单个POSTById(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {

        PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
        String imgBack = appService.查询背景(4);
        List<PkCashier> tips = appService.查询有效收款列表();







        return AppResponse.buildResponse(PageAction.执行处理器("success",new ImportPost(pkEntity.getTopic(),tips,imgBack)));
    }









}
