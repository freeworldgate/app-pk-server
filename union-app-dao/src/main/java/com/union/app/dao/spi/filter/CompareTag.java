package com.union.app.dao.spi.filter;

public enum CompareTag {

    Equal("等于","="),

    NotEqual("不等于","<>"),

    Bigger("大于",">"),

    Small("小于","<"),

    EqualBigger("大于等于",">="),

    EqualSmall("小于等于","<="),


    ;

    private String desc;

    private String symbol;

    CompareTag(String desc, String symbol) {
        this.desc = desc;
        this.symbol = symbol;
    }

    public String getDesc() {
        return desc;
    }

    public String getSymbol() {
        return symbol;
    }
}
