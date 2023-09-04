package com.chua.elasticsearch.support.sql.table;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.datasource.support.schema.AbstractCalciteSchemaFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.calcite.adapter.elasticsearch.ElasticsearchSchema;
import org.apache.calcite.adapter.elasticsearch.ElasticsearchSchemaFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.elasticsearch.client.RestClient;

import java.util.Map;

/**
 * es
 *
 * @author CH
 * @since 2022-03-14
 */
@Spi("elasticsearch")
public class TableElasticsearchSchemaFactory extends AbstractCalciteSchemaFactory {
    private Schema schema;

    public TableElasticsearchSchemaFactory(ConnectorMetadata connectorMetadata) {
        super(connectorMetadata);
    }

    @Override
    public Schema getSchema() {
        if (null == schema) {

            RestClient restClient = (RestClient) connectorMetadata.getConfig().getObject("restClient", null);
            if (null != restClient) {
                ObjectMapper objectMapper = (ObjectMapper) connectorMetadata.getConfig().getObject("objectMapper", new ObjectMapper());
                Object index = connectorMetadata.getConfig().getObject("index");
                return new ElasticsearchSchema(restClient,
                        objectMapper,
                        index.toString()
                );
            }
            ElasticsearchSchemaFactory factory = new ElasticsearchSchemaFactory();
            return (schema = factory.create(
                    connectorMetadata.get("schemaPlus", SchemaPlus.class),
                    connectorMetadata.get("schema", String.class),
                    (Map<String, Object>) connectorMetadata.getConfig().getObject("operator")));
        }
        return schema;
    }

    @Override
    protected Map<String, Table> getTable() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        for (String tableName : getSchema().getTableNames()) {
            builder.put(tableName, getSchema().getTable(tableName));
        }
        return builder.build();
    }

}
