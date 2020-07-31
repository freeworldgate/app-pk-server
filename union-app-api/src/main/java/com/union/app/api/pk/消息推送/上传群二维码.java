package com.union.app.api.pk.消息推送;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.pk.PkActive;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkActiveEntity;
import com.union.app.entity.pk.PkCashierEntity;
import com.union.app.entity.pk.PkCashierFeeCodeEntity;
import com.union.app.entity.pk.PkCashierGroupEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.*;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
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
public class 上传群二维码 {


    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PkService pkService;

    @Autowired
    AppService appService;
    /**
     * 接收微信后台发来的用户消息
     * @return
     */
    @RequestMapping(value = "/uploadGroupCode", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 上传群二维码(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("url") String url,@RequestParam("type") int type) throws IOException, AppException {

        List<DataSet> dataSets = new ArrayList<>();
        Date currentDate = new Date();
        User creator = pkService.queryPkCreator(pkId);
        DataSet dataSet1 = new DataSet("creator",creator);
        DataSet dataSet5 = new DataSet("mode", "show");

        DataSet dataSet2 = new DataSet("imgUrl","");
        DataSet dataSet3 = new DataSet("mediaId","");
        DataSet dataSet4 = new DataSet("title","");
        DataSet dataSet6 = new DataSet("t1","");
        DataSet dataSet7 = new DataSet("t2","");


        //无权限
        if(!StringUtils.equals(creator.getUserId(),userId)) {
            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
        }







        if(type == 1)
        {

                String oldMediaId = dynamicService.查询PK群组二维码MediaId(pkId,currentDate);
                if(!StringUtils.isBlank(oldMediaId)){ return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"今日已更新")); }
                String mediaId = WeChatUtil.uploadImg2Wx(url);
                dynamicService.设置PK群组二维码MediaId(pkId,mediaId,currentDate);
                dynamicService.设置PK群组二维码Url(pkId,url,currentDate);


                dataSet2 = new DataSet("imgUrl",url);
                dataSet3 = new DataSet("mediaId",mediaId);
                dataSet4 = new DataSet("title",TimeUtils.currentDate() + "日审核群");

        }
        else if(type == 4)
        {
            appService.上传打赏截图(pkId,userId,url);

            PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
            dataSet4 = new DataSet("title", "打赏截图");
            if(ObjectUtils.isEmpty(pkActiveEntity) || StringUtils.isBlank(pkActiveEntity.getScreenCutUrl()))
            {
                dataSet5 = new DataSet("mode", "upload");
                dataSet6 = new DataSet("t1", "未上传");
                dataSet7 = new DataSet("t2", "上传打赏截图，只有一次机会...");
            }
            else
            {
                dataSet2 = new DataSet("imgUrl",pkActiveEntity.getScreenCutUrl());
                dataSet3 = new DataSet("mediaId",pkActiveEntity.getScreenCutMediaId());
                dataSet4 = new DataSet("mode", "show");
            }
        }
        else
        {




        }
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);
        dataSets.add(dataSet5);
        dataSets.add(dataSet6);
        dataSets.add(dataSet7);

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }


    @RequestMapping(value = "/queryGroupCode", method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询群二维码(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("type") int type) throws IOException, AppException {




        List<DataSet> dataSets = new ArrayList<>();
        Date currentDate = new Date();
        User creator = pkService.queryPkCreator(pkId);
        DataSet dataSet1 = new DataSet("creator",creator);
        DataSet dataSet5 = new DataSet("mode", "show");
        DataSet dataSet2 = new DataSet("imgUrl","");
        DataSet dataSet3 = new DataSet("mediaId","");
        DataSet dataSet4 = new DataSet("title","");
        DataSet dataSet6 = new DataSet("t1","");
        DataSet dataSet7 = new DataSet("t2","");


        boolean download = AppConfigService.getConfigAsBoolean(ConfigItem.系统当前是否客服模式);
        dataSets.add(new DataSet("download",download));
        if(download)
        {
            dataSets.add(new DataSet("buttonStr","获取图片"));
        }
        else
        {
            dataSets.add(new DataSet("buttonStr","保存图片到相册"));
        }




        if(type == 1)
        {
            //查询打榜群。

            String url = dynamicService.查询PK群组二维码Url(pkId,currentDate);
            String mediaId = dynamicService.查询PK群组二维码MediaId(pkId,currentDate);
            if(StringUtils.isBlank(url))
            {

                dataSet5 = new DataSet("mode", "");
                dataSet6 = new DataSet("t1", "空空如也");
                dataSet7 = new DataSet("t2", "等待用户更新今日审核群...");

                if(StringUtils.equals(userId,creator.getUserId()))
                {
                    dataSet5 = new DataSet("mode", "upload");
                    dataSet6 = new DataSet("t1", "上传群名片");
                    dataSet7 = new DataSet("t2", "每日只有一次上传机会，请使用微信群组名片...");

                }



            }
            else
            {
                dataSet2 = new DataSet("imgUrl",url);
                dataSet3 = new DataSet("mediaId",mediaId);
                dataSet4 = new DataSet("title",TimeUtils.currentDate() + "日审核群");
            }


        }
        else if(type == 2)
        {
            //查询激活群。

            PkCashierGroupEntity pkCashierGroupEntity = appService.查询可用群组(pkId,userId);
            dataSet2 = new DataSet("imgUrl",pkCashierGroupEntity.getGroupUrl());
            dataSet3 = new DataSet("mediaId",pkCashierGroupEntity.getGroupMediaId());
            dataSet4 = new DataSet("title",TimeUtils.currentDate() + "日激活群");

        }
        else if(type == 3)
        {
            //查询平台收款码。
            PkCashierFeeCodeEntity pkCashierFeeImgEntity = appService.查询可用收款码(pkId,userId);

            dataSet2 = new DataSet("imgUrl",pkCashierFeeImgEntity.getFeeCodeUrl());
            dataSet3 = new DataSet("mediaId",pkCashierFeeImgEntity.getFeeCodeMediaId());
            dataSet4 = new DataSet("title", "打赏码");

        }
        else if(type == 4)
        {
            //查询打赏截图。

            PkActiveEntity pkActiveEntity = appService.查询PK激活信息(pkId);
            dataSet4 = new DataSet("title", "打赏截图");
            if(ObjectUtils.isEmpty(pkActiveEntity) || StringUtils.isBlank(pkActiveEntity.getScreenCutUrl()))
            {
                dataSet5 = new DataSet("mode", "upload");
                dataSet6 = new DataSet("t1", "未上传");
                dataSet7 = new DataSet("t2", "上传打赏截图，只有一次机会...");
            }
            else
            {
                dataSet2 = new DataSet("imgUrl",pkActiveEntity.getScreenCutUrl());
                dataSet3 = new DataSet("mediaId",pkActiveEntity.getScreenCutMediaId());
            }


        }
        else
        {

        }




        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);
        dataSets.add(dataSet5);
        dataSets.add(dataSet6);
        dataSets.add(dataSet7);
        dataSets.add(new DataSet("upload","上传"));


        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));




    }


//    @RequestMapping(value = "/viewActiveGroupCode", method = RequestMethod.GET)
//    public AppResponse 查询激活群(@RequestParam("cashierId") String cashierId,@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws IOException {
//
//        String selectCashierId = appService.已选CashierId(pkId);
//        if(StringUtils.isBlank(selectCashierId))
//        {
//            return AppResponse.buildResponse(PageAction.执行处理器("selectCashier","选择激活群，不能修改"));
//        }
//        if(!StringUtils.equals(selectCashierId,cashierId))
//        {
//
//            return AppResponse.buildResponse(PageAction.信息反馈框("提示","没有权限"));
//        }
//        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/message/message?cashierId=" + cashierId + "&pkId=" + pkId,true));
//
//    }
//    @RequestMapping(value = "/confirmSelectCashier", method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 确认激活用户(@RequestParam("cashierId") String cashierId,@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws IOException {
//
//        appService.设置激活用户(pkId,cashierId);
//
//        return AppResponse.buildResponse(PageAction.页面跳转("/pages/pk/message/message?cashierId=" + cashierId + "&pkId=" + pkId,true));
//
//
//    }
//
//
//    @RequestMapping(value = "/queryActiveGroupCode", method = RequestMethod.GET)
//    public AppResponse 查询激活群二维码(@RequestParam("cashierId") String cashierId,@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws IOException {
//
//        Date currentDate = new Date();
//        //用户openId
//        PkCashierEntity pkCashierEntity = appService.查询Cashier(cashierId);
//
//
//        List<DataSet> dataSets = new ArrayList<>();
//
//        DataSet dataSet2 = new DataSet("groupCode",pkCashierEntity.getImgUrl());
//        DataSet dataSet3 = new DataSet("mediaId",pkCashierEntity.getMediaId());
//        DataSet dataSet4 = new DataSet("date", TimeUtils.currentDate() + "日激活群");
//        dataSets.add(dataSet2);
//        dataSets.add(dataSet3);
//        dataSets.add(dataSet4);
//
//
//        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
//
//
//    }
}
