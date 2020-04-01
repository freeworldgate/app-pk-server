package com.union.app.service.pk.service.storage.immutable;

import java.util.List;

/**
 * 累积型存储，不可变记录。
 */
public interface IStorage {



    /**
     * 添加
     * @param t
     * @param <T>
     */
    <T> void add(String storageId,String secondaryId,T t);

    /**
     * 删除
     * @param t
     * @param <T>
     */
    <T> void delete(String storageId,String secondaryId,T t);

    /**
     * 删除
     * @param t
     * @param <T>
     */
    <T> void update(String storageId,String secondaryId,T t);

    /**
     * 查询
     * @param tClass
     * @param page
     * @param <T>
     * @return
     */
    <T> List<T> query(Class<T> tClass,String storageId,int page);















}
