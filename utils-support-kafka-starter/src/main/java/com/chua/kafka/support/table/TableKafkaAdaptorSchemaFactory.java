package com.chua.kafka.support.table;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.datasource.support.schema.AbstractCalciteSchemaFactory;
import org.apache.calcite.adapter.kafka.KafkaStreamTable;
import org.apache.calcite.adapter.kafka.KafkaTableFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka
 *
 * @author CH
 * @since 2022-03-14
 */
@Spi("kafka")
public class TableKafkaAdaptorSchemaFactory extends AbstractCalciteSchemaFactory {

    public TableKafkaAdaptorSchemaFactory(ConnectorMetadata connectorMetadata) {
        super(connectorMetadata);
    }

    @Override
    protected Map<String, Table> getTable() {
        Map<String, Table> rs = new HashMap<>();
        KafkaTableFactory kafkaTableFactory = new KafkaTableFactory();
        Map<String, Object> op = analysisOperand(connectorMetadata);
        KafkaStreamTable kafkaStreamTable = kafkaTableFactory.create(
                    connectorMetadata.get("schemaPlus", SchemaPlus.class)
                , profile.getString("name"), op, null);
        rs.put(profile.getString("name"), kafkaStreamTable);
        return rs;
    }


    /**
     * "tables": [
     * {
     * "name": "TABLE_NAME",
     * "type": "custom",
     * "factory": "org.apache.calcite.adapter.kafka.KafkaTableFactory",
     * "row.converter": "com.example.CustKafkaRowConverter",
     * "operand": {
     * "bootstrap.servers": "host1:port,host2:port",
     * "topic.name": "kafka.topic.name",
     * "consumer.params": {
     * "key.deserializer": "org.apache.kafka.common.serialization.ByteArrayDeserializer",
     * "value.deserializer": "org.apache.kafka.common.serialization.ByteArrayDeserializer"
     * }
     * }
     * }
     *
     * @param table kafka
     * @return opera
     */
    private Map<String, Object> analysisOperand(ConnectorMetadata table) {
        Map<String, Object> rs = new HashMap<>();
        rs.put("bootstrap.servers", table.get("host", String.class));
        rs.put("topic.name", table.get("topic", String.class));
        Map<String, String> params = new HashMap<>(2);
        params.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        params.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        rs.put("consumer.params", params);

        return rs;
    }
}
