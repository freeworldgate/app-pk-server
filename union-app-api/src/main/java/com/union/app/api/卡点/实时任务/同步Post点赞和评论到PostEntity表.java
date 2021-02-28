package com.union.app.api.卡点.实时任务;


import com.alibaba.fastjson.JSON;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.domain.pk.comment.SyncType;
import com.union.app.domain.pk.comment.TimeSyncType;
import com.union.app.entity.pk.PkEntity;
import com.union.app.entity.pk.PostEntity;
import com.union.app.entity.pk.city.CityEntity;
import com.union.app.entity.pk.post.CommentRestoreEntity;
import com.union.app.entity.pk.post.PostCommentEntity;
import com.union.app.plateform.storgae.KeyType;
import com.union.app.service.pk.service.LocationService;
import com.union.app.service.pk.service.PkService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring初始化完成以后的监听器
 * @author root
 */
@Component
public class 同步Post点赞和评论到PostEntity表
{

    @Autowired
    AppDaoService appDao;

    @Autowired
    PkService pkService;

    @Autowired
    KeyService keyService;


    @Autowired
    LocationService locationService;
    /**
     * 附近卡点的排序是按照PKEntity表的totalUser字段来排序的。所以要定时排序
     */
    @Scheduled(cron = "*/5 * * * * ?") //每天5点执行一次
    @Transactional(rollbackOn = Exception.class)
    public void work() {


        int i=0;
        String syncStr = "";
        while(i< 100 && StringUtils.isNotBlank(syncStr = keyService.获取同步队列() ))
        {
            this.process(syncStr);
            i++;
        }
        System.out.println("结束定时线程:"+Thread.currentThread().getId());
    }









    private void process(String syncStr)
    {
        System.out.println("定时线程执行同步任务:"+Thread.currentThread().getId());
        Map<String,Object> map = new HashMap<>();
        SyncType syncType = JSON.parseObject(syncStr,SyncType.class);
        if(syncType.getType() == TimeSyncType.POSTCOMPLAIN.getScene())
        {
            long complains = keyService.queryKey(syncType.getId(),KeyType.打卡投诉数量);
            map.put("complains",complains);
            appDao.updateColumById(PostEntity.class,"postId",syncType.getId(),map);

        }
        else if(syncType.getType() == TimeSyncType.POSTLIKE.getScene() || syncType.getType() == TimeSyncType.POSTDISLIKE.getScene())
        {
            long likes = keyService.queryKey(syncType.getId(),KeyType.点赞数量);
            long dislikes = keyService.queryKey(syncType.getId(),KeyType.踩数量);
            map.put("likes",likes);
            map.put("dislikes",dislikes);
            appDao.updateColumById(PostEntity.class,"postId",syncType.getId(),map);

        }
        else if(syncType.getType() == TimeSyncType.POSTCOMMENT.getScene() )
        {
            long comments = keyService.queryKey(syncType.getId(),KeyType.打卡评论数量);
            map.put("comments",comments);
            appDao.updateColumById(PostEntity.class,"postId",syncType.getId(),map);

        }
        else if(syncType.getType() == TimeSyncType.COMMENTRESTORE.getScene())
        {
            long restores = keyService.queryKey(syncType.getId(),KeyType.评论回复数量);
            map.put("restores",restores);
            appDao.updateColumById(PostCommentEntity.class,"commentId",syncType.getId(),map);

        }
        else if(syncType.getType() == TimeSyncType.COMMENTLIKE.getScene() || syncType.getType() == TimeSyncType.COMMENTDISLIKE.getScene())
        {
            long likes = keyService.queryKey(syncType.getId(),KeyType.点赞数量);
            long dislikes = keyService.queryKey(syncType.getId(),KeyType.踩数量);
            map.put("likes",likes);
            map.put("dislikes",dislikes);
            appDao.updateColumById(PostCommentEntity.class,"commentId",syncType.getId(),map);

        }
        else if(syncType.getType() == TimeSyncType.RESTORELIKE.getScene() || syncType.getType() == TimeSyncType.RESTOREDISLIKE.getScene())
        {
            long likes = keyService.queryKey(syncType.getId(),KeyType.点赞数量);
            long dislikes = keyService.queryKey(syncType.getId(),KeyType.踩数量);
            map.put("likes",likes);
            map.put("dislikes",dislikes);
            appDao.updateColumById(CommentRestoreEntity.class,"restoreId",syncType.getId(),map);

        }
        else if(syncType.getType() == TimeSyncType.CITY.getScene())
        {
            long pks = keyService.queryKey(syncType.getId(),KeyType.城市卡点数量);
            map.put("pks",pks);
            appDao.updateColumById(CityEntity.class,"cityCode",Integer.valueOf(syncType.getId()),map);

        }
        else if(syncType.getType() == TimeSyncType.PK.getScene())
        {
            long totalImgs = keyService.queryKey(syncType.getId(),KeyType.PK图片总量);
            long totalUser = keyService.queryKey(syncType.getId(),KeyType.卡点人数);
            map.put("totalUsers",totalUser);
            map.put("totalImgs",totalImgs);
            appDao.updateColumById(PkEntity.class,"pkId",syncType.getId(),map);
        }
        else
        {

        }




    }




}
