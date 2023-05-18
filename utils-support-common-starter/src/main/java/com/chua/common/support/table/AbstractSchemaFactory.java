package com.chua.common.support.table;

/**
 * @author Administrator
 */
public abstract class AbstractSchemaFactory<Schema> implements SchemaFactory<Schema> {

    public static final String SCHEMA = "schema";

    protected ConnectorMetadata connectorMetadata;

    public AbstractSchemaFactory(ConnectorMetadata connectorMetadata) {
        this.connectorMetadata = connectorMetadata;
    }


}
