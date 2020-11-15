package com.union.app.api.pk.消息推送;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.pk.PkActive;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
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

            String mediaId = WeChatUtil.uploadImg2Wx(url);

            dynamicService.设置PK群组二维码MediaId(pkId, mediaId);
            dynamicService.设置PK群组二维码Url(pkId, url);
            pkService.更新群组时间(pkId);

            dataSet2 = new DataSet("imgUrl",url);
            dataSet3 = new DataSet("mediaId",mediaId);
            dataSet4 = new DataSet("title",  "主题群");

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
    public AppResponse 查询群二维码(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws IOException, AppException {




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


        dataSets.add(new DataSet("buttonStr1","获取图片"));
        dataSets.add(new DataSet("buttonStr2","保存图片到相册"));





            //查询打榜群。
            String url ="";
            String mediaId ="";
            PkEntity pkEntity = pkService.querySinglePkEntity(pkId);
            url = dynamicService.查询PK群组二维码Url(pkId);
            mediaId = dynamicService.查询PK群组二维码MediaId(pkId);


//            if(pkEntity.getPkType() == PkType.内置相册 && pkEntity.getIsInvite() == InviteType.公开)
//            {
//                url = dynamicService.查询内置公开PK群组二维码Url(pkId);
//                mediaId = dynamicService.查询内置公开PK群组二维码MediaId(pkId);
//            }
//            else
//            {
//                 url = dynamicService.查询PK群组二维码Url(pkId);
//                 mediaId = dynamicService.查询PK群组二维码MediaId(pkId);
//            }



            dataSet2 = new DataSet("imgUrl",url);
            dataSet3 = new DataSet("mediaId",mediaId);
            dataSet4 = new DataSet("title","主题群");

            if(StringUtils.equals(userId,creator.getUserId()))
            {
                dataSet5 = new DataSet("mode", "upload");
                if(StringUtils.isBlank(url)){
                    dataSet6 = new DataSet("t1", "上传群名片");
                    if(userService.是否是遗传用户(userId)) {
                        dataSet7 = new DataSet("t2", "群成员满200，及时更换群名片...");
                    }
                }

            }
            else
            {
                if(StringUtils.isBlank(url))
                {

                    dataSet5 = new DataSet("mode", "");
                    dataSet6 = new DataSet("t1", "未更新主题群");
                    dataSet7 = new DataSet("t2", "等待更新主题群,稍后再查看...");





                }



            }






        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);
        dataSets.add(dataSet5);
        dataSets.add(dataSet6);
        dataSets.add(dataSet7);
        boolean msgChat = AppConfigService.getConfigAsBoolean(ConfigItem.是否允许客服会话);
        dataSets.add(new DataSet("session", msgChat));

        dataSets.add(new DataSet("upload","上传"));
        DataSet dataSet8 = new DataSet("word3", msgChat?"客服消息回复1获取图片":"");
        dataSets.add(dataSet8);

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
