package com.chua.common.support.database.structure;

import com.chua.common.support.database.structure.StructureValue;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库数据结构
 *
 * @author CH
 */
public class DataSourceDataStructure implements DataStructure {

    private DataSource dataSource;

    public DataSourceDataStructure(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createStructure(StructureValue structure) {
        String schema = structure.getSchema();
        try (Connection connection = dataSource.getConnection()) {
            String catalog = schema;
            if (StringUtils.isNullOrEmpty(schema)) {
                catalog = connection.getCatalog();
            }
            TableStructure tableStructure = ServiceProvider.of(TableStructure.class).getNewExtension(connection.getMetaData().getDatabaseProductName(), dataSource);
            tableStructure.analysis(catalog, structure);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
