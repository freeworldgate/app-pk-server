package com.union.app.entity.pk;

public enum OrderStatu {

    无订单(0,"无订单"),

    订单确认中(1,"确认中"),

    已收款(2,"已收款"),

    未收款(3,"未收款"),


    ;

    private int statu;
    private String statuStr;

    OrderStatu(int statu, String statuStr) {
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
