package com.chua.common.support.database.dialect;

/**
 * 方言感知器
 * @author Troy.Zhou @ 2022-01-18
 * @since v3.3.0
 */
public interface DialectSensor {
    /**
     * 方言
     * @param dialect 方言
     */
    void setDialect(Dialect dialect);

}
