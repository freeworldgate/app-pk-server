package com.union.app.service.pk.service;

import com.union.app.common.dao.AppDaoService;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {


    @Autowired
    AppDaoService daoService;

    @Autowired
    UserService userService;


    @Autowired
    PkService pkService;

    @Autowired
    DynamicService dynamicService;


    public void sendText2User(String userId,String text){};
    public void sendImage2User(String userId,String mediaId){};














}
