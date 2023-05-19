package com.chua.datasource.support.schema;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.datasource.support.TableUtils;
import com.chua.datasource.support.adator.CalciteTable;
import com.chua.datasource.support.table.TablesawTable;
import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.Table;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Reflective Schema
 *
 * @author CH
 * @since 2021-11-10
 */
@Spi("tablesaw")
public class TablesawSchemaFactory extends AbstractCalciteSchemaFactory {


    public TablesawSchemaFactory(ConnectorMetadata connectorMetadata) {
        super(connectorMetadata);
    }

    @Override
    @SuppressWarnings("ALL")
    public Map<String, Table> getTable() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        builder.put(profile.getString("name"), new TablesawTable(profile,
                TableUtils.createColumn(profile.getType("mapping", Collections.emptyMap(), Map.class), profile.getObject("column"))));
        String directory = profile.getString("directory");
        List<CalciteTable> tableList = TableUtils.createTable(directory, "tablesaw");
        for (CalciteTable table : tableList) {
            String[] name = table.name();
            for (String s : name) {
                builder.put(s, table);
            }
        }
        return builder.build();
    }


}
