package com.chua.common.support.database.orm.type;


import com.chua.common.support.database.entity.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
public interface TypeHandler<T> {
    /**
     * 设置字段
     *
     * @param ps        处理器
     * @param i         索引位置
     * @param parameter 值
     * @param jdbcType  类型
     * @throws SQLException ex
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    /**
     * Gets the result.
     *
     * @param rs         the rs
     * @param columnName Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
     * @return the result
     * @throws SQLException the SQL exception
     */
    T getResult(ResultSet rs, String columnName) throws SQLException;

    /**
     * 获取结果
     *
     * @param rs          结果集
     * @param columnIndex 字段
     * @return 结果
     * @throws SQLException ex
     */
    T getResult(ResultSet rs, int columnIndex) throws SQLException;

    /**
     * 获取结果
     *
     * @param cs          结果集
     * @param columnIndex 字段
     * @return 结果
     * @throws SQLException ex
     */
    T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
