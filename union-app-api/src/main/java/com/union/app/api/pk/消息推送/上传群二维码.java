package com.union.app.api.pk.消息推送;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.pk.客服消息.WxSendMessage;
import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.user.UserService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/pk")
public class 上传群二维码 {


    @Autowired
    UserService userService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    PkService pkService;
    /**
     * 接收微信后台发来的用户消息
     * @return
     */
    @RequestMapping(value = "/uploadGroupCode", method = RequestMethod.GET)
    public AppResponse 上传群二维码(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("url") String url) throws IOException {


        Date currentDate = new Date();

        //用户openId
        User creator = pkService.queryPkCreator(pkId);

        if(StringUtils.equals(creator.getUserId(),userId)) {
            String oldMediaId = dynamicService.查询PK群组二维码MediaId(pkId,currentDate);
            if(!StringUtils.isBlank(oldMediaId)){
                return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"今日已更新"));
            }



            String mediaId = WeChatUtil.uploadImg2Wx(url);
            dynamicService.设置PK群组二维码MediaId(pkId,mediaId,currentDate);
            dynamicService.设置PK群组二维码Url(pkId,url,currentDate);


            List<DataSet> dataSets = new ArrayList<>();
            DataSet dataSet1 = new DataSet("creator",creator);
            DataSet dataSet2 = new DataSet("groupCode",url);
            DataSet dataSet3 = new DataSet("mediaId",mediaId);
            dataSets.add(dataSet1);
            dataSets.add(dataSet2);
            dataSets.add(dataSet3);


            return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
        }
        else
            {
                return AppResponse.buildResponse(PageAction.消息级别提示框(Level.错误消息,"非法操作"));
            }

    }


    @RequestMapping(value = "/queryGroupCode", method = RequestMethod.GET)
    public AppResponse 查询群二维码(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws IOException {
        Date currentDate = new Date();
        //用户openId
        User creator = pkService.queryPkCreator(pkId);
        String url = dynamicService.查询PK群组二维码Url(pkId,currentDate);
        String mediaId = dynamicService.查询PK群组二维码MediaId(pkId,currentDate);
        List<DataSet> dataSets = new ArrayList<>();
        DataSet dataSet1 = new DataSet("creator",creator);
        DataSet dataSet2 = new DataSet("groupCode",url);
        DataSet dataSet3 = new DataSet("mediaId",mediaId);
        DataSet dataSet4 = new DataSet("date",TimeUtils.currentDate());
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);


        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));




    }


}
