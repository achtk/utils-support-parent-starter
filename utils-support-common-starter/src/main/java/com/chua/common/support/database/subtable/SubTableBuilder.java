package com.chua.common.support.database.subtable;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分表
 *
 * @author CH
 */
@NoArgsConstructor
public final class SubTableBuilder {
    private String type;
    private final Properties properties = new Properties();

    private final List<LogicTable> logicTables = new LinkedList<>();
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    public SubTableBuilder(String type) {
        this.type = type;
    }

    /**
     * 初始化
     *
     * @param type 类型
     * @return this
     */
    public static SubTableBuilder newBuilder(String type) {
        return new SubTableBuilder(type);
    }

    /**
     * 初始化
     *
     * @return this
     */
    public static SubTableBuilder newBuilder() {
        return new SubTableBuilder();
    }

    /**
     * 设置环境
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    public SubTableBuilder env(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    /**
     * 逻辑表
     *
     * @param logicTable 逻辑表
     * @return this
     */
    public SubTableBuilder addLogicTable(LogicTable logicTable) {
        logicTables.add(logicTable);
        return this;
    }

    /**
     * 设置环境
     *
     * @param name       数据库名称
     * @param dataSource 数据库
     * @return this
     */
    public SubTableBuilder register(String name, DataSource dataSource) {
        dataSourceMap.put(name, dataSource);
        return this;
    }

    /**
     * 设置环境
     *
     * @param dataSource 数据库
     * @return this
     */
    public SubTableBuilder register(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            dataSourceMap.put(connection.getCatalog(), dataSource);
        } catch (SQLException e) {
            dataSourceMap.put("default", dataSource);
        }
        return this;
    }


    /**
     * 是否显示sql
     *
     * @return 是否显示sql
     */
    public SubTableBuilder sqlShow() {
        return env("sql.show", true);
    }

    /**
     * 构建
     *
     * @return 构建
     */
    public SubTableFactory build() {
        if (StringUtils.isNullOrEmpty(type)) {
            type = "sharding";
        }
        return ServiceProvider.of(SubTableFactory.class)
                .getNewExtension(type, properties, dataSourceMap, logicTables);
    }

}
