package com.union.app.entity.pk.msg;

public enum MessageType {
//    新创建(0,"已保存(可修改)"),

    留言(1,"留言"),

    回复(2,"回复"),


    ;

    private int statu;
    private String statuStr;

    MessageType(int statu, String statuStr) {
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
