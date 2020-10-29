package com.union.app.service.pk.dynamic;

public class DynamicKeyName {



    public static String getSetKey_Value_Name(DynamicItem item,String setName) {return item.getLevel().getName().toUpperCase() + "-" + setName.toUpperCase() + "-" + item.getRedisKeySuffix().toUpperCase(); }






}
