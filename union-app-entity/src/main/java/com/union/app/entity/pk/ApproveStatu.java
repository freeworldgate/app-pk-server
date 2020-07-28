package com.union.app.entity.pk;

public enum ApproveStatu {



    处理过(1,"处理过"),

    未处理(2,"未处理"),


    ;

    private int statu;
    private String statuStr;

    ApproveStatu(int statu, String statuStr) {
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
