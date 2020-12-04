package com.union.app.entity.pk;

public enum PostStatu {



    显示(1,"显示"),

    隐藏(3,"隐藏"),






    ;

    private int statu;
    private String statuStr;

    PostStatu(int statu, String statuStr) {
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
