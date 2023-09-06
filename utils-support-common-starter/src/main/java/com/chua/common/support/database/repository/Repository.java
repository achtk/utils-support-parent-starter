package com.chua.common.support.database.repository;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.orm.conditions.SqlWrapper;
import com.chua.common.support.database.orm.conditions.SqlWrappers;
import com.chua.common.support.lang.page.Page;

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
     * @param <S> 类型
     * @return 实体
     */
    <S extends T> List<S> list();

    /**
     * 保存实体
     *
     * @param <S>     类型
     * @param wrapper wrapper
     * @return 实体
     */
    <S extends T> List<S> list(SqlWrapper<S> wrapper);

    /**
     * 保存实体
     *
     * @param wrapper wrapper
     * @return 实体
     */
    default boolean exist(SqlWrapper<T> wrapper) {
        return page(new Page<T>().setPageNum(1).setPageSize(1), wrapper).getData().size() != 0;
    }

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
     *
     * @param entity 实体
     * @return 数量
     */
    int updateById(T entity);

    /**
     * 根据ID獲取數據
     *
     * @param key key
     * @return s
     */
    <S extends T> S getById(Serializable key);

    /**
     * 根据ID删除
     *
     * @param key key
     * @return 数量
     */
    int deleteById(Serializable key);

    /**
     * 分页查询
     *
     * @param page    页码
     * @param wrapper 條件
     * @return 结果
     */
    Page<T> page(Page<T> page, SqlWrapper<T> wrapper);

    /**
     * 分页查询
     *
     * @param page 页码
     * @return 结果
     */
    default Page<T> page(Page<T> page) {
        return page(page, SqlWrappers.emptyWrapper());
    }

    /**
     * 保存/更新
     *
     * @param entity 实体
     * @return 结果
     */
    int saveOrUpdate(T entity);

    /**
     * 删除数据
     *
     * @param wrapper 条件
     */
    void delete(SqlWrapper<T> wrapper);
}
