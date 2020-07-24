package com.union.app.entity.pk;

public enum InviteType {


    公开(0,"公开"),

    邀请(1,"仅邀请"),
;


    private int statu;
    private String statuStr;

    InviteType(int statu, String statuStr) {
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
