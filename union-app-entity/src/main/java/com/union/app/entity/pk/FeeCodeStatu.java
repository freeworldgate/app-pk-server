package com.union.app.entity.pk;

public enum FeeCodeStatu {

    停用(0,"停用状态"),

    启用(1,"启用状态")







    ;

    private int statu;
    private String statuStr;

    FeeCodeStatu(int statu, String statuStr) {
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
