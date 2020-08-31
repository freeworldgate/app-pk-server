package com.union.app.entity.pk;

public enum LinkType {


    微店(1,"微店"),
    淘宝(2,"淘宝")





    ;

    int type;

    String desc;

    LinkType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
