package com.union.app.entity.pk.complain;

public enum ComplainStatu {

    处理中(0,"处理中"),

    已处理(1,"已处理"),




    ;

    private int statu;
    private String statuStr;

    ComplainStatu(int statu, String statuStr) {
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
