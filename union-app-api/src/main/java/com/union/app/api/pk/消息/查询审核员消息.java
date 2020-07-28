package com.union.app.api.pk.消息;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.complain.Complain;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PkStatu;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
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
public class 查询审核员消息 {


    @Autowired
    ComplainService complainService;



    @Autowired
    ApproveService approveService;


    @Autowired
    UserService userService;

    @Autowired
    PkService pkService;


    @RequestMapping(path="/queryApproveMessage",method = RequestMethod.GET)
    public AppResponse 查询审核员消息(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {

        Date currentDate = new Date();

        ApproveMessage approveMessage = approveService.获取审核人员消息(pkId);

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("message",approveMessage));
        dataSets.add(new DataSet("user",userService.queryUser(userId)));
        dataSets.add(new DataSet("creator",pkService.queryPkCreator(pkId)));
        dataSets.add(new DataSet("pkId",pkId));
        dataSets.add(new DataSet("date","审核公告"));

        boolean mode = AppConfigService.getConfigAsBoolean(ConfigItem.系统当前是否客服模式);
        dataSets.add(new DataSet("mode",mode));
        if(mode)
        {
            dataSets.add(new DataSet("buttonStr","获取图片"));
        }
        else
        {
            dataSets.add(new DataSet("buttonStr","保存图片到相册"));
        }
        dataSets.add(new DataSet("word1","编辑公告"));
        dataSets.add(new DataSet("word2","审核公告"));

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
    }

    @RequestMapping(path="/canEditApproveMessage",method = RequestMethod.GET)
    public AppResponse 查询审核员消息(@RequestParam("pkId") String pkId) throws AppException, IOException {

        PkEntity pk = pkService.querySinglePkEntity(pkId);
        if(pk.getAlbumStatu() == PkStatu.已审核)
        {
            return AppResponse.buildResponse(PageAction.信息反馈框("提示","相册已发布,不能审核"));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.执行处理器("success",""));
        }




    }















}
