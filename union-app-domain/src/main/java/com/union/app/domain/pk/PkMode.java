package com.union.app.domain.pk;

public enum PkMode {

    推广模式(0,"推广模式"),

    打赏模式(1,"打赏模式"),







    投诉模式(2,"投诉模式"),

    惩罚模式(3,"惩罚模式"),

    休眠模式(4,"休眠模式"),



    ;

    private int key;

    private String value;

    PkMode(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }




}
