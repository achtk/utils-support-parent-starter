package com.chua.common.support.table;

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

}
