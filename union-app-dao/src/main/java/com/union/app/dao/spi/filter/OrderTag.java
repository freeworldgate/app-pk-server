package com.union.app.dao.spi.filter;

public enum OrderTag {

    ASC("升序","asc"),

    DESC("降序","desc");


    private String desc;

    private String tag;

    private OrderTag(String desc, String tag) {
        this.desc = desc;
        this.tag = tag;
    }

    public String getDesc() {
        return desc;
    }

    public String getTag() {
        return tag;
    }
}
