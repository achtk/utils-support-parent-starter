package com.chua.datasource.support.datasource;

import org.apache.calcite.sql.dialect.MysqlSqlDialect;

/**
 * mysql
 *
 * @author CH
 * @since 2022-03-16
 */
public class MysqlFixSqlDialect extends MysqlSqlDialect {
    /**
     * Creates a MysqlSqlDialect.
     *
     * @param context context
     */
    public MysqlFixSqlDialect(Context context) {
        super(context);
    }

    /**
     * 下发sql时，去除编码前缀,直接拼接原字符。 见用例testUtf8Sql
     */
    @Override
    public void quoteStringLiteral(StringBuilder buf, String charsetName, String val) {
        buf.append(this.literalQuoteString);
        buf.append(val.replace(this.literalEndQuoteString, this.literalEscapedQuote));
        buf.append(this.literalEndQuoteString);
    }
}
