package com.union.app.plateform.storgae;

public enum  KeyName {

    守榜用户("WINNERS"),

    用户榜帖("UserPost"),

    处理的Post最大值("MAX_POST_VALUE"),

    用户浏览记录("VIEW_HISTORY");


















    private String name;

    KeyName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
