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
        import com.union.app.util.time.TimeUtils;
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
    LikeService likeService;

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

    @Autowired
    PkUserDynamicService pkUserDynamicService;

    PkDynamicService pkDynamicService;

    @RequestMapping(path="/queryPk",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {

        //统计PK请求此次数。




        List<DataSet> dataSets = new ArrayList<>();
        //查询PK详情
        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);
        if(pkEntity.getTopPostSetTime()>0){
            long timeLength = System.currentTimeMillis() - pkEntity.getTopPostSetTime();

            dataSets.add(new DataSet("topTime",TimeUtils.已顶置时间(timeLength)));

        }
        PkDetail pkDetail = locationService.querySinglePk(pkEntity);
        List<Post> posts = pkService.queryPkPost(pkId,1);

        if((System.currentTimeMillis() - pkEntity.getTopPostSetTime()) < pkEntity.getTopPostTimeLength() * 60 * 1000L) {
            去除顶置POST(posts, pkEntity.getTopPostId());
            Post topPost = postService.查询顶置帖子(pkEntity);
            if (!ObjectUtils.isEmpty(topPost)) {
                posts.add(0, topPost);
            }
        }
        else
        {
            pkDetail.setTopPostId(null);
        }


        dataSets.add(new DataSet("pk",pkDetail));
        dataSets.add(new DataSet("inviteStatu",appService.查询收藏状态(pkId,userId)));
        likeService.查询Post点赞记录(posts,userId);
        dataSets.add(new DataSet("posts",posts));

        if(CollectionUtils.isEmpty(posts)){dataSets.add(new DataSet("emptyData",appService.查询背景(4)));}
        dataSets.add(new DataSet("page",1));
        if(userService.isUserExist(userId))
        {
            PkUserDynamicEntity pkUserDynamicEntity = pkUserDynamicService.查询卡点用户动态表(pkId,userId);
            if(ObjectUtils.isEmpty(pkUserDynamicEntity))
            {
                dataSets.add(new DataSet("leftTime",0));
            }
            else
            {
                long postLastUpdateTime = pkUserDynamicEntity.getLastPublishPostTime();
                long leftTime = System.currentTimeMillis() - postLastUpdateTime;
                int timePerid = AppConfigService.getConfigAsInteger(ConfigItem.发帖的时间间隔);
                if(leftTime > timePerid * 1000L)
                {
                    dataSets.add(new DataSet("leftTime",0));
                }
                else
                {
//                    dataSets.add(new DataSet("leftTime",0));
                    dataSets.add(new DataSet("leftTime",timePerid-leftTime/1000));
                }
            }
            dataSets.add(new DataSet("postTimes",ObjectUtils.isEmpty(pkUserDynamicEntity)?0:pkUserDynamicEntity.getPostTimes()));
            dataSets.add(new DataSet("totalPostTimes",ObjectUtils.isEmpty(pkUserDynamicEntity)?0:pkUserDynamicEntity.getTotalPostTimes()));

            //查询用户打卡次数:
        }
        dataSets.add(new DataSet("borderRadius",AppConfigService.getConfigAsInteger(ConfigItem.用户头像BorderRadius)));
        dataSets.add(new DataSet("postBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.小图片圆角)));
        dataSets.add(new DataSet("post1BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post2或4张图圆角)));
        dataSets.add(new DataSet("post2BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.Post1张图圆角)));
        dataSets.add(new DataSet("post3BorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.文字背景圆角)));
        dataSets.add(new DataSet("videoBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.视频圆角)));
        dataSets.add(new DataSet("pkBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.PK创建者头像圆角)));
        dataSets.add(new DataSet("buttonBorderRadius",AppConfigService.getConfigAsInteger(ConfigItem.操作按钮圆角)));

        dataSets.add(new DataSet("pageTag",true));
        dataSets.add(new DataSet("nomore",false));
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
    public AppResponse 查询单个PK(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("sortId") long sortId) throws AppException, IOException {


        PkEntity pkEntity = locationService.querySinglePkEntity(pkId);


        //页数不断递增，但是只有一百页。
        List<Post> posts = pkService.查询下一页Post(pkId,sortId);
        去除顶置POST(posts,pkEntity.getTopPostId());
        if(CollectionUtils.isEmpty(posts))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",Boolean.TRUE));
        }
        likeService.查询Post点赞记录(posts,userId);
        return AppResponse.buildResponse(PageAction.执行处理器("success",posts));

    }

}
