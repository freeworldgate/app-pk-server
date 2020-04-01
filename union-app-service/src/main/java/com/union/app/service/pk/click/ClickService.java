package com.union.app.service.pk.click;

import com.union.app.dao.spi.AppDaoService;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.PageAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


@Service
public class ClickService {


    @Autowired
    AppDaoService appDaoService;





    public Map<ClickItem,ClickHandler> handlerMap = new HashMap<>();



    public PageAction click(int tab, String userId, String id) throws AppException {
        ClickItem clickItem = login(tab,userId);
        ClickHandler handler = handlerMap.get(clickItem);
        if(ObjectUtils.isEmpty(handler))
        {
            throw AppException.buildException(PageAction.信息反馈框("操作异常","未找到对应的处理器"));
        }
        else
        {
            return handler.handle(userId,id);
        }
    }

    private ClickItem login(int tab, String userId) throws AppException {
        ClickItem clickItem = ClickItem.valueOf(tab);
        if(clickItem.isLoginStatu()&&(!userExist(userId))){
            throw AppException.buildException(PageAction.执行处理器("login",""));
        }
        return clickItem;

    }

    private boolean userExist(String userId) {
        if(!(StringUtils.isEmpty(userId) || org.apache.commons.lang.StringUtils.equalsIgnoreCase("undefined",userId) || org.apache.commons.lang.StringUtils.equalsIgnoreCase("nan",userId))){
            return true;
        }
        return false;
    }

    public void registeHandler(ClickItem clickItem,ClickHandler clickHandler){
        handlerMap.put(clickItem,clickHandler);

    }

}
