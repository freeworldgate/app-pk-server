package com.union.app.service.pk.dynamic;

public enum DynamicItem {

    配置(DynamicLevel.ALL,"ALL-CONFIG"),
    榜帖审核用户(DynamicLevel.ALL,"ALL-APPROVEUSER-POST"),

    审核用户消息(DynamicLevel.ALL,"ALL-APPROVEUSER-MESSAGE"),

    审核用户昨日排名(DynamicLevel.ALL,"ALL-APPROVEUSER-YESTERDAY-SORT"),

    审核用户昨日积分(DynamicLevel.ALL,"ALL-APPROVEUSER-YESTERDAY-SCORE"),

    审核用户审核中数量(DynamicLevel.ALL,"ALL-APPROVEUSER-APPROVING"),
    用户审核中POST列表(DynamicLevel.ALL,"USER-POSTLIST-APPROVING"),

    审核用户已审核数量(DynamicLevel.ALL,"ALL-APPROVEUSER-APPROVED"),
    用户已审核POST列表(DynamicLevel.ALL,"USER-POSTLIST-APPROVED"),




    等待变更模式的PK列表(DynamicLevel.ALL,"WAITING-LIST-CHANGE-PK-MODE"),


    PK模式(DynamicLevel.ALL,"PK-MODE"),



    PK状态(DynamicLevel.PK,"STATU"),

    PK总人数(DynamicLevel.PK,"TOTAL-USERS"),

    PK总推广次数(DynamicLevel.PK,"FINISH-VERIFY-ORDERS"),
    PK本次周期推广次数(DynamicLevel.PK,"SINGLE-FINISH-VERIFY-ORDERS"),


    PK总打赏次数(DynamicLevel.PK,"FINISH-FEE-ORDERS"),
    PK本次周期打赏次数(DynamicLevel.PK,"SINGLE-FINISH-FEE-ORDERS"),

    PK审核中收款码数量(DynamicLevel.PK,"PK-CODE-APPROVING-NUMS"),
    PK审核通过收款码数量(DynamicLevel.PK,"PK-CODE-APPROVED-NUMS"),
    PK审核不通过收款码数量(DynamicLevel.PK,"PK-CODE-UNAPPROVED-NUMS"),




    //--------------------------------------
    POST浏览次数(DynamicLevel.PK_POST,"POST-VIEW"),

    //------------------------------------------

    PKUSER榜帖被收藏的次数(DynamicLevel.PK_USER,"USER-POST-FOLLOW"),

    PKUSER今日分享次数(DynamicLevel.PK_USER,"USER-TODAY-SHARE"),

    PKUSER收款次数(DynamicLevel.PK_USER,"USER-CONFIRM-MONEY"),


    PKUSER用户投诉(DynamicLevel.PK_USER,"USER-COMPLAIN"),

    //--------------------------------------------
    PK今日排名(DynamicLevel.PK_USER,"USER-TODAY-SORT"),

    PK今日审批用户(DynamicLevel.PK_USER,"USER-TODAY-APPROVEUSER"),

    PK当前任务(DynamicLevel.PK,"CUREENT-FEE-TASTS"),

    PK当前操作动态(DynamicLevel.PK,"CUREENT-OPER-INFOS"),


    PK订单过期时间列表(DynamicLevel.PK,"CUREENT-ORDER-LIST"),


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
