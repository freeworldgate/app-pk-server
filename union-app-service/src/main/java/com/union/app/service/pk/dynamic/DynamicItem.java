package com.union.app.service.pk.dynamic;

public enum DynamicItem {

    PK状态(DynamicLevel.PK,"STATU"),

    PK总人数(DynamicLevel.PK,"TOTAL-USERS"),

    PK已收款审核订单次数(DynamicLevel.PK,"FINISH-VERIFY-ORDERS"),

    PK已打赏确认收款订单次数(DynamicLevel.PK,"FINISH-FEE-ORDERS"),











    //--------------------------------------
    POST浏览次数(DynamicLevel.PK_POST,"POST-VIEW"),

    //------------------------------------------

    PKUSER榜帖被收藏的次数(DynamicLevel.PK_USER,"USER-POST-FOLLOW"),

    PKUSER今日分享次数(DynamicLevel.PK_USER,"USER-TODAY-SHARE"),





    //--------------------------------------------
    PK今日排名(DynamicLevel.PK_USER,"USER-TODAY-SORT"),

    PK当前任务(DynamicLevel.PK,"CUREENT-FEE-TASTS"),

    PK当前操作动态(DynamicLevel.PK,"CUREENT-OPER-INFOS"),
















    ;



    private DynamicLevel level;

    private String redisKeySuffix;

    DynamicItem(DynamicLevel level, String redisKeySuffix) {
        this.level = level;
        this.redisKeySuffix = redisKeySuffix;
    }

    public DynamicLevel getLevel() {
        return level;
    }

    public void setLevel(DynamicLevel level) {
        this.level = level;
    }

    public String getRedisKeySuffix() {
        return redisKeySuffix;
    }

    public void setRedisKeySuffix(String redisKeySuffix) {
        this.redisKeySuffix = redisKeySuffix;
    }
}
