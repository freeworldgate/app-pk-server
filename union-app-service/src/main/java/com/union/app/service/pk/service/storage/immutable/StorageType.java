package com.union.app.service.pk.service.storage.immutable;

import java.util.List;

/**
 * 累积型存储，不可变记录。
 */
public enum StorageType {

    PK浏览记录(),//

    POST浏览记录(),

    PK的POST列表(),

    POST评论列表(),

    POST订单列表(),

    用户的POST列表(),




    ;

    private Class<?> type;

    private int singlePageSize;

















}
