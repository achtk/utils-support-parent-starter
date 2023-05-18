package com.chua.common.support.database.inquirer;

import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * sql
 *
 * @author CH
 */
public interface SqlInquirer extends Inquirer {
    /**
     * 执行command
     *
     * @param command command
     * @param args    参数
     * @return 结果
     * @throws Exception ex
     */
    int executeStatement(String command, Object... args) throws Exception;

    /**
     * 执行command
     *
     * @param command command
     * @param args    参数
     * @return 结果
     * @throws Exception ex
     */
    int executeUpdate(String command, Object... args) throws Exception;

    /**
     * 批处理
     *
     * @param sql    sql
     * @param params 参数
     * @return 结果
     */
    int[] batch(String sql, Object[][] params);

    /**
     * 更新数据
     *
     * @param sql    sql
     * @param params 参数
     * @return 结果
     */
    int update(String sql, Object[] params);

    /**
     * 插入数据
     *
     * @param sql      sql
     * @param params   参数
     * @param beanType 类型
     * @param <T>      类型
     * @return 结果
     */
    <T> T insert(String sql, Object[] params, Class<T> beanType);

    /**
     * 查询
     *
     * @param sql  sql
     * @param args 参数
     * @return 结果
     * @throws Exception ex
     */
    @SuppressWarnings("ALL")
    default List<Map<String, Object>> query(String sql, Object... args) throws Exception {
        List query = query(sql, args, LinkedHashMap.class);
        return query;
    }

    /**
     * 查询
     *
     * @param sql      sql
     * @param beanType 类型
     * @return 结果
     * @throws Exception ex
     */
    @SuppressWarnings("ALL")
    default <T> List<T> query(String sql, Class<T> beanType) throws Exception {
        return query(sql, new Object[0], beanType);
    }

    /**
     * 查询
     *
     * @param sql  sql
     * @param args 参数
     * @return 结果
     */
    @SneakyThrows
    default Map<String, Object> queryOne(String sql, Object... args) {
        List<Map<String, Object>> query = query(sql, args);
        return query.isEmpty() ? null : query.get(0);
    }

    /**
     * 查询
     *
     * @param sql      sql
     * @param args     参数
     * @param beanType 类型
     * @param <T>      类型
     * @return 结果
     */
    @SneakyThrows
    default <T> T queryOne(String sql, Object[] args, Class<T> beanType) {
        List<T> query = query(sql, args, beanType);
        return query.isEmpty() ? null : query.get(0);
    }

    /**
     * 查询
     *
     * @param sql      sql
     * @param args     参数
     * @param beanType 类型
     * @param <T>      类型
     * @return 结果
     */
    <T> List<T> query(String sql, Object[] args, Class<T> beanType);
}
