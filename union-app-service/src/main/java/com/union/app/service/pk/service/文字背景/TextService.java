package com.union.app.service.pk.service.文字背景;

import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.pk.文字背景.TextBack;
import com.union.app.entity.pk.文字背景.TextBackEntity;
import com.union.app.util.idGenerator.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class TextService {


    @Autowired
    AppDaoService daoService;

    private static Map<String,TextBack> backMap = new HashMap<>();

    private static volatile long updateTime;

    @Autowired
    KeyService keyService;

    private static volatile long lastUpdateTime;

    public List<TextBack> 查询背景() {
        List<TextBack> list = new ArrayList<>();
        if(backMap.isEmpty()|| (keyService.getBackUpdate() != lastUpdateTime))
        {
            backMap.clear();
            更新背景列表();
        }
        list.addAll(backMap.values());
        return list;

    }

    public synchronized Map<String,TextBack> 更新背景列表() {

        if(backMap.isEmpty())
        {
            Map<String,TextBack> sBackMap = new HashMap<>();
            EntityFilterChain filter = EntityFilterChain.newFilterChain(TextBackEntity.class)
                    .pageLimitFilter(1,30)
                    .orderByRandomFilter();
            List<TextBackEntity> textBackEntities = daoService.queryEntities(TextBackEntity.class,filter);
            textBackEntities.forEach(textBackEntity -> {
                TextBack textBack = new TextBack();
                textBack.setBackId(textBackEntity.getBackId());
                textBack.setBackColor(textBackEntity.getBackColr());
                textBack.setBackUrl(textBackEntity.getBackUrl());
                textBack.setFontColor(textBackEntity.getFontColor());
                sBackMap.put(textBackEntity.getBackId(),textBack);

            });
            backMap.putAll(sBackMap);
            lastUpdateTime = keyService.getBackUpdate();
        }
        return backMap;

    }

    public TextBack 查询TextBackEntity(String backId) {
        TextBack textBack = new TextBack();
        textBack.setBackId("-1");
        textBack.setBackColor("fafafa");
        textBack.setFontColor("000000");
        textBack.setBackUrl("");

        EntityFilterChain filter = EntityFilterChain.newFilterChain(TextBackEntity.class)
                .compareFilter("backId",CompareTag.Equal,backId);
        TextBackEntity textBackEntity = daoService.querySingleEntity(TextBackEntity.class,filter);
        if(!ObjectUtils.isEmpty(textBackEntity))
        {
            textBack.setBackId(textBackEntity.getBackId());
            textBack.setBackUrl(textBackEntity.getBackUrl());
            textBack.setFontColor(textBackEntity.getFontColor());
            textBack.setBackColor(textBackEntity.getBackColr());
        }

        return textBack;
    }

    public String 添加背景(String backColor, String fontColor, String imgUrl) {

        TextBackEntity textBackEntity = new TextBackEntity();
        textBackEntity.setBackId(IdGenerator.getBackId());
        textBackEntity.setBackColr(backColor);
        textBackEntity.setFontColor(fontColor);
        textBackEntity.setBackUrl(imgUrl);
        daoService.insertEntity(textBackEntity);

        return textBackEntity.getBackId();
    }

    public List<TextBack> 查询TextBacks(int page) {
            List<TextBack> textBacks = new ArrayList<>();

            EntityFilterChain filter = EntityFilterChain.newFilterChain(TextBackEntity.class)
                    .pageLimitFilter(page,30);
            List<TextBackEntity> textBackEntities = daoService.queryEntities(TextBackEntity.class,filter);
            textBackEntities.forEach(textBackEntity -> {
                TextBack textBack = new TextBack();
                textBack.setBackId(textBackEntity.getBackId());
                textBack.setBackColor(textBackEntity.getBackColr());
                textBack.setBackUrl(textBackEntity.getBackUrl());
                textBack.setFontColor(textBackEntity.getFontColor());
                textBacks.add(textBack);

            });


            return textBacks;


    }

    public void 删除背景(String backId) {
        EntityFilterChain filter = EntityFilterChain.newFilterChain(TextBackEntity.class)
                .compareFilter("backId",CompareTag.Equal,backId);
        TextBackEntity textBackEntity = daoService.querySingleEntity(TextBackEntity.class,filter);

        daoService.deleteEntity(textBackEntity);



    }
}
