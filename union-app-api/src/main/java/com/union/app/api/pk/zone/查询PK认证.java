package com.union.app.api.pk.zone;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.PkActive;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.审核.AppMessage;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.pk.审核.PkComment;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.PkActiveEntity;
import com.union.app.entity.pk.PkCashierEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 查询PK认证 {


    @Autowired
    PkService pkService;

    @Autowired
    ClickService clickService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;


    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppService appService;

    @RequestMapping(path="/queryPkApprove",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException {
            List<DataSet> dataSets = new ArrayList<>();
            PkActive active = appService.查询激活信息(pkId);
            if(!ObjectUtils.isEmpty(active))
            {
                dataSets.add(new DataSet("pkActive", active));
                dataSets.add(new DataSet("icon1", "/images/waiting.png"));
                dataSets.add(new DataSet("approveText", "审核中"));

            }
            else
            {
//                dataSets.add(new DataSet("activeCode", ""));
                dataSets.add(new DataSet("title", "激活榜单"));
                dataSets.add(new DataSet("title1", "输入激活码"));
                dataSets.add(new DataSet("tip1", "1、只有比别人更早,更勤奋的努力,才能尝到成功的滋味。"));
                dataSets.add(new DataSet("tip2", "2、书籍便是这种改造灵魂的工具。人类所需要的，是富有启发性的养料。而阅读，则正是这种养料———雨果"));
                dataSets.add(new DataSet("tip3", "3、有创见的书籍传布在黑暗的时代里，犹如一些太阳光照耀在荒凉的沙漠上，为的是化黑暗为光明。这些书是人类精神史上划时代的作品，人们凭借它们的原则，向种种新的发现迈进。书本是将圣贤豪杰的心照射到我们心里的忠实的镜子。——吉本"));


                dataSets.add(new DataSet("buttonName", "请求激活"));
                dataSets.add(new DataSet("buttonType", "share"));


                PkCashierEntity weidianUrl = appService.获取微店(pkId);
                PkCashierEntity taobaoUrl = appService.获取淘宝(pkId);

                dataSets.add(new DataSet("link1Name", "微店"));
                dataSets.add(new DataSet("link1Icon", ObjectUtils.isEmpty(weidianUrl)?"":weidianUrl.getLinkUrl()));
                dataSets.add(new DataSet("link1Url", "/pages/pk/message/message?pkId=" + pkId + "&userId=" + userId + "&type=2"));

                dataSets.add(new DataSet("link2Name", "淘宝"));
                dataSets.add(new DataSet("link2Icon", ObjectUtils.isEmpty(taobaoUrl)?"":taobaoUrl.getLinkUrl()));
                dataSets.add(new DataSet("link2Url", "/pages/pk/message/message?pkId=" + pkId + "&userId=" + userId + "&type=3"));
            }






            return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));






    }

}
