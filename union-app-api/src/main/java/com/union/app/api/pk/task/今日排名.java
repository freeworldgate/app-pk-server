package com.union.app.api.pk.task;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.user.User;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@RestController
@RequestMapping(path="/pk")
public class 今日排名 {


    /**
     * hehe
     */
    @Autowired
    DynamicService dynamicService;


    @Autowired
    ApproveService approveService;

    @Autowired
    PkService pkService;

    @RequestMapping(path="/querySort",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 今日排名(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("index") int page) throws AppException, IOException, ParseException {

        List<DataSet> dataSets = new ArrayList<>();

        Date currentDate = new Date();
        UserIntegral userIntegralInfo = dynamicService.查询用户打榜信息(pkId,userId);

        User creator = pkService.queryPkCreator(pkId);
        List<UserIntegral> userIntegrals = dynamicService.queryUserIntegrals(pkId,page,currentDate);
        int approverNum = approveService.计算管理员设置人数(pkId,currentDate);
        int sortNum = approveService.今日打榜总人数(pkId,currentDate);
        if(dynamicService.是否开抢时间(pkId)) {
            List<UserIntegral> approvers = dynamicService.查询预备审核用户列表(pkId, currentDate);
            DataSet dataSet5 = new DataSet("approvers",approvers);
            dataSets.add(dataSet5);

        }
        //打榜剩余时间
        long leftTime = dynamicService.计算今日剩余时间(pkId);



        String approverIndexRange = approveService.计算预审核员排名边界(sortNum);




        DataSet dataSet1 = new DataSet("userIntegrals",userIntegrals);
        DataSet dataSet2 = new DataSet("index",page + 1);
        DataSet dataSet3 = new DataSet("approverNum",approverNum);
        DataSet dataSet4 = new DataSet("sortNum",sortNum);


        DataSet dataSet7 = new DataSet("creator",creator);
        DataSet dataSet8 = new DataSet("leftTime",leftTime);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);

        dataSets.add(dataSet7);
        dataSets.add(dataSet8);
        dataSets.add(new DataSet("date",TimeUtils.dateStr(currentDate)));
        dataSets.add(new DataSet("userIntegralInfo",userIntegralInfo));
        dataSets.add(new DataSet("approverIndexRange",approverIndexRange));
        dataSets.add(dataSet1);

        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));
    }

    @RequestMapping(path="/queryMoreSort",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 今日排名More(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("index") int index) throws AppException, IOException {


        Date currentDate = new Date();

        List<UserIntegral> userIntegrals = dynamicService.queryUserIntegrals(pkId,index,currentDate);
        if(CollectionUtils.isEmpty(userIntegrals)){
            return AppResponse.buildResponse(PageAction.前端数据更新("end", true));
        }
        else
        {
            return AppResponse.buildResponse(PageAction.执行处理器("success", userIntegrals));
        }

    }
    @RequestMapping(path="/updateTime",method = RequestMethod.GET)
    public AppResponse 刷新时间(@RequestParam("pkId") String pkId) throws AppException, IOException, ParseException {


        long leftTime = dynamicService.计算今日剩余时间(pkId);

        return AppResponse.buildResponse(PageAction.执行处理器("success",leftTime));


    }


}
