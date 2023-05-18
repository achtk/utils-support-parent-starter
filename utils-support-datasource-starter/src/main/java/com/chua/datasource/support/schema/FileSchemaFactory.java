package com.chua.datasource.support.schema;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.datasource.support.table.FileTable;
import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.Table;

import java.util.Map;

/**
 * Reflective Schema
 *
 * @author CH
 * @since 2021-11-10
 */
@Spi("file")
public class FileSchemaFactory extends AbstractCalciteSchemaFactory {


    public FileSchemaFactory(ConnectorMetadata connectorMetadata) {
        super(connectorMetadata);
    }

    @Override
    @SuppressWarnings("ALL")
    Map<String, Table> getTable() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        builder.put(configureAttributes.getString("name"), new FileTable(configureAttributes));
        return builder.build();
    }


}
