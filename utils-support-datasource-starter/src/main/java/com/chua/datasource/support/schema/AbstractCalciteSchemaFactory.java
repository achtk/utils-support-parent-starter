package com.chua.datasource.support.schema;

import com.chua.common.support.context.factory.ApplicationContext;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.common.support.table.SchemaFactory;
import com.chua.datasource.support.TableUtils;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.Map;
import java.util.Properties;

/**
 * @author Administrator
 */
public abstract class AbstractCalciteSchemaFactory extends AbstractSchema implements SchemaFactory<Schema> {


    protected final Profile configureAttributes;
    private Map<String, Table> tableMap;
    protected final ConnectorMetadata connectorMetadata;

    public AbstractCalciteSchemaFactory(ConnectorMetadata connectorMetadata) {
        super();
        this.connectorMetadata = connectorMetadata;
        this.configureAttributes = connectorMetadata.getConfig();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return TableUtils.getContext();
    }

    @Override
    public String getSchemaName() {
        return configureAttributes.getString("name", connectorMetadata.hashCode() + "");
    }

    /**
     * 获取表
     *
     * @return 表
     */
    abstract Map<String, Table> getTable();

    @Override
    protected Map<String, Table> getTableMap() {
        if (null == tableMap) {
            tableMap = getTable();
        }

        return tableMap;
    }

    @Override
    public Properties comments() {
        return null;
    }

    @Override
    public Schema getSchema() {
        return this;
    }

    @Override
    public Map getTables() {
        return getTableMap();
    }
}
