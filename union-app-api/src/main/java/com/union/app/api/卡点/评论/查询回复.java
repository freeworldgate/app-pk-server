package com.union.app.api.卡点.评论;

import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.comment.Comment;
import com.union.app.domain.pk.comment.Restore;
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
public class 查询回复 {


    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;
    @Autowired
    PostService postService;

    @RequestMapping(path="/queryRestores",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询回复(@RequestParam("commentId") String commentId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {
        List<DataSet> dataSets = new ArrayList<>();
        //统计PK请求此次数。
        Comment comment = commentService.查询评论ById(commentId);
        List<Restore> restores = commentService.查询回复(commentId,1);
        likeService.查询回复点赞记录(restores,userId);
        if(!ObjectUtils.isEmpty(comment))
        {
            List<Comment> comments = new ArrayList<>();
            comments.add(comment);
            likeService.查询评论点赞记录(comments,userId);
            dataSets.add(new DataSet("comment",comment));
        }
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("restores",restores));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(path="/nextRestores",method = RequestMethod.GET)
    public AppResponse 下一页回复(@RequestParam("commentId") String commentId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<Restore> restores = commentService.查询回复(commentId,page+1);

        if(CollectionUtils.isEmpty(restores))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }
        likeService.查询回复点赞记录(restores,userId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",restores));

    }

}
