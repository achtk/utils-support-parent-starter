package com.chua.common.support.database.repository;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.orm.conditions.Wrapper;
import com.chua.common.support.database.orm.conditions.Wrappers;
import com.chua.common.support.database.orm.conditions.query.LambdaQueryWrapper;
import com.chua.common.support.database.orm.conditions.query.QueryWrapper;
import com.chua.common.support.lang.page.Page;
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
    <S extends T> List<S> list(Wrapper<S> wrapper);
    /**
     * 保存实体
     *
     * @param wrapper wrapper
     * @return 实体
     */
    default boolean exist(Wrapper<T> wrapper) {
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
     */
    int updateById(T entity);

    /**
     * 根据ID獲取數據
     *
     * @param key key
     */
    <S extends T> S getById(Serializable key);

    /**
     * 根据ID删除
     *
     * @param key key
     */
    int deleteById(Serializable key);

    /**
     * 分页查询
     *
     * @param page    页码
     * @param wrapper 條件
     * @return 结果
     */
    Page<T> page(Page<T> page, Wrapper<T> wrapper);
    /**
     * 分页查询
     *
     * @param page    页码
     * @return 结果
     */
    default Page<T> page(Page<T> page) {
        return page(page, Wrappers.emptyWrapper());
    }

    /**
     * 保存/更新
     *
     * @param entity 实体
     * @return 结果
     */
    int saveOrUpdate(T entity);

}
