package com.union.app.entity.pk.社交;

public enum GroupStatu {

    审核中(0,"审核中"),

    已通过(1,"已生效"),

    已过期(2,"已过期"),





    ;

    private int statu;
    private String statuStr;

    GroupStatu(int statu, String statuStr) {
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
