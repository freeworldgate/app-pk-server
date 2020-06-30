package com.union.app.service.quartz;

import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.entity.pk.审核.ApproveMessageEntity;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class 重新上传微信图片 {


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    ApproveService approveService;

    @Autowired
    AppDaoService daoService;

    @Scheduled(cron = "0 */1 * * * ?") // 每分钟执行一次  刷新整个PKID的所有PAGE
    @Transactional(rollbackOn = Exception.class)
    public void work() throws Exception {

        //TODO 每日清除排名信息，和 分享信息



//        //查询所有PK   循环删除
//        int page = 0;
//        for(){}

        for(int i=0;i<100;i++) {
            String pkId = dynamicService.获取需要更新公告的PKId();
            if(!StringUtils.isBlank(pkId)) {
                ApproveMessageEntity approveMessageEntity = approveService.获取审核人员消息Entity(pkId);
                if (!org.springframework.util.ObjectUtils.isEmpty(approveMessageEntity)) {
                    if (TimeUtils.图片是否在微信中过期(approveMessageEntity.getTime())) {
                        String mediaId = WeChatUtil.uploadImg2Wx(approveMessageEntity.getImgUrl());
                        approveMessageEntity.setMediaId(mediaId);
                        approveMessageEntity.setTime(System.currentTimeMillis());
                        daoService.updateEntity(approveMessageEntity);
                    }

                }
            }
        }

//        dynamicService.delKey(DynamicKeyName.getMapKey_Value_Name(DynamicItem.PKUSER今日分享次数,pkId));

//        dynamicService.delKey(DynamicKeyName.getSetKey_Value_Name(DynamicItem.PK今日排名,pkId));





//
//        for(int i=0;i<10;i++){
//            FactualInfo factualInfo = new FactualInfo();
//            factualInfo.setFactualId(UUID.randomUUID().toString());
//            factualInfo.setUser(RandomUtil.getRandomUser());
//            factualInfo.setOperType(new KeyNameValue(1,"上传收款码"));
//            factualInfo.setTime(RandomUtil.getRandomDate());
//            dynamicService.添加动态("PK01",factualInfo);
//            dynamicService.更新今日用户排名("PK01",RandomUtil.getRandomUser().getUserId());
//        }
//
//
//
//
//
//
//
//
//
//
//
//        List<FactualInfo> factualInfos = dynamicService.获取当前PK操作动态("PK01");
//        for(FactualInfo factualInfo1:factualInfos){
//            System.out.println("factualInfo=" + factualInfo1.toString());
//        }





    }












}
