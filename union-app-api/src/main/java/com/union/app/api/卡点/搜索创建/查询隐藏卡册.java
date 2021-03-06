package com.union.app.api.卡点.搜索创建;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Post;
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
public class 查询隐藏卡册 {


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
    DynamicService dynamicService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/queryHiddenPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse queryHiddenPost(@RequestParam("pkId") String pkId) throws AppException, IOException, InterruptedException {

        List<DataSet> dataSets = new ArrayList<>();


        List<Post> posts = locationService.queryHiddenPkPost(pkId,1);
        dataSets.add(new DataSet("emptyImage",appService.查询背景(4)));
        dataSets.add(new DataSet("posts",posts));
        dataSets.add(new DataSet("page",1));

        dataSets.add(new DataSet("borderRadius", AppConfigService.getConfigAsInteger(ConfigItem.用户头像BorderRadius)));
        dataSets.add(new DataSet("postBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.小图片圆角)));
        dataSets.add(new DataSet("post1BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post2或4张图圆角)));
        dataSets.add(new DataSet("post2BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post1张图圆角)));
        dataSets.add(new DataSet("post3BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.文字背景圆角)));
        dataSets.add(new DataSet("videoBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.视频圆角)));
        dataSets.add(new DataSet("pkBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.PK创建者头像圆角)));
        dataSets.add(new DataSet("buttonBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.操作按钮圆角)));



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextHiddenPage",method = RequestMethod.GET)
    public AppResponse queryHiddenPost(@RequestParam("pkId") String pkId,@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<Post> posts = locationService.queryHiddenPkPost(pkId,page+1);
        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("posts",posts));
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }

}
