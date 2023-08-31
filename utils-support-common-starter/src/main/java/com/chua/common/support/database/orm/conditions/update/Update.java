
package com.chua.common.support.database.orm.conditions.update;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2018-12-12
 */
public interface Update<Children, R> extends Serializable {

    /**
     * 集
     * ignore
     *
     * @param column 列
     * @param value  瓦尔
     * @return {@link Children}
     */
    default Children set(R column, Object value) {
        return set(true, column, value);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param value     值
     * @return children
     */
    default Children set(boolean condition, R column, Object value) {
        return set(condition, column, value, null);
    }

    /**
     * 集
     * ignore
     *
     * @param column  列
     * @param mapping 映射
     * @param value   价值
     * @return {@link Children}
     */
    default Children set(R column, Object value, String mapping) {
        return set(true, column, value, mapping);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param value     值
     * @param mapping   例: javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler
     * @return children
     */
    Children set(boolean condition, R column, Object value, String mapping);

    /**
     * 设置sql
     * ignore
     *
     * @param sql sql
     * @return {@link Children}
     */
    default Children setSql(String sql) {
        return setSql(true, sql);
    }

    /**
     * 设置sql
     * 设置 更新 SQL 的 SET 片段
     *
     * @param sql       set sql
     * @param condition 条件
     * @return children
     */
    Children setSql(boolean condition, String sql);

    /**
     * 获取sql设置
     * 获取 更新 SQL 的 SET 片段
     *
     * @return {@link String}
     */
    String getSqlSet();
}
