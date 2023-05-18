package com.chua.datasource.support.adator;

import java.util.List;

/**
 * 表适配器
 *
 * @author CH
 */
public interface TableAdaptor {

    /**
     * 获取表
     *
     * @param source 来源
     * @return 表
     */
    List<CalciteTable> createTable(Object source);
}
