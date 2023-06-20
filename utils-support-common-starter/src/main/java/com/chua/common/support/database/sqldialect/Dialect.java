
package com.chua.common.support.database.sqldialect;

import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 数据方言
 *
 * @author CH
 * @since 1.7.0
 */
public interface Dialect {
    static Dialect guessDialect(DataSource ds) {
        try (Connection connection = ds.getConnection()) {
            String driverName = connection.getMetaData().getDriverName();
            Map<String, Dialect> stringDialectMap = ServiceProvider.of(Dialect.class).list();
            for (Map.Entry<String, Dialect> entry : stringDialectMap.entrySet()) {
                if (entry.getValue().driverClassName().equals(driverName)) {
                    return entry.getValue();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 生成分页查询语句
     *
     * @param originalSql 语句
     * @param offset      当前位置
     * @param limit       限制数量
     * @return 结果
     */
    SqlModel formatPageSql(String originalSql, int offset, int limit);

    /**
     * 驱动
     *
     * @return 驱动
     */
    String driverClassName();

    /**
     * 获取表的所有字段
     *
     * @param connection 连接
     * @param table      表
     * @return 所有字段
     */
    default List<Column> getAllColumn(Connection connection, String table) {
        List<Column> rs;
        try {
            connection.setAutoCommit(true);
            rs = new LinkedList<>();
            try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, table, null)) {
                while (resultSet.next()) {
                    Column column = new Column();
                    column.setTableName(table);
                    column.setName(resultSet.getString("COLUMN_NAME"));
                    column.setLength(resultSet.getInt("COLUMN_SIZE"));
                    column.setJdbcType(JdbcType.valueOf(resultSet.getString("TYPE_NAME")));
                    column.setNullable(resultSet.getBoolean("NULLABLE"));

                    rs.add(column);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IoUtils.closeQuietly(connection);
        }

        return rs;
    }

    /**
     * alter sql
     *
     * @param table  表名
     * @param column 字段
     * @return sql
     */
    default String createAlterColumn(String table, Column column) {
        StringBuilder sb = new StringBuilder("ALTER TABLE `").append(table).append("`");
        sb.append(" ADD `").append(column.getName()).append("` ").append(column.getJdbcType().name());
        appColumnSuffix(column, sb);
        return sb.toString();
    }

    default String createColumn(Column column) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(column.getName()).append("`");
        sb.append(" ").append(column.getJdbcType().name()).append("");

        appColumnSuffix(column, sb);

        return sb.toString();
    }

    /**
     * 后缀添加
     *
     * @param column 字段
     * @param sb     sql
     */
    default void appColumnSuffix(Column column, StringBuilder sb) {
        if (column.getLength() > 0) {
            sb.append("(").append(column.getLength()).append(")");
        }

        if (!column.isNullable()) {
            sb.append(" NOT NULL ");
        }

        if (StringUtils.isNotBlank(column.getDefaultValue())) {
            sb.append(" DEFAULT ").append(column.getDefaultValue());
        }


        if (StringUtils.isNotBlank(column.getComment())) {
            sb.append(" COMMENT '").append(column.getComment()).append("'");
        }
    }

    /**
     * 建表语句
     *
     * @param metadata 媒体数据
     * @return sql
     */
    String createCreateSql(Metadata<?> metadata);

    /**
     * 删除语句
     *
     * @param metadata 媒体数据
     * @return sql
     */
    default String createDropSql(Metadata<?> metadata) {
        return "DROP TABLE `" + metadata.getTable() + "`";
    }
}


