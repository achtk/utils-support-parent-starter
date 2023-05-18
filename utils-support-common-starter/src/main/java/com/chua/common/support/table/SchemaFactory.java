package com.chua.common.support.table;

import com.chua.common.support.context.factory.ApplicationContext;

/**
 * 表工厂
 *
 * @author CH
 */
public interface SchemaFactory<Schema> extends TableFactory {

    /**
     * schema
     *
     * @return schema
     */
    Schema getSchema();

    /**
     * name
     *
     * @return name
     */
    String getSchemaName();

    /**
     * 上下文
     *
     * @return 上下文
     */
    ApplicationContext getApplicationContext();

}
