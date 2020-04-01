package com.union.app.service.pk.service.storage.immutable;

import java.util.List;

/**
 * 累积型存储，不可变记录。
 */
public interface MemoryImmutableStorage {


    /**
     * 添加
     * @param t
     * @param <T>
     */
    <T> void add(T t);

    /**
     * 查询
     * @param tClass
     * @param page
     * @param <T>
     * @return
     */
    <T> List<T> query(Class<T> tClass, String storageId, int page);















}
