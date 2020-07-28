package com.union.app.entity.pk;

public enum ActiveStatu {

    处理过(1,"处理过"),

    待处理(2,"待处理"),

    初始化(3,"初始化"),


    ;

    private int statu;
    private String statuStr;

    ActiveStatu(int statu, String statuStr) {
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
