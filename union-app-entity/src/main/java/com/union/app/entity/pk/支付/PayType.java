package com.union.app.entity.pk.支付;

public enum PayType {



    群组(4,"购买群组"),

    卡点(5,"购买卡点"),

    时间(6,"购买时间"),






    ;

    private int statu;
    private String statuStr;

    PayType(int statu, String statuStr) {
        this.statu = statu;
        this.statuStr = statuStr;
    }

    public static PayType valueType(int type) {
        if(type == PayType.卡点.statu){return PayType.卡点;}
        else if(type == PayType.时间.statu){return PayType.时间;}
        else if(type == PayType.群组.statu){return PayType.群组;}
        else{return null;}
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getStatuStr() {
        return statuStr;
    }

    public void setStatuStr(String statuStr) {
        this.statuStr = statuStr;
    }
}
