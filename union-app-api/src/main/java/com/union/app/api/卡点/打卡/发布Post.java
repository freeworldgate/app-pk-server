package com.union.app.api.卡点.打卡;

import com.union.app.common.dao.AppDaoService;
import com.union.app.domain.pk.Post;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.service.AppService;
import com.union.app.service.pk.service.KeyService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/pk")
public class 发布Post {


    @Autowired
    AppDaoService daoService;

    @Autowired
    ClickService clickService;

    @Autowired
    PkService pkService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    AppService appService;

    @Autowired
    RedisStringUtil redisStringUtil;

    @Autowired
    KeyService keyService;

    @RequestMapping(path="/createPost",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 发布Post(@RequestParam("pkId") String pkId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("backId") int backId,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {




        //添加时间限制

        String postId = postService.打卡(pkId,userId,title,images,backId);


        Post post = postService.查询帖子(pkId,postId,userId);

        keyService.同步Pk人数(KeyType.要同步的PK列表,pkId);

        //返回帖子  首页第一个要显示
        return AppResponse.buildResponse(PageAction.执行处理器("success",post));


    }



//
//    @RequestMapping(path="/uploadPostImgs",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 续传封面(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("title") String title,@RequestParam("userId") String userId,@RequestParam("imgUrls") List<String> images) throws AppException, IOException {
//
//
//
//        postService.续传帖子(postId,title,userId,images);
//
//        Post post = postService.查询帖子(pkId,postId,userId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
//
//    }
//



//    @RequestMapping(path="/deletePostImg",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 删除图片(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("imgId") String imgId,@RequestParam("userId") String userId) throws AppException, IOException {
//        Date cureentDate = new Date();
//
//        postService.删除帖子指定图片(postId,imgId,userId);
//
//        Post post = postService.查询帖子(pkId,postId,userId,cureentDate);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
//
//    }

//    @RequestMapping(path="/postStatu",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 查询榜帖状态(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId) throws AppException, IOException {
//        Date cureentDate = new Date();
//
//        PostEntity postEntity = postService.查询帖子ById(postId);
//        if(postEntity.getStatu() == PostStatu.显示)
//        {
//            return AppResponse.buildResponse(PageAction.信息反馈框("提示","图册已发布，不支持修改图册内容"));
////            return AppResponse.buildResponse(PageAction.执行处理器("online",""));
//        }
//        return AppResponse.buildResponse(PageAction.执行处理器("offline",""));
//
//
//
//
//
//    }
//
//
//    @RequestMapping(path="/replaceImg",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 替換图片(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("imgUrl") String imgUrl,@RequestParam("imgId") String imgId,@RequestParam("userId") String userId) throws AppException, IOException {
//        Date currentDate = new Date();
//
//        postService.替换指定图片(pkId,postId,imgUrl,imgId,userId,currentDate);
//
//
//        Post post = postService.查询帖子(pkId,postId,userId);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",post));
//
//    }
//
//
//
//    @RequestMapping(path="/replaceText",method = RequestMethod.GET)
//    @Transactional(rollbackOn = Exception.class)
//    public AppResponse 删除图片(@RequestParam("pkId") String pkId,@RequestParam("postId") String postId,@RequestParam("text") String text,@RequestParam("userId") String userId) throws AppException, IOException {
//        Date cureentDate = new Date();
//
//        postService.替换Topic(pkId,postId,text,userId);
//
//
//
//
////        Post post = postService.查询帖子(pkId,postId,userId,cureentDate);
//
//        return AppResponse.buildResponse(PageAction.执行处理器("success",""));
//
//    }
//






}
