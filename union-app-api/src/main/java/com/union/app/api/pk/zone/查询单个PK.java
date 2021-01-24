package com.union.app.api.pk.zone;

        import com.union.app.common.config.AppConfigService;
        import com.union.app.domain.pk.PkDetail;
        import com.union.app.domain.pk.Post;
        import com.union.app.entity.pk.PkEntity;
        import com.union.app.entity.pk.用户Key.PkUserDynamicEntity;
        import com.union.app.plateform.constant.ConfigItem;
        import com.union.app.plateform.data.resultcode.AppException;
        import com.union.app.plateform.data.resultcode.AppResponse;
        import com.union.app.plateform.data.resultcode.DataSet;
        import com.union.app.plateform.data.resultcode.PageAction;
        import com.union.app.plateform.storgae.redis.RedisStringUtil;
        import com.union.app.service.pk.click.ClickService;
        import com.union.app.service.pk.dynamic.DynamicService;
        import com.union.app.service.pk.service.*;
        import com.union.app.service.pk.service.pkuser.PkDynamicService;
        import com.union.app.service.pk.service.pkuser.PkUserDynamicService;
        import com.union.app.service.user.UserService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.util.CollectionUtils;
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
public class 查询单个PK {


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
    ApproveService approveService;

    @Autowired
    AppService appService;

    @Autowired
    LocationService locationService;

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    PkDynamicService pkDynamicService;

    @RequestMapping(path="/queryPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {

        List<DataSet> dataSets = new ArrayList<>();


        //查询PK详情
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        PkDetail pkDetail = locationService.querySinglePk(pkEntity);


        List<Post> posts = pkService.queryPkPost(pkId,0);
        去除顶置POST(posts,pkEntity.getTopPostId());
        Post topPost = postService.查询顶置帖子(pkEntity);
        if(!ObjectUtils.isEmpty(topPost)){posts.add(0,topPost);}


        dataSets.add(new DataSet("pk",pkDetail));


        dataSets.add(new DataSet("inviteStatu",appService.查询收藏状态(pkId,userId)));
        dataSets.add(new DataSet("posts",posts));
        if(CollectionUtils.isEmpty(posts)){dataSets.add(new DataSet("emptyData",appService.查询背景(4)));}
        dataSets.add(new DataSet("page",0));
        if(userService.isUserExist(userId))
        {
            PkUserDynamicEntity pkUserDynamicEntity = pkUserDynamicService.查询卡点用户动态表(pkId,userId);

            if(ObjectUtils.isEmpty(pkUserDynamicEntity)){
                dataSets.add(new DataSet("leftTime",0));
            }
            else
            {
                long postLastUpdateTime = pkUserDynamicEntity.getLastPublishPostTime();
                long leftTime = System.currentTimeMillis() - postLastUpdateTime;
                int timePerid = AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔);
                if(leftTime > timePerid * 1000)
                {
                    dataSets.add(new DataSet("leftTime",0));
                }
                else
                {
                    dataSets.add(new DataSet("leftTime",timePerid-leftTime/1000));
                }


            }
            PkUserDynamicEntity entity = pkUserDynamicService.查询卡点用户动态表(pkId,userId);
            dataSets.add(new DataSet("postTimes",ObjectUtils.isEmpty(entity)?0:entity.getPostTimes()));
            //查询用户打卡次数:
        }



        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    private void 去除顶置POST(List<Post> posts, String topPostId) {
        for(int i=0;i<posts.size();i++){
            if(org.apache.commons.lang.StringUtils.equals(topPostId,posts.get(i).getPostId()))
            {

                posts.remove(posts.get(i));
                break;

            }

        }



    }

    @RequestMapping(path="/nextPage",method = RequestMethod.GET)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException {

        //页数不断递增，但是只有一百页。
        List<Post> posts = pkService.queryPkPost(pkId,page+1);
        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }

        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("posts",posts));
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }

}
