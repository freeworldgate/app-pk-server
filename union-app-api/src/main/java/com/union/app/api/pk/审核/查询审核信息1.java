package com.union.app.api.pk.审核;

import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询审核信息1 {


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

    /** 用户查询所有审核人
     * @param pkId
     * @param postId
     * @param userId
     * @return
     * @throws AppException
     * @throws IOException
     */
    @RequestMapping(path="/queryApproveInfo1",method = RequestMethod.GET)
    public AppResponse 查询审核信息(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("userId") String userId) throws AppException, IOException, ParseException {


        Date currentDate = new Date();


        List<DataSet> dataSets = new ArrayList<>();
        PostEntity postEntity = postService.查询帖子ById(postId);
//        String approveUserId = dynamicService.查询审核用户(pkId,postId);

        if(!org.apache.commons.lang.StringUtils.equals(userId,postEntity.getUserId())){
            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }


        List<ApproveUser> approveUserList = approveService.查询当前可用户审核用户列表(pkId,postId);

        DataSet dataSet3 = new DataSet("approveUserList",approveUserList);
        dataSets.add(dataSet3);
        ApproveUser currentApprover = null;
        for(ApproveUser approveUser:approveUserList){
                if(org.apache.commons.lang.StringUtils.equals(approveUser.getUser().getUserId(),"")){
                    currentApprover = approveUser;
                    break;
                }
        }
        if(!ObjectUtils.isEmpty(currentApprover)) {
                DataSet dataSet4 = new DataSet("currentSelectApprover",currentApprover.getUser());
                int index = approveUserList.indexOf(currentApprover);
                DataSet dataSet5 = new DataSet("currentIndex",index);
                DataSet dataSet6 = new DataSet("currentApprover",currentApprover);
                dataSets.add(dataSet5);
                dataSets.add(dataSet4);
                dataSets.add(dataSet6);
        }
        else
        {
            DataSet dataSet4 = new DataSet("currentApprover",CollectionUtils.isEmpty(approveUserList)?null:approveUserList.get(0));
            DataSet dataSet5 = new DataSet("currentIndex",0);
            dataSets.add(dataSet5);
            dataSets.add(dataSet4);

        }
        //顺序不能颠倒
        User creator = pkService.queryPkCreator(pkId);


        String mediaId = dynamicService.查询PK群组二维码MediaId(pkId);
        DataSet dataSet8 = new DataSet("creator",creator);
        DataSet dataSet7 = new DataSet("mediaId",mediaId);
        DataSet dataSet9 = new DataSet("pkId",pkId);
//        DataSet dataSet10 = new DataSet("date",TimeUtils.dateStr(currentDate));
        DataSet dataSet11 = new DataSet("timeout",dynamicService.计算今日剩余时间(pkId));

        dataSets.add(dataSet7);
        dataSets.add(dataSet8);
        dataSets.add(dataSet9);
//        dataSets.add(dataSet10);
        dataSets.add(dataSet11);









        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }



}
