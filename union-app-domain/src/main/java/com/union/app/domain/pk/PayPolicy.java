package com.union.app.domain.pk;

public enum PayPolicy {


    不收费(0,"不收费"),



    ;

    private int key;

    private String value;

    PayPolicy(int key, String value) {
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
