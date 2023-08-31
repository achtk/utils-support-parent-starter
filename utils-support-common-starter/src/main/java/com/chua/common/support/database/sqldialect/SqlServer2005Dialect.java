package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.SqlModel;
import com.chua.common.support.utils.StringUtils;

/**
 * sql server 2005
 *
 * @author CH
 */
@Spi("sqlserver2005")
public class SqlServer2005Dialect extends OracleDialect {
    private static final String SELECT = "select";
    private static final String SD = "select distinct";

    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(originalSql);
        String distinctStr = CommonConstant.EMPTY;

        String loweredString = originalSql.toLowerCase();
        String sqlPartString = originalSql;
        if (loweredString.trim().startsWith(SELECT)) {
            int index = 6;
            if (loweredString.startsWith(SD)) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);

        // if no ORDER BY is specified use fake ORDER BY field to avoid errors
        if (StringUtils.isBlank(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }
        long firstParam = offset + 1;
        long secondParam = offset + limit;
        String sql = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " +
                " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder +
                ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
                //FIX#299：原因：mysql中limit 10(offset,size) 是从第10开始（不包含10）,；而这里用的BETWEEN是两边都包含，所以改为offset+1
                firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        return new SqlModel(sql);
    }

    @Override
    public String driverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    private static String getOrderByPart(String sql) {
        String loweredString = sql.toLowerCase();
        int orderByIndex = loweredString.lastIndexOf("order by");
        if (orderByIndex != -1) {
            return sql.substring(orderByIndex);
        } else {
            return CommonConstant.EMPTY;
        }
    }

    @Override
    public String getProtocol() {
        return "SQLServer2005";
    }

}
