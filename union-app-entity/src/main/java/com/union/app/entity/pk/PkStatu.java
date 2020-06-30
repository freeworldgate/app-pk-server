package com.union.app.entity.pk;

public enum PkStatu {


    审核中(0,"审核中"),

    已审核(1,"审核通过"),
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
