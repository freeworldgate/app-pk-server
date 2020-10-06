package com.union.app.entity.pk;

public enum PkStatu {


    审核中(0,"待发布"),

    已审核(1,"已发布"),

    已关闭(2,"已关闭"),
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
