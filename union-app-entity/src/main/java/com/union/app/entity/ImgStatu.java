package com.union.app.entity;

public enum ImgStatu {

    审核中(2,"审核中"),

    审核通过(3,"审核通过"),



    ;

    private int key;

    private String value;

    ImgStatu(int key, String value) {
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
