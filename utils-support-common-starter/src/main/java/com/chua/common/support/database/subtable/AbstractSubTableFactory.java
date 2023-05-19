package com.chua.common.support.database.subtable;

import com.chua.common.support.function.InitializingAware;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 分表
 *
 * @author CH
 */
public abstract class AbstractSubTableFactory implements SubTableFactory, InitializingAware {

    protected final Properties properties;
    protected final Map<String, DataSource> dataSourceMap;
    protected List<LogicTable> logicTables;

    public AbstractSubTableFactory(Properties properties, Map<String, DataSource> dataSourceMap, List<LogicTable> logicTables) {
        this.properties = properties;
        this.dataSourceMap = dataSourceMap;
        this.logicTables = logicTables;
    }
}
