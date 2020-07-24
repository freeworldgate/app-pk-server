package com.union.app.entity.pk;

public enum PkStatu {


    审核中(0,"待激活"),

    已审核(1,"已激活"),
;


    private int statu;
    private String statuStr;

    PkStatu(int statu, String statuStr) {
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
