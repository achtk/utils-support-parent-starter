package com.chua.common.support.table;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;

import static com.chua.common.support.table.ConnectorMetadata.DIRECTORY;

/**
 * 表工厂
 *
 * @author CH
 */
public interface ConnectorFactory extends AutoCloseable {

    /**
     * 注册适配器
     *
     * @param connectorMetadata 类型
     */
    void register(ConnectorMetadata connectorMetadata);

    /**
     * 获取连接池
     *
     * @return 连接池
     */
    DataSource getDataSource();

    /**
     * 获取所有表
     *
     * @return 表
     */
    Set<String> tableNames();

    /**
     * 注册数据源
     *
     * @param schemaName 名称
     * @param dataSource 数据源
     */
    default void register(String schemaName, DataSource dataSource) {
        register(ConnectorMetadata.create("datasource")
                .addParam("datasource", dataSource)
                .addParam("schema", schemaName)
                );
    }


    /**
     * 注册数据源
     *
     * @param tableName 名称
     * @param type      类型
     * @param data      数据
     */
    default void register(String tableName, Class<?> type, List<?> data) {
        register(ConnectorMetadata.create("mem")
                .name(tableName)
                .addParam("type", type)
                .addParam("data", data));
    }


    /**
     * 注册数据源
     *
     * @param directory 目录
     */
    default void register(String directory) {
        register(new File(directory));
    }

    /**
     * 注册数据源
     *
     * @param directory 目录
     */
    default void register(File directory) {
        register(ConnectorMetadata
                .create("file")
                .addParam(DIRECTORY, directory.getAbsolutePath())
                .addParam("mega", true));
    }

}
