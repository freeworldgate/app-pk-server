package com.union.app.api.卡点.搜索创建;

import com.union.app.domain.pk.PkImage;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkEntity;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.user.UserService;
import org.apache.commons.lang.StringUtils;
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
public class 查询卡点图片 {


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

    @Autowired
    ComplainService complainService;

    @Autowired
    LocationService locationService;

    @RequestMapping(path="/queryPkImages",method = RequestMethod.GET)
    public AppResponse queryPkImages(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId) throws AppException, IOException, InterruptedException {

        //创建者
        PkEntity pk = locationService.querySinglePkEntity(pkId);
        User creator = pkService.queryPkCreator(pkId);
        //查询者图片
        PkImage userImage = locationService.查询用户卡点图片(pkId,userId);
        //下一页
        List<PkImage> images = locationService.查询卡点图片(pkId,1);


        if(!ObjectUtils.isEmpty(userImage))
        {
            for(PkImage image:images)
            {
                if(StringUtils.equalsIgnoreCase(image.getImageId(),userImage.getImageId()))
                {
                    images.remove(image);

                    break;
                }
            }
            images.add(0,userImage);
        }

        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(new DataSet("pkBackUrl",pk.getBackUrl()));

        dataSets.add(new DataSet("images",images));
        dataSets.add(new DataSet("page",1));
        dataSets.add(new DataSet("creator",creator));
        return AppResponse.buildResponse(PageAction.前端多条数据更新(dataSets));

    }

    @RequestMapping(path="/nextPkImagePage",method = RequestMethod.GET)
    public AppResponse nextPkImagePage(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("page") int page) throws AppException, IOException, InterruptedException {


        List<PkImage> images = locationService.查询卡点图片(pkId,page+1);

        for(PkImage image:images)
        {
            if(StringUtils.equalsIgnoreCase(image.getUser().getUserId(),userId)){
                images.remove(image);
            }
        }
        if(CollectionUtils.isEmpty(images))
        {
            return AppResponse.buildResponse(PageAction.前端数据更新("nomore",true));

        }

        return AppResponse.buildResponse(PageAction.执行处理器("success",images));


    }


    @RequestMapping(path="/uploadPkImages",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse uploadPkImages(@RequestParam("pkId") String pkId,@RequestParam("userId") String userId,@RequestParam("imgUrl") String imgUrl) throws AppException, IOException, InterruptedException {

        //创建者

        locationService.上传卡点图片(pkId,userId,imgUrl);
        PkImage userImage = locationService.查询用户卡点图片(pkId,userId);



        return AppResponse.buildResponse(PageAction.执行处理器("success",userImage));

    }

}
