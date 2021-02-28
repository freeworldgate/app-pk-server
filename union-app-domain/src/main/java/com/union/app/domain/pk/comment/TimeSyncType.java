package com.union.app.domain.pk.comment;

public enum TimeSyncType
{

    POSTLIKE(1,"打卡点赞"),
    POSTDISLIKE(2,"打卡踩"),
    POSTCOMMENT(3,"打卡评论数量"),
    COMMENTLIKE(4,"评论点赞"),
    COMMENTDISLIKE(5,"评论踩"),
    COMMENTRESTORE(6,"评论回复"),
    RESTORELIKE(7,"回复点赞"),
    RESTOREDISLIKE(8,"回复踩"),


    PK(9,"PK人数和图片总数同步"),
    CITY(10,"城市创建卡点数量同步"),

    POSTCOMPLAIN(11,"打卡投诉"),
    ;


    private int scene;
    private String desc;

    TimeSyncType(int scene, String desc) {
        this.scene = scene;
        this.desc = desc;
    }

    TimeSyncType(String desc) {
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
