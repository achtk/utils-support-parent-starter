package com.chua.common.support.database.dialect;

/**
 * @author Administrator
 */
public class DialectWrapper implements DialectSensor {

    private Dialect dialect = Dialect.create("mysql");

    public DialectWrapper() {
    }

    public DialectWrapper(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * 把字段 dbField 转换为大写
     * @param builder sql builder
     * @param dbField 数据库字段
     */
    public void toUpperCase(StringBuilder builder, String dbField) {
        dialect.toUpperCase(builder, dbField);
    }

    /**
     * 是否支持 ilike 语法
     * @return 是否支持 ilike 语法
     * @since v3.7.0
     */
    public boolean hasLike() {
        return dialect.hasLike();
    }

    @Override
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect() {
        return dialect;
    }

}
