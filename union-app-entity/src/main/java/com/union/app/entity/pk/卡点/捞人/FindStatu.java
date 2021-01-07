package com.union.app.entity.pk.卡点.捞人;

public enum FindStatu {
//    新创建(0,"已保存(可修改)"),

    审核中(1,"审核中"),

    打捞中(2,"打捞中"),

    已过期(3,"已过期"),
    ;

    private int statu;
    private String statuStr;

    FindStatu(int statu, String statuStr) {
        this.statu = statu;
        this.statuStr = statuStr;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getStatuStr() {
        return statuStr;
    }

    public void setStatuStr(String statuStr) {
        this.statuStr = statuStr;
    }
}
