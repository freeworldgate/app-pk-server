package com.union.app.entity.pk.complain;

public enum ComplainType {

    收款码投诉(0,"处理中"),

    订单收款投诉(1,"已处理"),




    ;

    private int statu;
    private String statuStr;

    ComplainType(int statu, String statuStr) {
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
