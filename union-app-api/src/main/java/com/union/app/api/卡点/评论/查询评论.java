package com.union.app.api.卡点.评论;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.PostStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.LikeService;
import com.union.app.service.pk.service.PostService;
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
public class 查询评论 {

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;
    @Autowired
    PostService postService;

    @RequestMapping(path="/queryComments",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询单个PK(@RequestParam("postId") String postId,@RequestParam("userId") String userId) {

        //统计PK请求此次数。
        PostEntity postEntity = postService.查询帖子ById(postId);
        if(ObjectUtils.isEmpty(postEntity))
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("打卡记录已删除","打卡记录已删除!"));
        }
        if(postEntity.getStatu() == PostStatu.隐藏)
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("打卡记录已隐藏","打卡记录已隐藏!"));
        }
        Post post = postService.translate(postEntity);
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        likeService.查询Post点赞记录(posts,userId);
        List<Comment> comments = commentService.查询评论(postId,1);
        likeService.查询评论点赞记录(comments,userId);
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("posts",posts));
        dataSets.add(new DataSet("comments",comments));
        dataSets.add(new DataSet("commentNums",post.getComments()));
        dataSets.add(new DataSet("borderRadius", AppConfigService.getConfigAsInteger(ConfigItem.用户头像BorderRadius)));
        dataSets.add(new DataSet("postBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.小图片圆角)));
        dataSets.add(new DataSet("post1BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post2或4张图圆角)));
        dataSets.add(new DataSet("videoBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.视频圆角)));
        dataSets.add(new DataSet("post2BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post1张图圆角)));
        dataSets.add(new DataSet("post3BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.文字背景圆角)));
        dataSets.add(new DataSet("videoBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.文字背景圆角)));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(path="/nextComments",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("postId") String postId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<Comment> comments = commentService.查询评论(postId,page+1);

        if(CollectionUtils.isEmpty(comments))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }
        likeService.查询评论点赞记录(comments,userId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",comments));

    }

}
