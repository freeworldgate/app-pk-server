package com.union.app.domain.pk.comment;

public enum PubType
{

    RESTORE(1,"回复"),
    POSTCOMMENT(2,"评论"),

    ;


    private int scene;
    private String desc;

    PubType(int scene, String desc) {
        this.scene = scene;
        this.desc = desc;
    }

    PubType(String desc) {
        this.desc = desc;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    ;











}
