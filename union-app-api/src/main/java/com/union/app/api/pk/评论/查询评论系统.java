package com.union.app.api.pk.评论;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Comment;
import com.union.app.domain.pk.PkButton;
import com.union.app.domain.pk.PkButtonType;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.审核.PkComment;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询评论系统 {


    @Autowired
    AppService appService;

    @Autowired
    CommentService commentService;
    @Autowired
    DynamicService dynamicService;

    @RequestMapping(path="/queryComments",method = RequestMethod.GET)
    public AppResponse 查询评论系统(@RequestParam("userId") String userId,@RequestParam("id") String id,@RequestParam("type") int type) throws AppException, IOException {


        List<Comment> comments = commentService.查询评论(id,type,userId,1);






        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("feetImg",appService.查询背景(11)));
        dataSets.add(new DataSet("nums",dynamicService.getKeyValue(CacheKeyName.评论,id)));

        if(CollectionUtils.isEmpty(comments))
        {
            dataSets.add(new DataSet("pkEnd",true));}
        else
        {

            dataSets.add(new DataSet("comments",comments));
            dataSets.add(new DataSet("pkEnd",false));
        }
;
        dataSets.add(new DataSet("page",1));









        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextCommentPage",method = RequestMethod.GET)
    public AppResponse 查询评论系统(@RequestParam("userId") String userId,@RequestParam("id") String id,@RequestParam("type") int type,@RequestParam("page") int page) throws AppException, IOException {

        List<Comment> comments = commentService.查询评论(id,type,userId,page+1);

        if(comments.size() == 0)
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("pkEnd",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",comments));

    }

}
