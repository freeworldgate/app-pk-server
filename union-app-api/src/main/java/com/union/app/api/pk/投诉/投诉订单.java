package com.union.app.api.pk.投诉;

import com.union.app.domain.pk.apply.ApplyOrder;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.click.ClickService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.service.OrderService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequestMapping(path="/pk")
public class 投诉订单 {


    @Autowired
    ComplainService complainService;




    @RequestMapping(path="/complainOrder",method = RequestMethod.GET)
    @Transactional(rollbackOn = Exception.class)
    public AppResponse 投诉订单(@RequestParam("id") String id,@RequestParam("tag") int tag,@RequestParam("userId") String userId) throws AppException, IOException {

        ComplainEntity complainEntity = complainService.查询投诉信息(id);


        if(!ObjectUtils.isEmpty(complainEntity) && (complainEntity.getStatu() == ComplainStatu.处理中)){return AppResponse.buildResponse(PageAction.消息级别提示框(Level.警告消息,"已投诉"));}
        if(!ObjectUtils.isEmpty(complainEntity) && (complainEntity.getStatu() == ComplainStatu.已处理)){return AppResponse.buildResponse(PageAction.消息级别提示框(Level.警告消息,"已受理"));}

        if(tag == 1){


            complainService.新增审核投诉(id,userId);





            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.正常消息,"Upload投诉成功"));
        }else{



            complainService.新增收款投诉(id,userId);




            return AppResponse.buildResponse(PageAction.消息级别提示框(Level.正常消息,"Order投诉成功"));
        }



    }




}
