package com.union.app.entity.pk.task;

public enum TaskStatu {
    无任务(0,"无任务"),

    打赏中(1,"打赏中"),

    已打赏(2,"已打赏"),


    ;

    private int statu;
    private String statuStr;

    TaskStatu(int statu, String statuStr) {
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
