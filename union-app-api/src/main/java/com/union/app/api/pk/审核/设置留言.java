package com.union.app.api.pk.审核;

import com.union.app.domain.pk.审核.ApproveUser;
import com.union.app.entity.pk.PostEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class 设置留言 {


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

    @RequestMapping(path="/setComment",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 设置留言(@RequestParam("pkId") String pkId, @RequestParam("approvorId") String approvorId,  @RequestParam("userId") String userId,@RequestParam("text") String text,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException {

        List<DataSet> dataSets = new ArrayList<>();
        PostEntity postEntity = postService.查询用户帖(pkId,userId);


        approveService.设置审核留言(pkId,postEntity.getPostId(),approvorId,text,imgUrl);

        List<ApproveUser> newApproveUserList = new ArrayList<>();

        List<ApproveUser> approveUserList = approveService.查询今日所有审核用户(pkId,postEntity.getPostId());

        ApproveUser currentApproveUser = null;
        for(ApproveUser approveUser:approveUserList){
            if(org.apache.commons.lang.StringUtils.equals(approveUser.getUser().getUserId(),approvorId)){
                currentApproveUser = approveUser;
            }

        }

        DataSet dataSet4 = new DataSet("currentApprover",currentApproveUser);
        DataSet dataSet3 = new DataSet("approveUserList",approveUserList);
//        DataSet dataSet5 = new DataSet("currentIndex",0);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);







        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));













    }



}
