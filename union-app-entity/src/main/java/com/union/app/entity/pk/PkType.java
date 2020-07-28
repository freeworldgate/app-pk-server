package com.union.app.entity.pk;

public enum PkType {



    预设相册("预设相册"),

    平台相册("审核相册"),

    运营相册("运营相册"),

    ;

    private String desc;

    PkType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
