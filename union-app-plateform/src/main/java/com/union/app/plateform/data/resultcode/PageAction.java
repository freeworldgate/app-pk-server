package com.union.app.plateform.data.resultcode;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PageAction {


    private int _action_type;

    private String _0_message;
    private String _0_title;

    private String _1_message;
    private String _1_message_level;

    private List<DataSet> _2_data_sets;

    private String _3_url;
    private boolean _3_isneed_userId;

    private String _4_handler_name;
    private Object _4_data;




    public static PageAction 信息反馈框(String title,String message){
        PageAction action = new PageAction();
        action.set_action_type(ActionType.信息反馈框.getActionType());
        action.set_0_message(message);
        action.set_0_title(title);

        return action;
    }

    public static PageAction 消息级别提示框(Level level,String message){
        PageAction action = new PageAction();
        action.set_action_type(ActionType.消息级别提示框.getActionType());
        action.set_1_message(message);
        action.set_1_message_level(level.getLevel());
        return action;

    }

    public static PageAction 前端数据更新(String key,Object data){
        PageAction action = new PageAction();
        action.set_action_type(ActionType.前端数据更新.getActionType());
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet(key,data));
        action.set_2_data_sets(dataSets);
        return action;
    }

    public static PageAction 前端多条数据更新(List<DataSet> dataSets){
        PageAction action = new PageAction();
        action.set_action_type(ActionType.前端数据更新.getActionType());
        action.set_2_data_sets(dataSets);
        return action;
    }
    public static PageAction 页面跳转(String url,boolean isNeedUserId){
        PageAction action = new PageAction();
        action.set_action_type(ActionType.页面跳转.getActionType());
        action.set_3_url(url);
        action.set_3_isneed_userId(isNeedUserId);
        return action;
    }

    public static PageAction 执行处理器(String code,Object handleData){
        PageAction action = new PageAction();
        action.set_action_type(ActionType.执行处理器.getActionType());
        action.set_4_handler_name(code);
        action.set_4_data(handleData);
        return action;
    };




















}
