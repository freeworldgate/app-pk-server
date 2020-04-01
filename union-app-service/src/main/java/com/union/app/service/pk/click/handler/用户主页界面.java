package com.union.app.service.pk.click.handler;

import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.click.ClickHandler;
import com.union.app.service.pk.click.ClickItem;
import com.union.app.service.pk.click.ClickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class 用户主页界面 implements ClickHandler {

    @Autowired
    ClickService clickService;

    @PostConstruct
    void init(){
        clickService.registeHandler(ClickItem.用户界面,this);
    }


    @Override
    public PageAction handle(String userId, String id) {

        return PageAction.页面跳转("/pages/pk/userHome/userHome",true);

    }
}



