package com.chua.common.support.extra.quickio.api;

import com.chua.common.support.extra.quickio.core.IoEntity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 集合
 *
 * @param <T> 类型
 * @author CH
 */
public interface Collection<T extends IoEntity> {
    /**
     * 保存
     *
     * @param t 数据
     */
    void save(final T t);

    /**
     * 保存
     *
     * @param list 数据
     */
    void save(final List<T> list);

    /**
     * 更新
     *
     * @param t         数据
     * @param predicate 条件
     */
    void update(T t, Predicate<T> predicate);

    /**
     * 更新
     *
     * @param t        数据
     * @param consumer 条件
     */
    void updateWithIndex(T t, Consumer<FindOptions> consumer);

    /**
     * 删除
     *
     * @param id ID
     */
    void delete(long id);

    /**
     * 删除
     *
     * @param ids ID
     */
    void delete(long... ids);

    /**
     * 删除
     *
     * @param ids ID
     */
    void delete(List<Long> ids);

    /**
     * 删除
     *
     * @param predicate 条件
     */
    void delete(Predicate<T> predicate);

    /**
     * 删除全部
     */
    void deleteAll();

    /**
     * 删除
     *
     * @param consumer 回调
     */
    void deleteWithIndex(Consumer<FindOptions> consumer);

    /**
     * 查询全部
     *
     * @return 数据
     */
    List<T> findAll();

    /**
     * 查询数据
     *
     * @param predicate 条件
     * @param consumer  回调
     * @return 结果
     */
    List<T> find(Predicate<T> predicate, Consumer<FindOptions> consumer);

    /**
     * 查询数据
     *
     * @param predicate 条件
     * @return 结果
     */
    List<T> find(Predicate<T> predicate);

    /**
     * 查询数据
     *
     * @param ids 索引
     * @return 结果
     */
    List<T> find(List<Long> ids);

    /**
     * 查询数据
     *
     * @param ids 索引
     * @return 结果
     */
    List<T> find(long... ids);

    /**
     * 查询数据
     *
     * @param predicate 函数
     * @param consumer  回调
     * @return 结果
     */
    List<T> findWithId(Predicate<Long> predicate, Consumer<FindOptions> consumer);

    /**
     * 查询数据
     *
     * @param predicate 函数
     * @return 结果
     */
    List<T> findWithId(Predicate<Long> predicate);

    /**
     * 查询数据
     *
     * @param predicate 函数
     * @param consumer  回调
     * @return 结果
     */
    List<T> findWithTime(Predicate<Long> predicate, Consumer<FindOptions> consumer);

    /**
     * 查询数据
     *
     * @param predicate 函数
     * @return 结果
     */
    List<T> findWithTime(Predicate<Long> predicate);

    /**
     * 查询第一个元素
     * @param predicate 函数
     * @return 元素
     */

    T findFirst(Predicate<T> predicate);

    /**
     * 查询数据
     *
     * @return 结果
     */
    T findFirst();

    /**
     * 查询数据
     *
     * @param predicate 条件
     * @return 结果
     */
    T findLast(Predicate<T> predicate);

    /**
     * 查询数据
     *
     * @return 结果
     */
    T findLast();

    /**
     * 查询数据
     *
     * @param id 索引
     * @return 结果
     */
    T findOne(long id);

    /**
     * 查询数据
     *
     * @param predicate 条件
     * @return 结果
     */
    T findOne(Predicate<T> predicate);

    /**
     * 查询数据
     * @param consumer 回调
     * @return 结果
     */

    T findWithIndex(Consumer<FindOptions> consumer);

    /**
     * 是否存在
     * @param consumer 回调
     * @return 结果
     */
    boolean exist(Consumer<FindOptions> consumer);

    /**
     * 删除索引
     *
     * @param fieldName 字段
     */
    void dropIndex(String fieldName);

    /**
     * 统计
     *
     * @param predicate 条件
     * @return 数量
     */
    long count(Predicate<T> predicate);

    /**
     * 统计
     *
     * @return 数量
     */
    long count();

    /**
     * 统计
     *
     * @param predicate 条件
     * @param fieldName 字段
     * @return 数量
     */
    Double sum(String fieldName, Predicate<T> predicate);

    /**
     * 统计
     *
     * @param fieldName 字段
     * @return 数量
     */
    Double sum(String fieldName);

    /**
     * 统计
     *
     * @param predicate 条件
     * @param fieldName 字段
     * @return 数量
     */
    Double average(String fieldName, Predicate<T> predicate);

    /**
     * 统计
     *
     * @param fieldName 字段
     * @return 数量
     */
    Double average(String fieldName);

    /**
     * 统计
     *
     * @param predicate 条件
     * @param fieldName 字段
     * @return 数量
     */
    Double max(String fieldName, Predicate<T> predicate);

    /**
     * 统计
     *
     * @param fieldName 字段
     * @return 数量
     */
    Double max(String fieldName);

    /**
     * 统计
     *
     * @param predicate 条件
     * @param fieldName 字段
     * @return 数量
     */
    Double min(String fieldName, Predicate<T> predicate);

    /**
     * 统计
     *
     * @param fieldName 字段
     * @return 数量
     */
    Double min(String fieldName);
}
