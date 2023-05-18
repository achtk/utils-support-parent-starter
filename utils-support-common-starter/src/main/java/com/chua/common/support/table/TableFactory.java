package com.chua.common.support.table;

import java.util.Map;
import java.util.Properties;

/**
 * 表工厂
 * @author CH
 */
public interface TableFactory<Table> {
    /**
     * 说明
     * @return 说明
     */
    Properties comments();

    /**
     * 表
     * @return 表
     */
    Map<String, Table> getTables();
}
