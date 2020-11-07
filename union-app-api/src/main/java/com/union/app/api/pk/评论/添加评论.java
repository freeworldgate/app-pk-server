package com.union.app.api.pk.评论;

import com.union.app.domain.pk.Comment;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class 添加评论 {



    @Autowired
    CommentService commentService;


    @RequestMapping(path="/addComment",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询评论系统(@RequestParam("userId") String userId,@RequestParam("id") String id,@RequestParam("text") String text,@RequestParam("type") int type) throws AppException, IOException {


        Comment comment = commentService.添加评论(id,type,userId,text);



        return AppResponse.buildResponse(PageAction.执行处理器("success",comment));

    }



}
