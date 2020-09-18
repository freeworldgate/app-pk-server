package com.union.app.entity.pk;

public enum ApproveStatu {



    已处理(1,"已处理"),

    未处理(2,"未处理"),

    驳回修改(3,"驳回修改"),

    请求审核(4,"请求审核"),






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
