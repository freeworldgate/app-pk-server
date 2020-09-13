package com.union.app.api.pk.审核;

import com.union.app.domain.pk.ApproveButton;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
    ApproveService approveService;
    @Autowired
    AppService appService;
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


        List<DataSet> dataSets = new ArrayList<>();


        Post post = postService.查询帖子(pkId,postId,null);
        ApproveComment pkComment = approveService.获取留言信息(pkId, postId);
        ApproveButton approveButton = approveService.获取审核按钮(pkId,postId,userId);
        dataSets.add(new DataSet("t1","审核图贴"));
        dataSets.add(new DataSet("button",approveButton));
        dataSets.add(new DataSet("userPost",post));
        dataSets.add(new DataSet("pkComment",pkComment));
        dataSets.add(new DataSet("creator",pkService.queryPkCreator(pkId)));
        dataSets.add(new DataSet("date",TimeUtils.currentDate()));
        dataSets.add(new DataSet("pkId",pkId));
        dataSets.add(new DataSet("imgBack",appService.查询背景(4)));
        dataSets.add(new DataSet("t1","转发审核群"));
        dataSets.add(new DataSet("t2","审核留言"));
        dataSets.add(new DataSet("t3","留言"));
        dataSets.add(new DataSet("t4","编辑留言"));
        dataSets.add(new DataSet("t5","审核图贴"));

        if(approveButton == ApproveButton.转发审核群){
            dataSets.add(new DataSet("tip1","如群组已满，请选择"));
            dataSets.add(new DataSet("tip2", org.apache.commons.lang.StringUtils.isBlank(dynamicService.查询审核用户(pkId,postId))?"在线审核":"审核中"));
            dataSets.add(new DataSet("t1","转发审核群"));
        }



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }



}
