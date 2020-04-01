package com.union.app.service.pk.dynamic;

public class DynamicKeyName {


    public static String userPk_Follow_KeyName(String pkId){return "USER-PK-FOLLOW-" + pkId;}
    public static String userPk_Share_KeyName(String pkId){return "USER-PK-SHARE-" + pkId;}
    public static String userPk_TotalShare_KeyName(String pkId){return "USER-PK-TOTAL-SHARE" + pkId;}


    public static String post_Follow_KeyName(String pkId) {return "POST-PK-FOLLOW-" + pkId;}

    public static String post_View_KeyName(String pkId) {return "POST-PK-VIEW-" + pkId;}


    public static String getKey_Value_Name(DynamicItem item) {return item.getLevel().getName().toUpperCase() + "-" + item.getRedisKeySuffix().toUpperCase(); }


    public static String getMapKey_Value_Name(DynamicItem item,String mapName) {return item.getLevel().getName().toUpperCase() + "-" + mapName.toUpperCase() + "-" + item.getRedisKeySuffix().toUpperCase(); }

    public static String getSetKey_Value_Name(DynamicItem item,String setName) {return item.getLevel().getName().toUpperCase() + "-" + setName.toUpperCase() + "-" + item.getRedisKeySuffix().toUpperCase(); }

    public static String getTaskId(String userId, String cashierId) {
        return new StringBuffer().append(userId).append("-").append(cashierId).toString();
    }
}
