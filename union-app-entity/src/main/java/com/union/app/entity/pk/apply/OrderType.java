package com.union.app.entity.pk.apply;

public enum OrderType {

    审核订单(1,"审核费用"),


    打赏订单(2,"打赏金额"),


    ;

    private int type;
    private String title;

    OrderType(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
