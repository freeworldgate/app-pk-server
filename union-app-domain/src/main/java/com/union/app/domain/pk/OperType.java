package com.union.app.domain.pk;

public enum OperType {


    上传收款码(0,"上传收款码"),



    ;

    private int key;

    private String value;

    OperType(int key, String value) {
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
