package com.union.app.plateform.data.resultcode;

public enum Level {

    正常消息("info"),

    警告消息("warn"),

    错误消息("error"),

    ;

    private String level;

    Level(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
