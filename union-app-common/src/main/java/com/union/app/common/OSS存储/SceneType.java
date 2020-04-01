package com.union.app.common.OSS存储;

public enum SceneType {

    存储PK缓存("PKS","PK","PK : PKID : PKEntity"),
    存储POST相册("Post-Album","album","PkId-Album : PostId : Album"),
    存储POST封面("Post-FirstPage","firstPage","PkId-FirstPage : PostId : FirstPage"),
    存储POST动态信息("Post-Dynamic","dynamic","PkId-FirstPage : PostId : Dynamic"),
    存储POST评论信息("Post-Comment","comment","PostId-Comment : PostId : Dynamic"),
//    存储POST列表相关信息("Post-Dynamic","dynamic","PkId-FirstPage : PostId : Dynamic"),






    ;


    private String scene;

    private String code;

    private String struct;

    SceneType(String scene, String code, String struct) {
        this.scene = scene;
        this.code = code;
        this.struct = struct;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStruct() {
        return struct;
    }

    public void setStruct(String struct) {
        this.struct = struct;
    }
}
