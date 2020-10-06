package com.union.app.entity.pk;

public enum PkType {



    运营相册(2,"遗传相册"),
    内置相册(3,"内置相册"),
    审核相册(1,"普通相册"),








    ;

    private int type;
    private String desc;

    PkType(int type, String desc) {
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
