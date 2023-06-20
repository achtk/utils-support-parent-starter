package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.SqlModel;

/**
 * oracle
 *
 * @author CH
 */
@Spi("oracle")
public class Oracle12cDialect extends OracleDialect {

    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        String sql = originalSql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return new SqlModel(sql, offset, limit);
    }

    @Override
    public String driverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }


}
