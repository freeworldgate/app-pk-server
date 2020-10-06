package com.union.app.entity.pk;

public enum MessageType {


    收费(0,"收"),

    不收费(1,"不收"),
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
