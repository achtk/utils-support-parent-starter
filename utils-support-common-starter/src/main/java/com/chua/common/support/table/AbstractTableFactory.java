package com.chua.common.support.table;

import java.util.Map;
import java.util.Properties;

/**
 * 表工厂
 *
 * @author CH
 */
public abstract class AbstractTableFactory<Table> implements TableFactory<Table> {

    private Map<String, Table> tableMap;

    @Override
    public Map<String, Table> getTables() {
        if (tableMap == null) {
            tableMap = createTableMap();
        }
        return tableMap;
    }


    @Override
    public Properties comments() {
        return null;
    }

    /**
     * 表
     *
     * @return 表
     */
    public abstract Map<String, Table> createTableMap();
}
