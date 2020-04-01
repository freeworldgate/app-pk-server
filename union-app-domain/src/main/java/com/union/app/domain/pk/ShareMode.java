package com.union.app.domain.pk;

public enum ShareMode {

    完全开放(1,"完全开放"),

//    半开放(2,"半开放"),

    完全不开放(2,"完全不开放"),


    ;

    private int mode;

    private String desc;

    ShareMode(int mode, String desc) {
        this.mode = mode;
        this.desc = desc;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
