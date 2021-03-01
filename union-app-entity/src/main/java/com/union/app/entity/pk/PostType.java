package com.union.app.entity.pk;

public enum PostType {



    文字(1,"文字"),
    卡片(2,"卡片"),
    图片(3,"图片"),
    视频(4,"视频"),


    ;

    private int type;
    private String desc;

    PostType(int type, String desc) {
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
