package com.union.app.plateform.data.resultcode;

public enum ActionType {

    信息反馈框(0),

    消息级别提示框(1),

    前端数据更新(2),

    页面跳转(3),

    执行处理器(4),

    ;

    private int actionType;

    ActionType(int actionType) {
        this.actionType = actionType;
    }


    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
}
