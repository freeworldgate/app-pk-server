package com.union.app.entity.user.support;

public enum  UserType {



    普通用户(0),


    重点用户(1),

    管理用户(2),

    消息通知(3);

    private int type;

    UserType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
