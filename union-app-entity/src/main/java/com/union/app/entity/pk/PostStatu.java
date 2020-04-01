package com.union.app.entity.pk;

public enum PostStatu {

    无内容(0,""),

    上线(1,"上线"),

    下线(2,"下线"),


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
