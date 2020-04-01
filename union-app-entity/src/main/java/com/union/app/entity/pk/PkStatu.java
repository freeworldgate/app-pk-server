package com.union.app.entity.pk;

public enum PkStatu {



    推广模式(1,"推广模式"),

    打赏模式(2,"打赏模式"),





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
