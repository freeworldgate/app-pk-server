package com.union.app.api.卡点.评论;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.entity.user.UserDynamicEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.LikeService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.pk.service.评论.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
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
public class 查询用户评论 {

    @Autowired
    UserDynamicService userDynamicService;


    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;

    @RequestMapping(path="/queryUserComments",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询用户评论(@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {

        //统计PK请求此次数。
        UserDynamicEntity userDynamicEntity = userDynamicService.queryUserDynamicEntity(userId);
        List<Comment> comments = commentService.查询用户评论(userId,1);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("comments",comments));
        dataSets.add(new DataSet("commentNums",userDynamicEntity.getComments()));
        dataSets.add(new DataSet("borderRadius", AppConfigService.getConfigAsInteger(ConfigItem.用户头像BorderRadius)));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(path="/nextUserComments",method = RequestMethod.GET)
    public AppResponse 查询用户评论下一页(@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<Comment> comments = commentService.查询用户评论(userId,page+1);

        if(CollectionUtils.isEmpty(comments))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }
        return AppResponse.buildResponse(PageAction.执行处理器("success",comments));

    }

}
