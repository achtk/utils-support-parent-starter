package com.chua.common.support.database.repository;

import com.chua.common.support.annotations.Extension;
import com.sun.xml.internal.bind.v2.model.core.ID;

import java.io.Serializable;
import java.util.List;

/**
 * 接口
 *
 * @author CH
 */
@Extension("database")
public interface Repository<T> {
    /**
     * 保存实体
     *
     * @param <S>    类型
     * @return 实体
     */
    <S extends T> List<S> list();
    /**
     * 保存实体
     *
     * @param entity 实体
     * @param <S>    类型
     * @return 实体
     */
    <S extends T> S save(S entity);
    /**
     * 保存实体
     *
     * @param entity 实体
     * @param <S>    类型
     * @return 实体
     */
    <S extends T> S saveBatch(List<S> entity);

    /**
     * 根据ID更新
     * @param entity 实体
     */
    int updateById(T entity);

    /**
     * 根据ID删除
     * @param key key
     */
    int deleteById(Serializable key);
}
