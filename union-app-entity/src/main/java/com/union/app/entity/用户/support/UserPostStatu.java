package com.union.app.entity.用户.support;

public enum UserPostStatu {



    未打榜(0),


    已打榜(1);

    private int type;

    UserPostStatu(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
