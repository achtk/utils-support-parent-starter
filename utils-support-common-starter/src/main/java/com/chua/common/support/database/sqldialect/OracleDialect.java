package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.utils.ClassUtils;

/**
 * oracle
 *
 * @author CH
 */
@Spi("oracle")
public class OracleDialect extends MysqlDialect {

    static {
        JAVA_JDBC.put(String.class, JdbcType.NVARCHAR);
    }

    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        limit = (offset >= 1) ? (offset + limit) : limit;
        String sql = "SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( " +
                originalSql + " ) TMP WHERE ROWNUM <= ?) WHERE ROW_ID > ?";
        return new SqlModel(sql, limit, offset);
    }

    @Override
    public String driverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String createCreateSql(Metadata<?> metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommonConstant.SYMBOL_CREATE_TABLE)
                .append(" `").append(metadata.getTable()).append("` (");

        for (Column column : metadata.getColumn()) {
            sb.append(createColumn(column));
            sb.append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(" )");
        return sb.toString();
    }

    @Override
    public Object getHibernateDialect() {
        return ClassUtils.forObject("org.hibernate.dialect.Oracle9iDialect");
    }


}
